/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;
import org.torqlang.core.klvm.*;

import static org.junit.Assert.*;
import static org.torqlang.core.lang.CommonTools.*;

public class TestParserRecExpr {

    @Test
    public void testEmpty() {
        //                            012
        Parser p = new Parser("{}");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof RecExpr);
        RecExpr recExpr = (RecExpr) sox;
        assertSourceSpan(recExpr, 0, 2);
        assertNull(recExpr.label());
        assertEquals(0, recExpr.fields().size());
        // Test toString format
        String expectedFormat = "{}";
        String actualFormat = recExpr.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test indented format
        expectedFormat = "{}";
        actualFormat = LangFormatter.SINGLETON.format(recExpr);
        assertEquals(expectedFormat, actualFormat);
        // Test complete
        CompleteRec completeRec = recExpr.checkComplete();
        assertNotNull(completeRec);
        assertEquals(Rec.DEFAULT_LABEL, completeRec.label());
        assertEquals(0, completeRec.fieldCount());
    }

    @Test
    public void testFeatures1() {
        Str zeroFeat = Str.of("0-feat");
        //                                      1
        //                            01234567890123
        Parser p = new Parser("{'0-feat': 0}");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof RecExpr);
        RecExpr recExpr = (RecExpr) sox;
        assertSourceSpan(recExpr, 0, 13);
        assertNull(recExpr.label());
        // Test features
        assertEquals(1, recExpr.fields().size());
        FieldExpr fieldExpr = recExpr.fields().get(0);
        assertSourceSpan(fieldExpr, 1, 12);
        assertEquals(zeroFeat, asStrAsExpr(fieldExpr.feature).str);
        assertEquals(Int32.I32_0, asIntAsExpr(fieldExpr.value).int64());
        // Test toString format
        String expectedFormat = "{'0-feat': 0}";
        String actualFormat = recExpr.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test indented format
        expectedFormat = "{'0-feat': 0}";
        actualFormat = LangFormatter.SINGLETON.format(recExpr);
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testFeatures2() {
        Str zeroFeat = Str.of("0-feat");
        Str oneFeat = Str.of("1-feat");
        //                                      1         2
        //                            012345678901234567890123456
        Parser p = new Parser("{'0-feat': 0, '1-feat': 1}");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof RecExpr);
        RecExpr recExpr = (RecExpr) sox;
        assertSourceSpan(recExpr, 0, 26);
        assertNull(recExpr.label());
        // Test features
        assertEquals(2, recExpr.fields().size());
        FieldExpr fieldExpr = recExpr.fields().get(0);
        assertSourceSpan(fieldExpr, 1, 12);
        assertEquals(zeroFeat, asStrAsExpr(fieldExpr.feature).str);
        assertEquals(Int32.I32_0, asIntAsExpr(fieldExpr.value).int64());
        fieldExpr = recExpr.fields().get(1);
        assertSourceSpan(fieldExpr, 14, 25);
        assertEquals(oneFeat, asStrAsExpr(fieldExpr.feature).str);
        assertEquals(Int32.I32_1, asIntAsExpr(fieldExpr.value).int64());
        // Test toString format
        String expectedFormat = "{'0-feat': 0, '1-feat': 1}";
        String actualFormat = recExpr.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test indented format
        expectedFormat = "{'0-feat': 0, '1-feat': 1}";
        actualFormat = LangFormatter.SINGLETON.format(recExpr);
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testFeatures1WithLabel() {
        Str zeroFeat = Str.of("0-feat");
        //                                      1         2
        //                            0123456789012345678901234
        Parser p = new Parser("'my-label'#{'0-feat': 0}");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof RecExpr);
        RecExpr recExpr = (RecExpr) sox;
        assertSourceSpan(recExpr, 0, 24);
        assertEquals(Str.of("my-label"), asStrAsExpr(recExpr.label()).str);
        // Test features
        assertEquals(1, recExpr.fields().size());
        FieldExpr fieldExpr = recExpr.fields().get(0);
        assertSourceSpan(fieldExpr, 12, 23);
        assertEquals(zeroFeat, asStrAsExpr(fieldExpr.feature).str);
        assertEquals(Int32.I32_0, asIntAsExpr(fieldExpr.value).int64());
        // Test toString format
        String expectedFormat = "'my-label'#{'0-feat': 0}";
        String actualFormat = recExpr.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test indented format
        expectedFormat = "'my-label'#{'0-feat': 0}";
        actualFormat = LangFormatter.SINGLETON.format(recExpr);
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testStaticComplete() {

        // ValueAsExpr
        Parser p = new Parser("0");
        SntcOrExpr sox = p.parse();
        IntAsExpr intAsExpr = CommonTools.asIntAsExpr(sox);
        Complete complete = RecExpr.checkComplete(intAsExpr);
        assertTrue(complete instanceof Int32);

        // An identifier that is an unknown value
        p = new Parser("a");
        sox = p.parse();
        complete = RecExpr.checkComplete(sox);
        assertNull(complete);

        // RecExpr
        p = new Parser("'my-label'#{'0-feat': 0}");
        sox = p.parse();
        RecExpr recExpr = (RecExpr) sox;
        complete = RecExpr.checkComplete(recExpr);
        assertTrue(complete instanceof CompleteRec);

        // RecExpr with label identifier
        p = new Parser("x#{'0-feat': 0}");
        sox = p.parse();
        recExpr = (RecExpr) sox;
        complete = RecExpr.checkComplete(recExpr);
        assertNull(complete);

        // RecExpr with feature identifier
        p = new Parser("'my-label'#{x: 0}");
        sox = p.parse();
        recExpr = (RecExpr) sox;
        complete = RecExpr.checkComplete(recExpr);
        assertNull(complete);

        // RecExpr with value identifier
        p = new Parser("'my-label'#{'0-feat': x}");
        sox = p.parse();
        recExpr = (RecExpr) sox;
        complete = RecExpr.checkComplete(recExpr);
        assertNull(complete);

        // TupleExpr
        p = new Parser("'my-label'#[0]");
        sox = p.parse();
        TupleExpr tupleExpr = (TupleExpr) sox;
        complete = RecExpr.checkComplete(tupleExpr);
        assertTrue(complete instanceof CompleteTuple);

        // TupleExpr with label identifier
        p = new Parser("x#[0]");
        sox = p.parse();
        tupleExpr = (TupleExpr) sox;
        complete = RecExpr.checkComplete(tupleExpr);
        assertNull(complete);

        // TupleExpr with value identifier
        p = new Parser("'my-label'#[x]");
        sox = p.parse();
        tupleExpr = (TupleExpr) sox;
        complete = RecExpr.checkComplete(tupleExpr);
        assertNull(complete);
    }

}
