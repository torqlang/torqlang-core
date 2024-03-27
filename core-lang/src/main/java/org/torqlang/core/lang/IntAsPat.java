/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.Int64;
import org.torqlang.core.util.SourceSpan;

public final class IntAsPat extends AbstractLang implements FeatureAsPat {

    private String intText;
    private Int64 int64;

    public IntAsPat(Int64 int64, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.int64 = int64;
    }

    public IntAsPat(String intText, SourceSpan sourceSpan) {
        super(sourceSpan);
        // We must hold intermediate integers as strings during parsing. We can't hold the absolute value
        // of `Long.MIN_VALUE`, it's too large.
        this.intText = intText;
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitIntAsPat(this, state);
    }

    public final Int64 int64() {
        if (intText != null) {
            int64 = NumAsExpr.parseAsInt32OrInt64(intText);
            intText = null;
        }
        return int64;
    }

    public final String intText() {
        return intText;
    }

    public final boolean isNegative() {
        return intText != null ? intText.charAt(0) == '-' : int64.longValue() < 0;
    }

    public final IntAsPat negate(SourceSpan adjoinSourceSpan) {
        if (intText != null) {
            if (intText.charAt(0) == '-') {
                return new IntAsPat(intText.substring(1), adjoinSourceSpan.adjoin(this));
            } else {
                return new IntAsPat("-" + intText, adjoinSourceSpan.adjoin(this));
            }
        } else {
            return new IntAsPat(int64.negate(), adjoinSourceSpan.adjoin(this));
        }
    }

    @Override
    public final Int64 value() {
        return int64();
    }

}
