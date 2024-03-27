/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.List;

public class Closure implements Proc {

    private final Env capturedEnv;
    private final ProcDef procDef;

    public Closure(ProcDef procDef, Env capturedEnv) {
        this.procDef = procDef;
        this.capturedEnv = capturedEnv;
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
        if (ys.size() > 0) {
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

    public final Env capturedEnv() {
        return capturedEnv;
    }

    public CompleteClosure checkComplete() throws WaitVarException {
        for (EnvEntry envEntry : capturedEnv) {
            envEntry.var.resolveValue().checkComplete();
        }
        return new CompleteClosure(procDef, capturedEnv);
    }

    @Override
    public final boolean isValidKey() {
        return true;
    }

    public final ProcDef procDef() {
        return procDef;
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

}
