/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestFeatureComparator {

    @Test
    public void test() {

        FeatureComparator c = FeatureComparator.SINGLETON;

        //
        // Int
        //

        // Validate that same integer compares equal
        //noinspection all
        assertEquals(0, c.compare(Int32.I32_3, Int32.I32_3));
        assertEquals(0, c.compare(Int32.I32_3, Int64.I64_3));
        assertEquals(0, c.compare(Int64.I64_3, Int32.I32_3));

        // Validate that a smaller integer compares less than a larger integer
        assertTrue(c.compare(Int32.I32_2, Int32.I32_3) < 0);
        assertTrue(c.compare(Int32.I32_2, Int64.I64_3) < 0);
        assertTrue(c.compare(Int64.I64_2, Int32.I32_3) < 0);

        // Validate that a larger integer compares less than a smaller integer
        assertTrue(c.compare(Int32.I32_4, Int32.I32_3) > 0);
        assertTrue(c.compare(Int32.I32_4, Int64.I64_3) > 0);
        assertTrue(c.compare(Int64.I64_4, Int32.I32_3) > 0);

        final Str aStr1 = Str.of("abc");
        final Str anotherStr1 = Str.of("abc");
        final Str aStr2 = Str.of("cba");
        final Token aToken1 = new Token();
        final Token aToken2 = new Token();

        // Validate that an Int compares less than a Str
        assertTrue(c.compare(Int32.I32_5, aStr1) < 0);
        assertTrue(c.compare(aStr1, Int32.I32_5) > 0);

        // Validate that an Int compares less than a Bool
        assertTrue(c.compare(Int32.I32_5, Bool.FALSE) < 0);
        assertTrue(c.compare(Int32.I32_5, Bool.TRUE) < 0);
        assertTrue(c.compare(Bool.FALSE, Int32.I32_5) > 0);
        assertTrue(c.compare(Bool.TRUE, Int32.I32_5) > 0);

        // Validate that an Int compares less than Eof
        assertTrue(c.compare(Int32.I32_5, Eof.SINGLETON) < 0);
        assertTrue(c.compare(Eof.SINGLETON, Int32.I32_5) > 0);

        // Validate that an Int compares less than Nothing
        assertTrue(c.compare(Int32.I32_5, Nothing.SINGLETON) < 0);
        assertTrue(c.compare(Nothing.SINGLETON, Int32.I32_5) > 0);

        // Validate that an Int compares less than a Token
        assertTrue(c.compare(Int32.I32_5, aToken1) < 0);
        assertTrue(c.compare(aToken2, Int32.I32_5) > 0);

        //
        // Str
        //

        // Validate that a Str compares lexicographically
        //noinspection all
        assertEquals(0, c.compare(aStr1, aStr1));
        assertEquals(0, c.compare(aStr1, anotherStr1));
        assertTrue(c.compare(aStr1, aStr2) < 0);
        assertTrue(c.compare(aStr2, aStr1) > 0);

        // Validate that a Str compares less than a Bool
        assertTrue(c.compare(aStr1, Bool.FALSE) < 0);
        assertTrue(c.compare(aStr1, Bool.TRUE) < 0);
        assertTrue(c.compare(Bool.FALSE, aStr2) > 0);
        assertTrue(c.compare(Bool.TRUE, aStr2) > 0);

        // Validate that a Str compares less than Eof
        assertTrue(c.compare(aStr1, Eof.SINGLETON) < 0);
        assertTrue(c.compare(Eof.SINGLETON, aStr2) > 0);

        // Validate that a Str compares less than Nothing
        assertTrue(c.compare(aStr1, Nothing.SINGLETON) < 0);
        assertTrue(c.compare(Nothing.SINGLETON, aStr2) > 0);

        // Validate that a Str compares less than a Token
        assertTrue(c.compare(aStr1, aToken1) < 0);
        assertTrue(c.compare(aToken2, aStr2) > 0);

        //
        // Bool
        //

        // Validate that same Bool compares equal
        //noinspection all
        assertEquals(0, c.compare(Bool.FALSE, Bool.FALSE));
        //noinspection all
        assertEquals(0, c.compare(Bool.TRUE, Bool.TRUE));

        // Validate that a Bool compares in FALSE, TRUE order
        assertTrue(c.compare(Bool.FALSE, Bool.TRUE) < 0);
        assertTrue(c.compare(Bool.TRUE, Bool.FALSE) > 0);

        // Validate that a Bool compares less than Eof
        assertTrue(c.compare(Bool.FALSE, Eof.SINGLETON) < 0);
        assertTrue(c.compare(Bool.TRUE, Eof.SINGLETON) < 0);
        assertTrue(c.compare(Eof.SINGLETON, Bool.FALSE) > 0);
        assertTrue(c.compare(Eof.SINGLETON, Bool.TRUE) > 0);

        // Validate that a Bool compares less than Nothing
        assertTrue(c.compare(Bool.FALSE, Nothing.SINGLETON) < 0);
        assertTrue(c.compare(Bool.TRUE, Nothing.SINGLETON) < 0);
        assertTrue(c.compare(Nothing.SINGLETON, Bool.FALSE) > 0);
        assertTrue(c.compare(Nothing.SINGLETON, Bool.TRUE) > 0);

        // Validate that a Bool compares less than a Token
        assertTrue(c.compare(Bool.FALSE, aToken1) < 0);
        assertTrue(c.compare(Bool.TRUE, aToken1) < 0);
        assertTrue(c.compare(aToken2, Bool.FALSE) > 0);
        assertTrue(c.compare(aToken2, Bool.TRUE) > 0);

        //
        // Eof
        //

        // Validate that Eof compares to Eof
        //noinspection all
        assertEquals(0, c.compare(Eof.SINGLETON, Eof.SINGLETON));

        // Validate that an Eof compares less than Nothing
        assertTrue(c.compare(Eof.SINGLETON, Nothing.SINGLETON) < 0);
        assertTrue(c.compare(Nothing.SINGLETON, Eof.SINGLETON) > 0);

        // Validate that an Eof compares less than a Token
        assertTrue(c.compare(Eof.SINGLETON, aToken1) < 0);
        assertTrue(c.compare(aToken2, Eof.SINGLETON) > 0);

        //
        // Nothing
        //

        // Validate that Nothing compares to Nothing
        //noinspection all
        assertEquals(0, c.compare(Nothing.SINGLETON, Nothing.SINGLETON));

        // Validate that a Nothing compares less than a Token
        assertTrue(c.compare(Nothing.SINGLETON, aToken1) < 0);
        assertTrue(c.compare(aToken2, Nothing.SINGLETON) > 0);

        //
        // Token
        //

        // Validate that a Token compares equal to itself
        //noinspection all
        assertEquals(0, c.compare(aToken1, aToken1));
        //noinspection all
        assertEquals(0, c.compare(aToken2, aToken2));

        List<Token> manyTokens = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++) {
            manyTokens.add(new Token());
        }
        Token lastToken = aToken1;
        for (Token t : manyTokens) {
            //noinspection all
            assertEquals(0, c.compare(lastToken, lastToken));
            assertTrue(c.compare(lastToken, t) < 0);
            assertTrue(c.compare(t, lastToken) > 0);
            lastToken = t;
        }

    }

}
