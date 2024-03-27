/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.util.SourceSpan;

public final class IfClause extends AbstractLang {

    public final SntcOrExpr condition;
    public final SeqLang body;

    public IfClause(SntcOrExpr condition, SeqLang body, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.condition = condition;
        this.body = body;
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitIfClause(this, state);
    }

}
