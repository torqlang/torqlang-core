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
import org.torqlang.core.util.GetStackTrace;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

import static org.torqlang.core.local.OnMessageResult.NOT_FINISHED;

public abstract class AbstractActor implements ActorRef {

    /*
     * Concurrency invariants:
     *     1. All access to the mailbox value must be synchronized on mailboxLock
     *     2. All access to the state value must be synchronized on mailboxLock
     */

    private final Address address;
    private final Executor executor;
    private final Dispatcher dispatcher = new Dispatcher();
    private final Logger logger;
    private final Mailbox mailbox;
    private final Object mailboxLock = new Object();

    private volatile State state = State.WAITING;

    protected AbstractActor(Address address, Mailbox mailbox, Executor executor, Logger logger) {
        this.address = address;
        this.mailbox = mailbox;
        this.executor = executor;
        this.logger = logger;
    }

    public final Address address() {
        return address;
    }

    protected boolean isExecutable(Mailbox mailbox) {
        return !mailbox.isEmpty();
    }

    protected void logError(String message) {
        logger.error(address().toString(), message);
    }

    protected void logInfo(String message) {
        logger.info(address().toString(), message);
    }

    protected final Logger logger() {
        return logger;
    }

    protected abstract OnMessageResult onMessage(Envelope[] next);

    protected void onReceivedAfterFailed(Envelope envelope) {
        logger.error(address.toString(), String.format("Message received after FAILED: %s", envelope));
    }

    protected void onReceivedAfterSuccessful(Envelope envelope) {
        logger.error(address.toString(), String.format("Message received after SUCCESSFUL: %s", envelope));
    }

    protected void onRejectedByExecutor(RejectedExecutionException exc) {
        logger.error(address.toString(), String.format("Actor rejected by executor:\n%s",
            GetStackTrace.apply(exc, true)));
    }

    /*
     * INVARIANT: The mailbox is locked during this call so that implementations can empty the mailbox while responding
     * to pending requests with the error.
     */
    protected void onUnhandledError(Mailbox mailbox, Throwable throwable) {
        logger.error(address.toString(), String.format("Unhandled error\n" +
            GetStackTrace.apply(throwable, true)));
    }

    protected OnMessageResult onUnrecognizedMessage(Envelope envelope) {
        logger.error(address.toString(), String.format("Unrecognized message: %s", envelope));
        return NOT_FINISHED;
    }

    protected Envelope[] selectNext(Mailbox mailbox) {
        return new Envelope[]{mailbox.removeNext()};
    }

    @Override
    public final void send(Envelope envelope) {
        synchronized (mailboxLock) {
            if (state == State.FAILED) {
                onReceivedAfterFailed(envelope);
            } else if (state == State.SUCCESSFUL) {
                onReceivedAfterSuccessful(envelope);
            } else {
                mailbox.add(envelope);
                // If we are ACTIVE, SCHEDULED, or WAITING-not-executable, there is nothing to do. However, if we are
                // WAITING-executable, we must schedule for execution.
                if (state == State.WAITING && isExecutable(mailbox)) {
                    dispatcher.schedule();
                }
            }
        }
    }

    public final State state() {
        synchronized (mailboxLock) {
            return state;
        }
    }

    public enum State {
        WAITING,        // actor is NOT executable (mailbox is empty or no selectable message in mailbox)
        SCHEDULED,      // actor is executable and actor is queued for execution
        ACTIVE,         // actor is currently processing a message (messages may arrive during processing)
        SUCCESSFUL,     // actor finished normally and will no longer accept mail
        FAILED          // actor finished abnormally and will no longer accept mail
    }

    private final class Dispatcher implements Runnable {

        @Override
        public final void run() {
            // Because we have just been invoked by the executor, we know we are in the SCHEDULED state.
            try {
                Envelope[] next;
                synchronized (mailboxLock) {
                    // A message can be selected because we were previously scheduled as "executable", and now we
                    // are running. We must transition from SCHEDULED to ACTIVE as soon as we select a message from
                    // the mailbox.
                    next = selectNext(mailbox);
                    state = State.ACTIVE;
                }
                // CRITICAL: Do not synchronize on the mailboxLock during onMessage(). Releasing the lock allows
                // messages to be received while processing the current message.
                OnMessageResult result = onMessage(next);
                synchronized (mailboxLock) {
                    if (result == OnMessageResult.FINISHED) {
                        state = State.SUCCESSFUL;
                        return;
                    }
                    // We just completed processing of a single message, and we are not finished. We must transition
                    // from ACTIVE to either SCHEDULED or WAITING.
                    if (isExecutable(mailbox)) {
                        schedule();
                    } else {
                        state = State.WAITING;
                    }
                }
            } catch (Throwable throwable) {
                synchronized (mailboxLock) {
                    // We have just been interrupted by an unhandled error. We must transition from ACTIVE to FAILED.
                    state = State.FAILED;
                    onUnhandledError(mailbox, throwable);
                }
            }
        }

        /*
         * Must be called from within a "synchronized {...}" block
         */
        private void schedule() {
            try {
                state = State.SCHEDULED;
                executor.execute(this);
            } catch (RejectedExecutionException exc) {
                state = State.FAILED;
                onRejectedByExecutor(exc);
            }
        }
    }

}
