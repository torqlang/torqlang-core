/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.Map;

public final class CompleteClosure extends AbstractClosure implements Complete {

    private CompleteClosure() {
        super(null, null);
    }

    public CompleteClosure(ProcDef procDef, Map<Ident, Complete> capturedEnv) {
        super(procDef, Env.createComplete(capturedEnv));
    }

    static CompleteClosure instanceForRestore() {
        return new CompleteClosure();
    }

    public CompleteClosure checkComplete() {
        return this;
    }

}
