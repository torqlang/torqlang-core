/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.util.SourceSpan;
import org.torqlang.core.util.StringTools;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommonTools {

    static BoolAsExpr asBoolAsExpr(Object value) {
        assertTrue(value instanceof BoolAsExpr);
        return (BoolAsExpr) value;
    }

    static BoolAsPat asBoolAsPat(Object value) {
        assertTrue(value instanceof BoolAsPat);
        return (BoolAsPat) value;
    }

    static CharAsExpr asCharAsExpr(Object value) {
        assertTrue(value instanceof CharAsExpr);
        return (CharAsExpr) value;
    }

    static Dec128AsExpr asDec128AsExpr(Object value) {
        assertTrue(value instanceof Dec128AsExpr);
        return (Dec128AsExpr) value;
    }

    static EofAsExpr asEofAsExpr(Object value) {
        assertTrue(value instanceof EofAsExpr);
        return (EofAsExpr) value;
    }

    static EofAsPat asEofAsPat(Object value) {
        assertTrue(value instanceof EofAsPat);
        return (EofAsPat) value;
    }

    static FeatureAsPat asFeatureAsPat(Object value) {
        assertTrue(value instanceof FeatureAsPat);
        return (FeatureAsPat) value;
    }

    static FltAsExpr asFltAsExpr(Object value) {
        assertTrue(value instanceof FltAsExpr);
        return (FltAsExpr) value;
    }

    static GroupExpr asGroupExpr(Object value) {
        assertTrue(value instanceof GroupExpr);
        return (GroupExpr) value;
    }

    static IdentAsExpr asIdentAsExpr(Object value) {
        assertTrue(value instanceof IdentAsExpr);
        return (IdentAsExpr) value;
    }

    static IdentAsPat asIdentAsPat(Object value) {
        assertTrue(value instanceof IdentAsPat);
        return (IdentAsPat) value;
    }

    static InitVarDecl asInitVarDecl(Object value) {
        assertTrue(value instanceof InitVarDecl);
        return (InitVarDecl) value;
    }

    static IntAsExpr asIntAsExpr(Object value) {
        assertTrue(value instanceof IntAsExpr);
        return (IntAsExpr) value;
    }

    static IntAsPat asIntAsPat(Object value) {
        assertTrue(value instanceof IntAsPat);
        return (IntAsPat) value;
    }

    static NothingAsExpr asNothingAsExpr(Object value) {
        assertTrue(value instanceof NothingAsExpr);
        return (NothingAsExpr) value;
    }

    static NothingAsPat asNothingAsPat(Object value) {
        assertTrue(value instanceof NothingAsPat);
        return (NothingAsPat) value;
    }

    static SntcOrExpr asSingleExpr(Object value) {
        if (value instanceof GroupExpr) {
            return asSingleExprFromGroupExpr(value);
        }
        return asSingleExprFromSeqLang(value);
    }

    static SntcOrExpr asSingleExprFromGroupExpr(Object value) {
        assertTrue(value instanceof GroupExpr);
        GroupExpr groupExpr = (GroupExpr) value;
        assertTrue(groupExpr.expr instanceof SeqLang);
        SeqLang seqLang = (SeqLang) groupExpr.expr;
        assertEquals(1, seqLang.list.size());
        return seqLang.list.get(0);
    }

    static SntcOrExpr asSingleExprFromSeqLang(Object value) {
        assertTrue(value instanceof SeqLang);
        SeqLang seqLang = (SeqLang) value;
        assertEquals(1, seqLang.list.size());
        return seqLang.list.get(0);
    }

    static StrAsExpr asStrAsExpr(Object value) {
        assertTrue(value instanceof StrAsExpr);
        return (StrAsExpr) value;
    }

    static StrAsPat asStrAsPat(Object value) {
        assertTrue(value instanceof StrAsPat);
        return (StrAsPat) value;
    }

    static UnaryExpr asUnaryExpr(Object value) {
        assertTrue(value instanceof UnaryExpr);
        return (UnaryExpr) value;
    }

    static void assertSourceSpan(SourceSpan sourceSpan, int begin, int end) {
        assertEquals(begin, sourceSpan.begin());
        assertEquals(end, sourceSpan.end());
    }

    // NOTE: This method is duplicated at test org.torqlang.core.local
    public static String stripCircularSpecifics(String kernelString) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < kernelString.length()) {
            char next = kernelString.charAt(i);
            if (next == '<' && i + 1 < kernelString.length() && kernelString.charAt(i + 1) == '<') {
                sb.append("<<$circular");
                i += 2;
                while (i < kernelString.length()) {
                    next = kernelString.charAt(i);
                    if (next == '>' && i + 1 < kernelString.length() && kernelString.charAt(i + 1) == '>') {
                        sb.append(">>");
                        i += 2;
                        break;
                    } else {
                        i++;
                    }
                }
            } else {
                sb.append(next);
                i++;
            }
        }
        return sb.toString();
    }

    static <T, R> R testValue(T argument, Function<T, R> function) {
        return function.apply(argument);
    }

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    static void toStringWithEscapedControlCodes(String s, StringBuilder sb) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '\u0020') {
                sb.append("<<");
                StringTools.appendHexString(c, sb);
                sb.append(">>");
            } else {
                sb.append(c);
            }
        }
    }

}
