/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.Str;
import org.torqlang.core.util.SourceSpan;

public final class StrAsPat extends AbstractLang implements LiteralAsPat {

    public final Str str;

    public StrAsPat(Str str, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.str = str;
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitStrAsPat(this, state);
    }

    @Override
    public final Str value() {
        return str;
    }

}
