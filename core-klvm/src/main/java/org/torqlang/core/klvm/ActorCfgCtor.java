/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.ArrayList;
import java.util.List;

public class ActorCfgCtor implements Proc {

    private final Closure handlerCtor;

    public ActorCfgCtor(Closure handlerCtor) {
        this.handlerCtor = handlerCtor;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitActorCfgCtor(this, state);
    }

    @Override
    public final void apply(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        // NOTE: An ActorCfgCtor requires all arguments to be Complete. The last argument is not checked because it is
        // the return argument.
        List<Complete> resArgs = new ArrayList<>(ys.size());
        for (int i = 0; i < ys.size() - 1; i++) {
            CompleteOrIdent y = ys.get(i);
            Complete yRes = y.resolveValue(env).checkComplete();
            resArgs.add(yRes);
        }
        CompleteOrIdent target = ys.get(ys.size() - 1);
        ValueOrVar targetRes = target.resolveValueOrVar(env);
        ActorCfg actorCfg = new ActorCfg(resArgs, handlerCtor);
        targetRes.bindToValue(actorCfg, null);
    }

    public CompleteActorCfgCtor checkComplete() throws WaitVarException {
        for (EnvEntry envEntry : handlerCtor.capturedEnv()) {
            envEntry.var.resolveValue().checkComplete();
        }
        return new CompleteActorCfgCtor(handlerCtor);
    }

    public final Closure handlerCtor() {
        return handlerCtor;
    }

    @Override
    public final boolean isValidKey() {
        return true;
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

}
