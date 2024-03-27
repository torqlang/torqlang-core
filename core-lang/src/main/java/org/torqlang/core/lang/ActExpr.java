/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.util.SourceSpan;

public final class ActExpr extends AbstractLang implements Expr {

    public final SeqLang seq;

    public ActExpr(SeqLang seqExpr, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.seq = seqExpr;
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitActExpr(this, state);
    }

}
