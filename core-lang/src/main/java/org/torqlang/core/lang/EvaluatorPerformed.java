/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.Env;
import org.torqlang.core.klvm.Ident;
import org.torqlang.core.klvm.Kernel;
import org.torqlang.core.klvm.Var;

public interface EvaluatorPerformed {
    Env env();

    Ident exprIdent();

    Kernel kernel();

    long maxTime();

    Env rootEnv();

    SntcOrExpr sntcOrExpr();

    String source();

    Var varAtName(String name);
}
