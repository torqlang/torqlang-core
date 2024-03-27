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

public final class RecExpr extends AbstractLang implements Expr {

    private final Expr label;
    private final List<FieldExpr> fields;

    public RecExpr(Expr label, List<FieldExpr> fields, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.label = label;
        this.fields = nullSafeCopyOf(fields);
    }

    public static Complete checkComplete(SntcOrExpr expr) {
        if (expr instanceof ValueAsExpr valueAsExpr) {
            return valueAsExpr.value();
        }
        if (expr instanceof RecExpr recExpr) {
            return recExpr.checkComplete();
        }
        if (expr instanceof TupleExpr tupleExpr) {
            return tupleExpr.checkComplete();
        }
        return null;
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitRecExpr(this, state);
    }

    public final CompleteRec checkComplete() {
        CompleteRecBuilder b = Rec.completeRecBuilder();
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
        for (FieldExpr f : fields()) {
            Feature fr = (Feature) RecExpr.checkComplete(f.feature);
            if (fr == null) {
                return null;
            }
            Complete vl = RecExpr.checkComplete(f.value);
            if (vl == null) {
                return null;
            }
            b.addField(new CompleteField(fr, vl));
        }
        return b.build();
    }

    public final List<FieldExpr> fields() {
        return fields;
    }

    public final Expr label() {
        return label;
    }

}
