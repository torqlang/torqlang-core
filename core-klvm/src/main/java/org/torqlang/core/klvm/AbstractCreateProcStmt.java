/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.SourceSpan;

import java.util.Set;

public abstract class AbstractCreateProcStmt extends AbstractStmt implements CreateStmt {

    public final Ident x;
    public final ProcDef procDef;

    public AbstractCreateProcStmt(Ident x, ProcDef procDef, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.x = x;
        this.procDef = procDef;
    }

    @Override
    public final void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        Ident.captureLexicallyFree(x, knownBound, lexicallyFree);
        procDef.captureLexicallyFree(knownBound, lexicallyFree);
    }

    final Closure computeClosure(Env env) {
        Set<Ident> freeIdents = procDef.freeIdents;
        Env capturedEnv;
        if (!freeIdents.isEmpty()) {
            EnvEntry[] bindings = new EnvEntry[freeIdents.size()];
            int i = 0;
            for (Ident ident : freeIdents) {
                bindings[i++] = new EnvEntry(ident, env.get(ident));
            }
            capturedEnv = Env.createPrivatelyForKlvm(null, bindings);
        } else {
            capturedEnv = Env.emptyEnv();
        }
        return new Closure(procDef, capturedEnv);
    }

    public final ProcDef procDef() {
        return procDef;
    }

}
