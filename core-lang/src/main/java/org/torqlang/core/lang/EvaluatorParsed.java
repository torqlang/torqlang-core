/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.Env;
import org.torqlang.core.klvm.Ident;

public interface EvaluatorParsed {
    Ident exprIdent();

    EvaluatorGenerated generate() throws Exception;

    long maxTime();

    EvaluatorParsed parse() throws Exception;

    EvaluatorPerformed perform() throws Exception;

    Env rootEnv();

    SntcOrExpr sntcOrExpr();

    String source();
}
