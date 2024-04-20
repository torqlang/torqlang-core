/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.actor.ActorRef;
import org.torqlang.core.actor.Address;
import org.torqlang.core.actor.Envelope;
import org.torqlang.core.klvm.*;

import java.util.List;
import java.util.concurrent.*;

import static org.torqlang.core.local.ActorSystem.createResponse;

/*
 * A Timer generates a stream of ticks as `<period 1>, <tick 1>, ..., <period n>, <tick n>` where `<period>` is a delay
 * and '<tick 1>' through `<tick n>` are the tick events requested.
 *
 * A timer can be configured with a period and a time unit:
 *     var timer_cfg = Timer.cfg(1, 'seconds')
 *
 * Subsequently, a timer is spawned as a publisher, such as in the following:
 *     var timer_pub = spawn(timer_cfg)
 *
 * Once spawned, a timer can be used as a stream. Consider this code snippet:
 *     var tick_count = Cell.new(0)
 *     var timer_stream = Stream.new(timer_pub, 'request'#{'ticks': 5})
 *     for tick in Iter.new(timer_stream) do
 *         tick_count := @tick_count + 1
 *     end
 *     @tick_count
 *
 * A timer can only be used by one requester (subscriber) at a time.
 */
final class TimerMod {

    public static final Ident TIMER_IDENT = Ident.create("Timer");
    private static final int TIMER_CFG_CTOR_ARG_COUNT = 3;
    private static final CompleteProc TIMER_CFG_CTOR = TimerMod::timerCfgCtor;
    public static final CompleteRec TIMER_ACTOR = createTimerActor();

    private static CompleteRec createTimerActor() {
        return CompleteRec.singleton(Str.of("cfg"), TIMER_CFG_CTOR);
    }

    private static void timerCfgCtor(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        if (ys.size() != TIMER_CFG_CTOR_ARG_COUNT) {
            throw new InvalidArgCountError(TIMER_CFG_CTOR_ARG_COUNT, ys, "TimerCfgCtor");
        }
        Num period = (Num) ys.get(0).resolveValue(env);
        Str timeUnit = (Str) ys.get(1).resolveValue(env);
        TimerCfg config = new TimerCfg(period, timeUnit);
        ys.get(2).resolveValueOrVar(env).bindToValue(config, null);
    }

    private static final class Timer extends AbstractActor {

        public static final Str TICKS_FEAT = Str.of("ticks");

        public static final ScheduledThreadPoolExecutor SCHEDULED_EXECUTOR =
            new ScheduledThreadPoolExecutor(2, Timer::newTimerThread);

        private static final CompleteRec EOF_RECORD = Rec.completeRecBuilder()
            .setLabel(Eof.SINGLETON)
            .addField(Str.of("more"), Bool.FALSE)
            .build();

        private static final Object TIMER_CALLBACK = new Object();
        private final Num periodNum;
        private final Str timeUnitStr;
        private Envelope activeRequest;
        private CompleteRec activeMessage;
        private int currentTicks;
        private int requestedTicks;
        private ScheduledFuture<?> scheduledFuture;
        private boolean trace;

        public Timer(Address address, Mailbox mailbox, Executor executor, Logger logger, boolean trace, Num periodNum, Str timeUnitStr) {
            super(address, mailbox, executor, logger);
            this.trace = trace;
            this.periodNum = periodNum;
            this.timeUnitStr = timeUnitStr;
            if (trace) {
                logInfo("Timer created");
            }
        }

        private static Thread newTimerThread(Runnable r) {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        }

        @Override
        protected OnMessageResult onMessage(Envelope[] next) {
            Envelope envelope = next[0];
            try {
                if (envelope.isRequest()) {
                    return onTimerRequest(envelope);
                } else if (envelope.isResponse()) {
                    return onTimerCallback(envelope);
                } else {
                    throw new IllegalArgumentException("Unrecognized message: " + envelope);
                }
            } catch (Throwable throwable) {
                FailedValue failedValue = FailedValue.create(address().toString(), throwable);
                if (envelope.requester() != null) {
                    envelope.requester().send(createResponse(failedValue, envelope.requestId()));
                } else {
                    logError("Timer error:\n" + failedValue.toDetailsString());
                }
            }
            return OnMessageResult.NOT_FINISHED;
        }

