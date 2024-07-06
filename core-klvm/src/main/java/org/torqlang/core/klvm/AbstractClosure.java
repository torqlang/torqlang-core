/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.List;

public abstract class AbstractClosure implements Closure {

    ProcDef procDef;
    Env capturedEnv;

    AbstractClosure(ProcDef procDef, Env capturedEnv) {
        this.capturedEnv = capturedEnv;
        this.procDef = procDef;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state) throws Exception {
        return visitor.visitClosure(this, state);
    }

    @Override
    public final void apply(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        if (ys.size() != procDef.xs.size()) {
            throw new InvalidArgCountError(procDef.xs.size(), ys, this);
        }
        Env bodyEnv;
        if (!ys.isEmpty()) {
            EnvEntry[] actualArgs = new EnvEntry[ys.size()];
            for (int i = 0; i < ys.size(); i++) {
                actualArgs[i] = new EnvEntry(procDef.xs.get(i), ys.get(i).toVar(env));
            }
            bodyEnv = Env.createPrivatelyForKlvm(capturedEnv, actualArgs);
        } else {
            bodyEnv = capturedEnv;
        }
        machine.pushStackEntry(procDef.stmt, bodyEnv);
    }

    @Override
    public final Env capturedEnv() {
        return capturedEnv;
    }

    @Override
    public final boolean isValidKey() {
        return true;
    }

    @Override
    public final ProcDef procDef() {
        return procDef;
    }

    final void restore(ProcDef procDef, Env capturedEnv) {
        this.procDef = procDef;
        this.capturedEnv = capturedEnv;
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

}
