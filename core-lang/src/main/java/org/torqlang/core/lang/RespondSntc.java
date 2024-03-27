/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.util.SourceSpan;

public final class RespondSntc extends AbstractLang implements Sntc {

    public final SntcOrExpr expr;

    public RespondSntc(SntcOrExpr expr, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.expr = expr;
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitRespondSntc(this, state);
    }

}
