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
import org.torqlang.core.klvm.CompleteRec;
import org.torqlang.core.klvm.PartialField;
import org.torqlang.core.klvm.Rec;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class ActorSystem {

    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService COMPUTATION_EXECUTOR = Executors.newFixedThreadPool(Math.max(4, AVAILABLE_PROCESSORS));

    public static ActorBuilderInit actorBuilder() {
        return new ActorBuilder();
    }

    public static CompleteRec compileActorForImport(String source) throws Exception {
        Rec actorRec = ActorSystem.actorBuilder()
            .setSource(source)
            .createActorRec()
            .actorRec();
        actorRec.checkDetermined();
        PartialField actorField = (PartialField) actorRec.fieldAt(0);
        return Rec.completeRecBuilder()
            .addField(actorField.feature, actorField.value.checkComplete())
            .build();
    }

    public static Executor computationExecutor() {
        return COMPUTATION_EXECUTOR;
    }

    public static Address createAddress(String path) {
        return LocalAddress.create(path);
    }

    static LocalAddress createAddress(Address parentAddress, String path) {
        return LocalAddress.create((LocalAddress) parentAddress, path);
    }

    public static Envelope createControlNotify(Object message) {
        return new LocalEnvelope(true, message, null, null);
    }

    public static Envelope createControlRequest(Object message, ActorRef requester, Object requestId) {
        return new LocalEnvelope(true, message, requester, requestId);
    }

    public static Envelope createControlResponse(Object message, Object requestId) {
        return new LocalEnvelope(true, message, null, requestId);
    }

    public static Logger createLogger() {
        return ConsoleLogger.SINGLETON;
    }

    public static LinkedListMailbox createMailbox() {
        return new LinkedListMailbox(EnvelopeComparator.SINGLETON);
    }

    public static Envelope createNotify(Object message) {
        return new LocalEnvelope(false, message, null, null);
    }

    public static Envelope createRequest(Object message, ActorRef requester, Object requestId) {
        return new LocalEnvelope(false, message, requester, requestId);
    }

    public static Envelope createResponse(Object message, Object requestId) {
        return new LocalEnvelope(false, message, null, requestId);
    }

    public static void shutdownAndAwait(long millis) throws InterruptedException {
        COMPUTATION_EXECUTOR.shutdown();
        if (!COMPUTATION_EXECUTOR.awaitTermination(millis, TimeUnit.MILLISECONDS)) {
            throw new IllegalStateException("Time expired awaiting termination");
        }
    }

}
