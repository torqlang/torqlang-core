/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;
import org.torqlang.core.klvm.Ident;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.torqlang.core.lang.CommonTools.*;

public class TestParserActorSntc {

    @Test
    public void test() {
        //                                      1         2         3         4         5         6
        //                            012345678901234567890123456789012345678901234567890123456789012
        Parser p = new Parser("actor MyActor() in ask 'get' in a end tell 'incr' in b end end");
        SntcOrExpr sox = p.parse();
        ActorSntc actorSntc = (ActorSntc) sox;
        assertSourceSpan(actorSntc, 0, 62);
        assertEquals(Ident.create("MyActor"), actorSntc.name());
        // Test format
        String expectedFormat = """
            actor MyActor() in
                ask 'get' in
                    a
                end
                tell 'incr' in
                    b
                end
            end""";
        String actualFormat = actorSntc.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test initializer
        assertEquals(0, actorSntc.initializer().size());
        // Test handlers
        assertEquals(2, actorSntc.handlers().size());
        assertTrue(actorSntc.handlers().get(0) instanceof AskSntc);
        assertSourceSpan(actorSntc.handlers().get(0), 19, 37);
        assertTrue(actorSntc.handlers().get(1) instanceof TellSntc);
        assertSourceSpan(actorSntc.handlers().get(1), 38, 58);
    }

    @Test
    public void testWithInitializer() {
        //                                      1         2         3         4         5         6         7         8
        //                            01234567890123456789012345678901234567890123456789012345678901234567890123456789012
        Parser p = new Parser("actor MyActor() in var x = 0 var y = 1 ask 'get' in a end tell 'incr' in b end end");
        SntcOrExpr sox = p.parse();
        ActorSntc actorSntc = (ActorSntc) sox;
        assertSourceSpan(actorSntc, 0, 82);
        assertEquals(Ident.create("MyActor"), actorSntc.name());
        // Test format
        String expectedFormat = """
            actor MyActor() in
                var x = 0
                var y = 1
                ask 'get' in
                    a
                end
                tell 'incr' in
                    b
                end
            end""";
        String actualFormat = actorSntc.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test initializer
        assertEquals(2, actorSntc.initializer().size());
        assertTrue(actorSntc.initializer().get(0) instanceof VarSntc);
        assertTrue(actorSntc.initializer().get(1) instanceof VarSntc);
        // Test handlers
        assertEquals(2, actorSntc.handlers().size());
        assertTrue(actorSntc.handlers().get(0) instanceof AskSntc);
        assertSourceSpan(actorSntc.handlers().get(0), 39, 57);
        assertTrue(actorSntc.handlers().get(1) instanceof TellSntc);
        assertSourceSpan(actorSntc.handlers().get(1), 58, 78);
    }

    @Test
    public void testWithRespondType() {
        //                                      1         2         3         4         5
        //                            012345678901234567890123456789012345678901234567890
        Parser p = new Parser("actor MyActor() in ask 'get' -> Int32 in a end end");
        SntcOrExpr sox = p.parse();
        ActorSntc actorSntc = (ActorSntc) sox;
        assertSourceSpan(actorSntc, 0, 50);
        assertEquals(Ident.create("MyActor"), actorSntc.name());
        // Test format
        String expectedFormat = """
            actor MyActor() in
                ask 'get' -> Int32 in
                    a
                end
            end""";
        String actualFormat = actorSntc.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test initializer
        assertEquals(0, actorSntc.initializer().size());
        // Test handlers
        assertEquals(1, actorSntc.handlers().size());
        assertTrue(actorSntc.handlers().get(0) instanceof AskSntc);
        assertSourceSpan(actorSntc.handlers().get(0), 19, 46);
    }

}
