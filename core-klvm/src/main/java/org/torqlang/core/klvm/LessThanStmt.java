/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.SourceSpan;

import java.util.Set;

public final class LessThanStmt extends AbstractStmt {

    public final CompleteOrIdent a;
    public final CompleteOrIdent b;
    public final Ident x;

    public LessThanStmt(CompleteOrIdent a, CompleteOrIdent b, Ident x, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.a = a;
        this.b = b;
        this.x = x;
    }

    public final CompleteOrIdent a() {
        return a;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitLessThanStmt(this, state);
    }

    public final CompleteOrIdent b() {
        return b;
    }

    @Override
    public final void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        CompleteOrIdent.captureLexicallyFree(a, knownBound, lexicallyFree);
        CompleteOrIdent.captureLexicallyFree(b, knownBound, lexicallyFree);
        Ident.captureLexicallyFree(x, knownBound, lexicallyFree);
    }

    @Override
    public void compute(Env env, Machine machine) throws WaitException {
        Value aRes = a.resolveValue(env);
        Value bRes = b.resolveValue(env);
        Value result = aRes.lessThan(bRes);
        ValueOrVar xRes = x.resolveValueOrVar(env);
        xRes.bindToValue(result, null);
    }

    public final Ident x() {
        return x;
    }
}
