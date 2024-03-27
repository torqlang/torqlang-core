/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.SourceSpan;

public interface BindStmt extends Stmt {

    static BindStmt create(CompleteOrIdent leftSide, CompleteOrIdent rightSide, SourceSpan sourceSpan) {
        if (leftSide instanceof Ident lsi) {
            if (rightSide instanceof Ident rsi) {
                return new BindIdentToIdentStmt(rsi, lsi, sourceSpan);
            } else {
                return new BindCompleteToIdentStmt((Complete) rightSide, lsi, sourceSpan);
            }
        } else {
            if (rightSide instanceof Ident rsi) {
                return new BindCompleteToIdentStmt((Complete) leftSide, rsi, sourceSpan);
            } else {
                return new BindCompleteToCompleteStmt((Complete) leftSide, (Complete) rightSide, sourceSpan);
            }
        }
    }

}
