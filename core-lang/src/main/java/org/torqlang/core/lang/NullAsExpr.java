/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.Null;
import org.torqlang.core.util.SourceSpan;

public final class NullAsExpr extends AbstractLang implements ValueAsExpr, LabelExpr {

    public NullAsExpr(SourceSpan sourceSpan) {
        super(sourceSpan);
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitNullAsExpr(this, state);
    }

    @Override
    public final Null value() {
        return Null.SINGLETON;
    }

}
