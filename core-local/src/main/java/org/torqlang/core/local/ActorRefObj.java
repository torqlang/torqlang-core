/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.*;

import java.util.List;

import static org.torqlang.core.local.Envelope.createNotify;
import static org.torqlang.core.local.Envelope.createRequest;

public final class ActorRefObj implements CompleteObj {

    private final ActorRef referent;

    private final ObjProcBinding<ActorRefObj> PROC_ASK = new ObjProcBinding<>(this, ActorRefObj::objAsk);
    private final ObjProcBinding<ActorRefObj> PROC_TELL = new ObjProcBinding<>(this, ActorRefObj::objTell);

    public ActorRefObj(ActorRef referent) {
        this.referent = referent;
    }

    private static void objAsk(ActorRefObj obj, List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        final int expectedCount = 2;
        if (ys.size() != expectedCount) {
            throw new InvalidArgCountError(expectedCount, ys, "ActorRefObj.ask");
        }
        Value candidateMessage = ys.get(0).resolveValue(env);
        // This procedure will be suspended if 'checkComplete()' throws WaitException
        Complete message = candidateMessage.checkComplete();
        ValueOrVar responseTarget = ys.get(1).resolveValueOrVar(env);
        ActorRef owner = machine.owner();
        if (obj.referent == owner) {
            throw new SelfRefAskError(machine.current());
        }
        obj.referent.send(createRequest(message, owner, new ValueOrVarRef(responseTarget)));
    }

    private static void objTell(ActorRefObj obj, List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        final int expectedCount = 1;
        if (ys.size() != expectedCount) {
            throw new InvalidArgCountError(expectedCount, ys, "ActorRefObj.tell");
        }
        Value candidateMessage = ys.get(0).resolveValue(env);
        // This procedure will be suspended if 'checkComplete()' throws WaitException
        Complete message = candidateMessage.checkComplete();
        obj.referent.send(createNotify(message));
    }

    public final ActorRef referent() {
        return referent;
    }

    @Override
    public final Value select(Feature feature) {
        if (feature.equals(CommonFeatures.ASK)) {
            return PROC_ASK;
        } else if (feature.equals(CommonFeatures.TELL)) {
            return PROC_TELL;
        }
        throw new FeatureNotFoundError(this, feature);
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

}
