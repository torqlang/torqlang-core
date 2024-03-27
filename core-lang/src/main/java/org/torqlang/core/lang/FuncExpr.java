/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.util.SourceSpan;

import java.util.List;

public final class FuncExpr extends FuncLang implements Expr {

    public FuncExpr(List<Pat> formalArgs, TypeAnno returnType, SeqLang body, SourceSpan sourceSpan) {
        super(formalArgs, returnType, body, sourceSpan);
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitFuncExpr(this, state);
    }

}
