/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.Ident;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.torqlang.core.lang.CommonTools.assertSourceSpan;

public class TestParserActorSntc {

    @Test
    public void test() {
        //                                      1         2         3         4         5         6         7
        //                            01234567890123456789012345678901234567890123456789012345678901234567890123456
        Parser p = new Parser("actor MyActor() in handle ask 'get' in a end handle tell 'incr' in b end end");
        SntcOrExpr sox = p.parse();
        ActorSntc actorSntc = (ActorSntc) sox;
        assertSourceSpan(actorSntc, 0, 76);
        assertEquals(Ident.create("MyActor"), actorSntc.name());
        // Test format
        String expectedFormat = """
            actor MyActor() in
                handle ask 'get' in
                    a
                end
                handle tell 'incr' in
                    b
                end
            end""";
        String actualFormat = actorSntc.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test initializer
        assertEquals(0, actorSntc.initializer().size());
        // Test handlers
        assertEquals(1, actorSntc.askHandlers().size());
        assertEquals(1, actorSntc.tellHandlers().size());
        assertSourceSpan(actorSntc.askHandlers().get(0), 19, 44);
        assertSourceSpan(actorSntc.tellHandlers().get(0), 45, 72);
    }

    @Test
    public void testWithInitializer() {
        //                                      1         2         3         4         5         6         7         8         9
        //                            0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456
        Parser p = new Parser("actor MyActor() in var x = 0 var y = 1 handle ask 'get' in a end handle tell 'incr' in b end end");
        SntcOrExpr sox = p.parse();
        ActorSntc actorSntc = (ActorSntc) sox;
        assertSourceSpan(actorSntc, 0, 96);
        assertEquals(Ident.create("MyActor"), actorSntc.name());
        // Test format
        String expectedFormat = """
            actor MyActor() in
                var x = 0
                var y = 1
                handle ask 'get' in
                    a
                end
                handle tell 'incr' in
                    b
                end
            end""";
        String actualFormat = actorSntc.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test initializer
        assertEquals(2, actorSntc.initializer().size());
        assertInstanceOf(VarSntc.class, actorSntc.initializer().get(0));
        assertInstanceOf(VarSntc.class, actorSntc.initializer().get(1));
        // Test handlers
        assertEquals(1, actorSntc.askHandlers().size());
        assertEquals(1, actorSntc.tellHandlers().size());
        assertSourceSpan(actorSntc.askHandlers().get(0), 39, 64);
        assertSourceSpan(actorSntc.tellHandlers().get(0), 65, 92);
    }

    @Test
    public void testWithRespondType() {
        //                                      1         2         3         4         5
        //                            0123456789012345678901234567890123456789012345678901234567
        Parser p = new Parser("actor MyActor() in handle ask 'get' -> Int32 in a end end");
        SntcOrExpr sox = p.parse();
        ActorSntc actorSntc = (ActorSntc) sox;
        assertSourceSpan(actorSntc, 0, 57);
        assertEquals(Ident.create("MyActor"), actorSntc.name());
        // Test format
        String expectedFormat = """
            actor MyActor() in
                handle ask 'get' -> Int32 in
                    a
                end
            end""";
        String actualFormat = actorSntc.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test initializer
        assertEquals(0, actorSntc.initializer().size());
        // Test handlers
        assertEquals(1, actorSntc.askHandlers().size());
        assertSourceSpan(actorSntc.askHandlers().get(0), 19, 53);
    }

}
