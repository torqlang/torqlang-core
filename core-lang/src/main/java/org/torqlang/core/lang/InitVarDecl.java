/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.util.SourceSpan;

public final class InitVarDecl extends AbstractLang implements VarDecl {

    public final Pat varPat;
    public final SntcOrExpr valueExpr;

    public InitVarDecl(Pat varPat, SntcOrExpr valueExpr, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.varPat = varPat;
        this.valueExpr = valueExpr;
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state) throws Exception {
        return visitor.visitInitVarDecl(this, state);
    }

}
