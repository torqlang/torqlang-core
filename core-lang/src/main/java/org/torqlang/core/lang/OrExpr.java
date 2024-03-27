/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.util.SourceSpan;

public final class OrExpr extends AbstractLang implements BuiltInApplyExpr {

    public final SntcOrExpr arg1;
    public final SntcOrExpr arg2;

    public OrExpr(SntcOrExpr arg1, SntcOrExpr arg2, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitOrExpr(this, state);
    }

}
