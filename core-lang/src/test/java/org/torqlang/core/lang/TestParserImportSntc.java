/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.Str;

import static org.junit.jupiter.api.Assertions.*;
import static org.torqlang.core.lang.CommonTools.assertSourceSpan;

public class TestParserImportSntc {

    @Test
    public void testDoubleQualifier() {
        //                         1         2         3
        //               0123456789012345678901234567890
        String source = "import system.module.ArrayList";
        Parser p = new Parser(source);
        SntcOrExpr sox = p.parse();
        assertInstanceOf(ImportSntc.class, sox);
        ImportSntc importSntc = (ImportSntc) sox;
        assertSourceSpan(importSntc, 0, 30);
        assertEquals(Str.of("system.module"), importSntc.qualifier);
        assertEquals(1, importSntc.names.size());
        assertEquals(Str.of("ArrayList"), importSntc.names.get(0).name);
        assertNull(importSntc.names.get(0).alias);
        assertEquals(source, sox.toString());
    }

    @Test
    public void testDoubleQualifierMultiSelection() {
        //                         1         2         3
        //               01234567890123456789012345678901234567
        String source = "import system.module[ArrayList, Cell]";
        Parser p = new Parser(source);
        SntcOrExpr sox = p.parse();
        assertInstanceOf(ImportSntc.class, sox);
        ImportSntc importSntc = (ImportSntc) sox;
        assertSourceSpan(importSntc, 0, 37);
        assertEquals(Str.of("system.module"), importSntc.qualifier);
        assertEquals(2, importSntc.names.size());
        assertEquals(Str.of("ArrayList"), importSntc.names.get(0).name);
        assertNull(importSntc.names.get(0).alias);
        assertEquals(Str.of("Cell"), importSntc.names.get(1).name);
        assertNull(importSntc.names.get(1).alias);
        assertEquals(source, sox.toString());
    }

    @Test
    public void testNoQualifier() {
        //                         1
        //               01234567890123456
        String source = "import ArrayList";
        Parser p = new Parser(source);
        SntcOrExpr sox = p.parse();
        assertInstanceOf(ImportSntc.class, sox);
        ImportSntc importSntc = (ImportSntc) sox;
        assertSourceSpan(importSntc, 0, 16);
        assertEquals(Str.of(""), importSntc.qualifier);
        assertEquals(1, importSntc.names.size());
        assertEquals(Str.of("ArrayList"), importSntc.names.get(0).name);
        assertNull(importSntc.names.get(0).alias);
        assertEquals(source, sox.toString());
    }

    @Test
    public void testNoQualifierMultiSelection() {
        //                         1         2
        //               0123456789012345678901234
        String source = "import [ArrayList, Cell]";
        Parser p = new Parser(source);
        ParserError exc = assertThrows(ParserError.class, p::parse);
        assertEquals("Identifier expected", exc.getMessage());
    }

    @Test
    public void testSingleQualifier() {
        //                         1         2
        //               012345678901234567890123
        String source = "import system.ArrayList";
        Parser p = new Parser(source);
        SntcOrExpr sox = p.parse();
        assertInstanceOf(ImportSntc.class, sox);
        ImportSntc importSntc = (ImportSntc) sox;
        assertSourceSpan(importSntc, 0, 23);
        assertEquals(Str.of("system"), importSntc.qualifier);
        assertEquals(1, importSntc.names.size());
        assertEquals(Str.of("ArrayList"), importSntc.names.get(0).name);
        assertNull(importSntc.names.get(0).alias);
        assertEquals(source, sox.toString());
    }

    @Test
    public void testSingleQualifierMultiSelection() {
        //                         1         2         3
        //               0123456789012345678901234567890
        String source = "import system[ArrayList, Cell]";
        Parser p = new Parser(source);
        SntcOrExpr sox = p.parse();
        assertInstanceOf(ImportSntc.class, sox);
        ImportSntc importSntc = (ImportSntc) sox;
        assertSourceSpan(importSntc, 0, 30);
        assertEquals(Str.of("system"), importSntc.qualifier);
        assertEquals(2, importSntc.names.size());
        assertEquals(Str.of("ArrayList"), importSntc.names.get(0).name);
        assertNull(importSntc.names.get(0).alias);
        assertEquals(Str.of("Cell"), importSntc.names.get(1).name);
        assertNull(importSntc.names.get(1).alias);
        assertEquals(source, sox.toString());
    }

    @Test
    public void testSingleQualifierMultiSelectionAlias() {
        //                         1         2         3         4
        //               012345678901234567890123456789012345678901234567
        String source = "import system[ArrayList as JavaArrayList, Cell]";
        Parser p = new Parser(source);
        SntcOrExpr sox = p.parse();
        assertInstanceOf(ImportSntc.class, sox);
        ImportSntc importSntc = (ImportSntc) sox;
        assertSourceSpan(importSntc, 0, 47);
        assertEquals(Str.of("system"), importSntc.qualifier);
        assertEquals(2, importSntc.names.size());
        assertEquals(Str.of("ArrayList"), importSntc.names.get(0).name);
        assertEquals(Str.of("JavaArrayList"), importSntc.names.get(0).alias);
        assertEquals(Str.of("Cell"), importSntc.names.get(1).name);
        assertNull(importSntc.names.get(1).alias);
        assertEquals(source, sox.toString());

        //                  1         2         3         4
        //        01234567890123456789012345678901234567890
        source = "import system[ArrayList, Cell as MyCell]";
        p = new Parser("import system[ArrayList, Cell as MyCell]");
        sox = p.parse();
        assertInstanceOf(ImportSntc.class, sox);
        importSntc = (ImportSntc) sox;
        assertSourceSpan(importSntc, 0, 40);
        assertEquals(Str.of("system"), importSntc.qualifier);
        assertEquals(2, importSntc.names.size());
        assertEquals(Str.of("ArrayList"), importSntc.names.get(0).name);
        assertNull(importSntc.names.get(0).alias);
        assertEquals(Str.of("Cell"), importSntc.names.get(1).name);
        assertEquals(Str.of("MyCell"), importSntc.names.get(1).alias);
        assertEquals(source, sox.toString());
    }

}
