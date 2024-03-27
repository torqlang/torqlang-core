/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.Env;
import org.torqlang.core.klvm.Ident;
import org.torqlang.core.klvm.Var;

public interface EvaluatorInit {
    EvaluatorInit addVar(Ident ident);

    EvaluatorInit addVar(Ident ident, Var var);

    EvaluatorInit setExprIdent(Ident exprIdent);

    EvaluatorInit setMaxTime(long maxTime);

    EvaluatorInit setRootEnv(Env rootEnv);

    EvaluatorReady setSntcOrExpr(SntcOrExpr sntcOrExpr);

    EvaluatorReady setSource(String source);
}