        private OnMessageResult onTimerCallback(Envelope envelope) {
            if (!(envelope.message() == TIMER_CALLBACK)) {
                throw new IllegalArgumentException("Invalid timer callback: " + envelope);
            }
            if (activeRequest == null) {
                if (trace) {
                    logInfo("Timer received callback after it completed: " + envelope.requestId());
                }
                return OnMessageResult.NOT_FINISHED;
            }
            long now = System.currentTimeMillis();
            if (currentTicks >= requestedTicks) {
                scheduledFuture.cancel(true);
                activeRequest.requester().send(createResponse(EOF_RECORD, activeRequest.requestId()));
                activeRequest = null;
                if (trace) {
                    logInfo("Timer is now complete: " + envelope.requestId());
                }
            } else {
                currentTicks++;
                activeRequest.requester().send(createResponse(CompleteTuple.singleton(Int64.of(now)),
                    activeRequest.requestId()));
            }
            return OnMessageResult.NOT_FINISHED;
        }

        private OnMessageResult onTimerRequest(Envelope envelope) {
            if (trace) {
                logInfo("Timer received a new request");
            }
            if (activeRequest != null) {
                throw new IllegalStateException("Timer is already active: " + envelope);
            }
            activeRequest = envelope;
            activeMessage = validateMessage();
            requestedTicks = validateTicks();
            TimeUnit timeUnit = validateTimeUnit();
            scheduledFuture = SCHEDULED_EXECUTOR.scheduleAtFixedRate(() ->
                    this.send(createResponse(TIMER_CALLBACK, activeRequest.requestId())),
                periodNum.longValue(), periodNum.longValue(), timeUnit);
            return OnMessageResult.NOT_FINISHED;
        }

        private CompleteRec validateMessage() {
            if (!(activeRequest.message() instanceof CompleteRec completeRec)) {
                throw new IllegalArgumentException("Invalid timer request: " + activeRequest);
            }
            if (!completeRec.label().equals(Str.of("request"))) {
                throw new IllegalArgumentException("Invalid timer request: " + activeRequest);
            }
            if (completeRec.fieldCount() != 1) {
                throw new IllegalArgumentException("Timer request must contain a 'ticks' feature");
            }
            return completeRec;
        }

        private int validateTicks() {
            Complete ticks = activeMessage.findValue(TICKS_FEAT);
            if (!(ticks instanceof Int64 int32)) {
                throw new IllegalArgumentException("Not an Int32");
            }
            return int32.intValue();
        }

        private TimeUnit validateTimeUnit() {
            TimeUnit timeUnit;
            if (timeUnitStr.value.equalsIgnoreCase("microseconds")) {
                timeUnit = TimeUnit.MICROSECONDS;
            } else if (timeUnitStr.value.equalsIgnoreCase("milliseconds")) {
                timeUnit = TimeUnit.MILLISECONDS;
            } else if (timeUnitStr.value.equalsIgnoreCase("seconds")) {
                timeUnit = TimeUnit.SECONDS;
            } else {
                throw new IllegalArgumentException("Not 'microseconds', 'milliseconds', or 'seconds'");
            }
            return timeUnit;
        }
    }

    private static final class TimerCfg extends OpaqueValue implements NativeActorCfg {
        final Num periodNum;
        final Str timeUnitStr;

        TimerCfg(Num periodNum, Str timeUnitStr) {
            this.periodNum = periodNum;
            this.timeUnitStr = timeUnitStr;
        }

        @Override
        public final ActorRef spawn(Address address, Mailbox mailbox, Executor executor, Logger logger, boolean trace) {
            return new Timer(address, mailbox, executor, logger, trace, periodNum, timeUnitStr);
        }
    }

}
