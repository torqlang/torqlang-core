/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;
import org.torqlang.core.klvm.CompleteRec;
import org.torqlang.core.klvm.Int32;
import org.torqlang.core.klvm.Rec;
import org.torqlang.core.klvm.Str;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.torqlang.core.lang.CommonTools.*;

public class TestParserTupleExpr {

    @Test
    public void testEmpty() {
        //                            012
        Parser p = new Parser("[]");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof TupleExpr);
        TupleExpr tupleExpr = (TupleExpr) sox;
        assertSourceSpan(tupleExpr, 0, 2);
        assertNull(tupleExpr.label());
        assertEquals(0, tupleExpr.values().size());
        // Test toString format
        String expectedFormat = "[]";
        String actualFormat = tupleExpr.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test indented format
        expectedFormat = "[]";
        actualFormat = LangFormatter.SINGLETON.format(tupleExpr);
        assertEquals(expectedFormat, actualFormat);
        // Test complete
        CompleteRec completeRec = tupleExpr.checkComplete();
        assertNotNull(completeRec);
        assertEquals(Rec.DEFAULT_LABEL, completeRec.label());
        assertEquals(0, completeRec.fieldCount());
    }

    @Test
    public void testValues1() {
        //                            0123
        Parser p = new Parser("[0]");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof TupleExpr);
        TupleExpr tupleExpr = (TupleExpr) sox;
        assertSourceSpan(tupleExpr, 0, 3);
        assertNull(tupleExpr.label());
        // Test features
        assertEquals(1, tupleExpr.values().size());
        SntcOrExpr valueExpr = tupleExpr.values().get(0);
        assertSourceSpan(valueExpr, 1, 2);
        assertEquals(Int32.I32_0, asIntAsExpr(valueExpr).int64());
        // Test toString format
        String expectedFormat = "[0]";
        String actualFormat = tupleExpr.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test indented format
        expectedFormat = "[0]";
        actualFormat = LangFormatter.SINGLETON.format(tupleExpr);
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testValues2() {
        //                            0123456
        Parser p = new Parser("[0, 1]");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof TupleExpr);
        TupleExpr tupleExpr = (TupleExpr) sox;
        assertSourceSpan(tupleExpr, 0, 6);
        assertNull(tupleExpr.label());
        // Test features
        assertEquals(2, tupleExpr.values().size());
        SntcOrExpr valueExpr = tupleExpr.values().get(0);
        assertSourceSpan(valueExpr, 1, 2);
        assertEquals(Int32.I32_0, asIntAsExpr(valueExpr).int64());
        valueExpr = tupleExpr.values().get(1);
        assertSourceSpan(valueExpr, 4, 5);
        assertEquals(Int32.I32_1, asIntAsExpr(valueExpr).int64());
        // Test toString format
        String expectedFormat = "[0, 1]";
        String actualFormat = tupleExpr.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test indented format
        expectedFormat = "[0, 1]";
        actualFormat = LangFormatter.SINGLETON.format(tupleExpr);
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testValues1WithLabel() {
        //                                      1
        //                            012345678901234
        Parser p = new Parser("'my-label'#[0]");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof TupleExpr);
        TupleExpr tupleExpr = (TupleExpr) sox;
        assertSourceSpan(tupleExpr, 0, 14);
        assertEquals(Str.of("my-label"), asStrAsExpr(tupleExpr.label()).str);
        // Test features
        assertEquals(1, tupleExpr.values().size());
        SntcOrExpr valueExpr = tupleExpr.values().get(0);
        assertSourceSpan(valueExpr, 12, 13);
        assertEquals(Int32.I32_0, asIntAsExpr(valueExpr).int64());
        // Test toString format
        String expectedFormat = "'my-label'#[0]";
        String actualFormat = tupleExpr.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test indented format
        expectedFormat = "'my-label'#[0]";
        actualFormat = LangFormatter.SINGLETON.format(tupleExpr);
        assertEquals(expectedFormat, actualFormat);
    }

}
