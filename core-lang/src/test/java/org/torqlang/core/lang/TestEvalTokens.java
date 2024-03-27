/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;
import org.torqlang.core.klvm.Ident;
import org.torqlang.core.klvm.Token;
import org.torqlang.core.klvm.TokenMod;
import org.torqlang.core.klvm.Var;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestEvalTokens {

    @Test
    public void test() throws Exception {
        String source = """
            begin
                x = Token.new()
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(TokenMod.TOKEN_IDENT, new Var(TokenMod.TOKEN_CLS))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = "$select_apply(Token, ['new'], x)";
        assertEquals(expected, e.kernel().toString());
        assertTrue(e.varAtName("x").valueOrVarSet() instanceof Token);
    }

}
