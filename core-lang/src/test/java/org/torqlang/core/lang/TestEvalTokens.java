/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.Ident;
import org.torqlang.core.klvm.Token;
import org.torqlang.core.klvm.TokenPack;
import org.torqlang.core.klvm.Var;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class TestEvalTokens {

    @Test
    public void test() throws Exception {
        String source = """
            begin
                x = Token.new()
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(TokenPack.TOKEN_IDENT, new Var(TokenPack.TOKEN_CLS))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = "$select_apply(Token, ['new'], x)";
        assertEquals(expected, e.kernel().toString());
        assertInstanceOf(Token.class, e.varAtName("x").valueOrVarSet());
    }

}
