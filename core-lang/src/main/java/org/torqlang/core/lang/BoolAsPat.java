/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.Bool;
import org.torqlang.core.util.SourceSpan;

public final class BoolAsPat extends AbstractLang implements LiteralAsPat {

    public final Bool bool;

    public BoolAsPat(Bool bool, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.bool = bool;
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitBoolAsPat(this, state);
    }

    @Override
    public final Bool value() {
        return bool;
    }

}
