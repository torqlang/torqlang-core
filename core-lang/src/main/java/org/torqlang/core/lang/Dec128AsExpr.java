/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.Dec128;
import org.torqlang.core.util.SourceSpan;

public final class Dec128AsExpr extends AbstractLang implements NumAsExpr {

    private String decText;

    private Dec128 dec128;

    public Dec128AsExpr(Dec128 dec128, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.dec128 = dec128;
    }

    public Dec128AsExpr(String decText, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.decText = decText;
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitDec128AsExpr(this, state);
    }

    public final Dec128 dec128() {
        if (decText != null) {
            dec128 = (Dec128) NumAsExpr.parseAsFlt32OrFlt64OrDec128(decText);
            decText = null;
        }
        return dec128;
    }

    public final String decText() {
        return decText;
    }

    @Override
    public final Dec128 value() {
        return dec128();
    }

}
