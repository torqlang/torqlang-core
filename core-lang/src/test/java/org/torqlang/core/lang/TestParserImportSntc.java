/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.Str;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.torqlang.core.lang.CommonTools.assertSourceSpan;

public class TestParserImportSntc {

    @Test
    public void testDoubleQualifier() {
        //                                      1         2        3
        //                            0123456789012345678901234567890
        Parser p = new Parser("import system.module.ArrayList");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(ImportSntc.class, sox);
        ImportSntc importExpr = (ImportSntc) sox;
        assertSourceSpan(importExpr, 0, 30);
        assertEquals(Str.of("system.module"), importExpr.qualifier);
        assertEquals(List.of(Str.of("ArrayList")), importExpr.selections);
    }

    @Test
    public void testDoubleQualifierMultiSelection() {
        //                                      1         2         3
        //                            01234567890123456789012345678901234567
        Parser p = new Parser("import system.module[ArrayList, Cell]");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(ImportSntc.class, sox);
        ImportSntc importExpr = (ImportSntc) sox;
        assertSourceSpan(importExpr, 0, 37);
        assertEquals(Str.of("system.module"), importExpr.qualifier);
        assertEquals(List.of(Str.of("ArrayList"), Str.of("Cell")), importExpr.selections);
    }

    @Test
    public void testNoQualifier() {
        //                                      1
        //                            01234567890123456
        Parser p = new Parser("import ArrayList");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(ImportSntc.class, sox);
        ImportSntc importExpr = (ImportSntc) sox;
        assertSourceSpan(importExpr, 0, 16);
        assertEquals(Str.of(""), importExpr.qualifier);
        assertEquals(List.of(Str.of("ArrayList")), importExpr.selections);
    }

    @Test
    public void testNoQualifierMultiSelection() {
        //                                      1         2
        //                            0123456789012345678901234
        Parser p = new Parser("import [ArrayList, Cell]");
        ParserError exc = assertThrows(ParserError.class, p::parse);
        assertEquals("Identifier expected", exc.getMessage());
    }

    @Test
    public void testSingleQualifier() {
        //                                      1         2
        //                            012345678901234567890123
        Parser p = new Parser("import system.ArrayList");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(ImportSntc.class, sox);
        ImportSntc importSntc = (ImportSntc) sox;
        assertSourceSpan(importSntc, 0, 23);
        assertEquals(Str.of("system"), importSntc.qualifier);
        assertEquals(List.of(Str.of("ArrayList")), importSntc.selections);
    }

    @Test
    public void testSingleQualifierMultiSelection() {
        //                                      1         2         3
        //                            0123456789012345678901234567890
        Parser p = new Parser("import system[ArrayList, Cell]");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(ImportSntc.class, sox);
        ImportSntc importSntc = (ImportSntc) sox;
        assertSourceSpan(importSntc, 0, 30);
        assertEquals(Str.of("system"), importSntc.qualifier);
        assertEquals(List.of(Str.of("ArrayList"), Str.of("Cell")), importSntc.selections);
    }

}
