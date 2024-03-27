/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.SourceSpan;

import java.util.Set;

public abstract class AbstractStmt implements Stmt {

    public final SourceSpan sourceSpan;

    public AbstractStmt(SourceSpan sourceSpan) {
        this.sourceSpan = sourceSpan;
    }

    @Override
    public final int begin() {
        return sourceSpan.begin();
    }

    @Override
    public abstract void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree);

    @Override
    public final int end() {
        return sourceSpan.end();
    }

    @Override
    public void pushStackEntries(Machine machine, Env env) {
        machine.pushStackEntry(this, env);
    }

    @Override
    public final String source() {
        return sourceSpan.source();
    }

    @Override
    public final SourceSpan toSourceSpanBegin() {
        return sourceSpan.toSourceSpanBegin();
    }

    @Override
    public final SourceSpan toSourceSpanEnd() {
        return sourceSpan.toSourceSpanEnd();
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

}
