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
import static org.torqlang.core.lang.CommonTools.asIdentAsExpr;
import static org.torqlang.core.lang.CommonTools.assertSourceSpan;

public class TestParserSpawnExpr {

    @Test
    public void test() {
        //                            012345678
        Parser p = new Parser("spawn(x)");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(SpawnExpr.class, sox);
        SpawnExpr spawnExpr = (SpawnExpr) sox;
        assertSourceSpan(spawnExpr, 0, 8);
        assertEquals(1, spawnExpr.args.size());
        assertEquals(Ident.create("x"), asIdentAsExpr(spawnExpr.args.get(0)).ident);
        assertSourceSpan(spawnExpr.args.get(0), 6, 7);
        // Test toString format
        String expectedFormat = "spawn(x)";
        String actualFormat = spawnExpr.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test indented format
        expectedFormat = "spawn(x)";
        actualFormat = LangFormatter.SINGLETON.format(spawnExpr);
        assertEquals(expectedFormat, actualFormat);
    }

}
