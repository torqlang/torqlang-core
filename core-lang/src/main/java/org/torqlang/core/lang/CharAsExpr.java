/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.Char;
import org.torqlang.core.util.SourceSpan;

public final class CharAsExpr extends AbstractLang implements NumAsExpr {

    private final Char charNum;

    public CharAsExpr(Char charNum, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.charNum = charNum;
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitCharAsExpr(this, state);
    }

    public final Char charNum() {
        return charNum;
    }

    @Override
    public final Char value() {
        return charNum();
    }

}
