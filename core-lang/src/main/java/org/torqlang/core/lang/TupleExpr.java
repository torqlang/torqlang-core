/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.*;
import org.torqlang.core.util.SourceSpan;

import java.util.List;

import static org.torqlang.core.util.ListTools.nullSafeCopyOf;

public final class TupleExpr extends AbstractLang implements Expr {

    private final Expr label;
    private final List<SntcOrExpr> values;

    public TupleExpr(Expr label, List<SntcOrExpr> values, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.label = label;
        this.values = nullSafeCopyOf(values);
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitTupleExpr(this, state);
    }

    public final CompleteTuple checkComplete() {
        CompleteTupleBuilder b = Rec.completeTupleBuilder();
        SntcOrExpr label = label();
        if (label == null) {
            b.setLabel(Rec.DEFAULT_LABEL);
        } else {
            Complete l = RecExpr.checkComplete(label());
            if (l == null) {
                return null;
            }
            b.setLabel((Literal) l);
        }
        for (SntcOrExpr v : values()) {
            Complete cv = RecExpr.checkComplete(v);
            if (cv == null) {
                return null;
            }
            b.addValue(cv);
        }
        return b.build();
    }

    public final Expr label() {
        return label;
    }

    public final List<SntcOrExpr> values() {
        return values;
    }

}
