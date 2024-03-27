/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;
import org.torqlang.core.klvm.Ident;

import static org.junit.Assert.*;
import static org.torqlang.core.lang.CommonTools.*;

public class TestParserTuplePat {

    @Test
    public void testEmpty() {
        //                                      1
        //                            01234567890
        Parser p = new Parser("var [] = x");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof VarSntc);
        VarSntc varSntc = (VarSntc) sox;
        assertSourceSpan(varSntc, 0, 10);
        assertEquals(1, varSntc.varDecls.size());
        InitVarDecl decl = asInitVarDecl(varSntc.varDecls.get(0));
        assertSourceSpan(decl, 4, 10);
        assertTrue(decl.varPat instanceof TuplePat);
        TuplePat tuplePat = (TuplePat) decl.varPat;
        assertSourceSpan(tuplePat, 4, 6);
        // Test label and partial arity
        assertNull(tuplePat.label());
        assertFalse(tuplePat.partialArity());
        // Test features
        assertEquals(0, tuplePat.values().size());
        // Test toString format
        String expectedFormat = "var [] = x";
        String actualFormat = varSntc.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test indented format
        expectedFormat = "var [] = x";
        actualFormat = LangFormatter.SINGLETON.format(varSntc);
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testFeatures1() {
        //                                      1
        //                            012345678901
        Parser p = new Parser("var [a] = x");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof VarSntc);
        VarSntc varSntc = (VarSntc) sox;
        assertSourceSpan(varSntc, 0, 11);
        assertEquals(1, varSntc.varDecls.size());
        InitVarDecl decl = asInitVarDecl(varSntc.varDecls.get(0));
        assertSourceSpan(decl, 4, 11);
        assertTrue(decl.varPat instanceof TuplePat);
        TuplePat tuplePat = (TuplePat) decl.varPat;
        assertSourceSpan(tuplePat, 4, 7);
        // Test label and partial arity
        assertNull(tuplePat.label());
        assertFalse(tuplePat.partialArity());
        // Test features
        assertEquals(1, tuplePat.values().size());
        Pat valuePat = tuplePat.values().get(0);
        assertEquals(Ident.create("a"), asIdentAsPat(valuePat).ident);
        // Test toString format
        String expectedFormat = "var [a] = x";
        String actualFormat = varSntc.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test indented format
        expectedFormat = "var [a] = x";
        actualFormat = LangFormatter.SINGLETON.format(varSntc);
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testFeatures1PartialArity() {
        //                                      1
        //                            01234567890123456
        Parser p = new Parser("var [a, ...] = x");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof VarSntc);
        VarSntc varSntc = (VarSntc) sox;
        assertSourceSpan(varSntc, 0, 16);
        assertEquals(1, varSntc.varDecls.size());
        InitVarDecl decl = asInitVarDecl(varSntc.varDecls.get(0));
        assertSourceSpan(decl, 4, 16);
        assertTrue(decl.varPat instanceof TuplePat);
        TuplePat tuplePat = (TuplePat) decl.varPat;
        assertSourceSpan(tuplePat, 4, 12);
        // Test label and partial arity
        assertNull(tuplePat.label());
        assertTrue(tuplePat.partialArity());
        // Test features
        assertEquals(1, tuplePat.values().size());
        Pat valuePat = tuplePat.values().get(0);
        assertEquals(Ident.create("a"), asIdentAsPat(valuePat).ident);
        // Test toString format
        String expectedFormat = "var [a, ...] = x";
        String actualFormat = varSntc.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test indented format
        expectedFormat = "var [a, ...] = x";
        actualFormat = LangFormatter.SINGLETON.format(varSntc);
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testFeatures2() {
        //                                      1
        //                            012345678901234
        Parser p = new Parser("var [a, b] = x");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof VarSntc);
        VarSntc varSntc = (VarSntc) sox;
        assertSourceSpan(varSntc, 0, 14);
        assertEquals(1, varSntc.varDecls.size());
        InitVarDecl decl = asInitVarDecl(varSntc.varDecls.get(0));
        assertSourceSpan(decl, 4, 14);
        assertTrue(decl.varPat instanceof TuplePat);
        TuplePat tuplePat = (TuplePat) decl.varPat;
        assertSourceSpan(tuplePat, 4, 10);
        // Test label and partial arity
        assertNull(tuplePat.label());
        assertFalse(tuplePat.partialArity());
        // Test features
        assertEquals(2, tuplePat.values().size());
        Pat valuePat = tuplePat.values().get(0);
        assertEquals(Ident.create("a"), asIdentAsPat(valuePat).ident);
        valuePat = tuplePat.values().get(1);
        assertEquals(Ident.create("b"), asIdentAsPat(valuePat).ident);
        // Test toString format
        String expectedFormat = "var [a, b] = x";
        String actualFormat = varSntc.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test indented format
        expectedFormat = "var [a, b] = x";
        actualFormat = LangFormatter.SINGLETON.format(varSntc);
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testFeatures2PartialArity() {
        //                                      1
        //                            01234567890123456789
        Parser p = new Parser("var [a, b, ...] = x");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof VarSntc);
        VarSntc varSntc = (VarSntc) sox;
        assertSourceSpan(varSntc, 0, 19);
        assertEquals(1, varSntc.varDecls.size());
        InitVarDecl decl = asInitVarDecl(varSntc.varDecls.get(0));
        assertSourceSpan(decl, 4, 19);
        assertTrue(decl.varPat instanceof TuplePat);
        TuplePat tuplePat = (TuplePat) decl.varPat;
        assertSourceSpan(tuplePat, 4, 15);
        // Test label and partial arity
        assertNull(tuplePat.label());
        assertTrue(tuplePat.partialArity());
        // Test features
        assertEquals(2, tuplePat.values().size());
        Pat valuePat = tuplePat.values().get(0);
        assertEquals(Ident.create("a"), asIdentAsPat(valuePat).ident);
        valuePat = tuplePat.values().get(1);
        assertEquals(Ident.create("b"), asIdentAsPat(valuePat).ident);
        // Test toString format
        String expectedFormat = "var [a, b, ...] = x";
        String actualFormat = varSntc.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test indented format
        expectedFormat = "var [a, b, ...] = x";
        actualFormat = LangFormatter.SINGLETON.format(varSntc);
        assertEquals(expectedFormat, actualFormat);
    }

}
