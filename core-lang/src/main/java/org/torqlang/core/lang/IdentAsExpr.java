/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.Ident;
import org.torqlang.core.util.SourceSpan;

public final class IdentAsExpr extends AbstractLang implements LabelExpr {

    public final Ident ident;

    public IdentAsExpr(Ident ident, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.ident = ident;
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitIdentAsExpr(this, state);
    }

}
