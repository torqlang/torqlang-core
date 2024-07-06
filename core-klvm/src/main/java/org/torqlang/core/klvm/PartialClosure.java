/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.IdentityHashMap;

public class PartialClosure extends AbstractClosure implements Partial {

    public PartialClosure(ProcDef procDef, Env capturedEnv) {
        super(procDef, capturedEnv);
    }

    public Complete checkComplete() throws WaitVarException {
        return checkComplete(new IdentityHashMap<>());
    }

    @Override
    public final Complete checkComplete(IdentityHashMap<Partial, Complete> memos) throws WaitVarException {
        Complete previous = memos.get(this);
        if (previous != null) {
            return previous;
        }
        if (capturedEnv.parentEnv() != null) {
            throw new IllegalStateException("Invalid closure environment");
        }
        CompleteClosure thisCompleteClosure = CompleteClosure.instanceForRestore();
        memos.put(this, thisCompleteClosure);
        EnvEntry[] envEntries = new EnvEntry[capturedEnv.shallowSize()];
        int nextIndex = 0;
        for (EnvEntry envEntry : capturedEnv) {
            Value resolvedValue = envEntry.var.resolveValue();
            Complete completeValue;
            if (resolvedValue instanceof Partial partial) {
                completeValue = partial.checkComplete(memos);
            } else {
                completeValue = resolvedValue.checkComplete();
            }
            envEntries[nextIndex++] = new EnvEntry(envEntry.ident, new Var(completeValue));
        }
        thisCompleteClosure.restore(procDef, Env.createPrivatelyForKlvm(null, envEntries));
        return thisCompleteClosure;
    }

}
