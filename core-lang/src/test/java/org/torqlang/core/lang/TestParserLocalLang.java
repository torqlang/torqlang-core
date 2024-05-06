/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.Ident;
import org.torqlang.core.klvm.Int32;

import static org.junit.jupiter.api.Assertions.*;
import static org.torqlang.core.lang.CommonTools.*;

public class TestParserLocalLang {

    @Test
    public void testWithOneVar() {
        //                                      1         2
        //                            012345678901234567890123
        Parser p = new Parser("local x::Int32 in x end");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(LocalLang.class, sox);
        LocalLang varSntc = (LocalLang) sox;
        assertSourceSpan(varSntc, 0, 23);
        assertEquals(1, varSntc.varDecls.size());
        assertInstanceOf(IdentVarDecl.class, varSntc.varDecls.get(0));
        IdentVarDecl identVarDecl = (IdentVarDecl) varSntc.varDecls.get(0);
        assertSourceSpan(identVarDecl, 6, 14);
        assertSourceSpan(identVarDecl.identAsPat, 6, 14);
        assertEquals(Ident.create("x"), identVarDecl.identAsPat.ident);
        assertFalse(identVarDecl.identAsPat.escaped);
        assertEquals(Ident.create("Int32"), identVarDecl.identAsPat.typeAnno.ident);
        assertSourceSpan(identVarDecl.identAsPat.typeAnno, 9, 14);
        // Test format
        String expectedFormat = """
            local x::Int32 in
                x
            end""";
        String actualFormat = varSntc.toString();
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testWithTwoVars() {
        //                                      1         2         3
        //                            0123456789012345678901234567890
        Parser p = new Parser("local x::Int32, y = 1 in x end");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(LocalLang.class, sox);
        LocalLang varSntc = (LocalLang) sox;
        assertSourceSpan(varSntc, 0, 30);
        assertEquals(2, varSntc.varDecls.size());
        assertInstanceOf(IdentVarDecl.class, varSntc.varDecls.get(0));
        IdentVarDecl identVarDecl = (IdentVarDecl) varSntc.varDecls.get(0);
        assertSourceSpan(identVarDecl, 6, 14);
        assertSourceSpan(identVarDecl.identAsPat, 6, 14);
        assertEquals(Ident.create("x"), identVarDecl.identAsPat.ident);
        assertFalse(identVarDecl.identAsPat.escaped);
        assertEquals(Ident.create("Int32"), identVarDecl.identAsPat.typeAnno.ident);
        assertSourceSpan(identVarDecl.identAsPat.typeAnno, 9, 14);
        assertInstanceOf(InitVarDecl.class, varSntc.varDecls.get(1));
        assertInstanceOf(InitVarDecl.class, varSntc.varDecls.get(1));
        InitVarDecl initVarDecl = (InitVarDecl) varSntc.varDecls.get(1);
        assertSourceSpan(initVarDecl, 16, 21);
        assertEquals(Ident.create("y"), asIdentAsPat(initVarDecl.varPat).ident);
        assertSourceSpan(initVarDecl.varPat, 16, 17);
        assertEquals(Int32.I32_1, asIntAsExpr(initVarDecl.valueExpr).int64());
        assertSourceSpan(initVarDecl.valueExpr, 20, 21);
        // Test format
        String expectedFormat = """
            local x::Int32, y = 1 in
                x
            end""";
        String actualFormat = varSntc.toString();
        assertEquals(expectedFormat, actualFormat);
    }

}
