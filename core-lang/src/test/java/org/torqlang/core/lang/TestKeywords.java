/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestKeywords {

    @Test
    public void test01() {

        String source = """
            /*start*/ act actor begin break case catch continue do else elseif end eof false finally for func if
            import in local nothing of proc return self skip spawn then throw true try var when while /*stop*/""";
        int begin, end;

        assertFalse(SymbolsAndKeywords.isKeyword(source, 0, 50)); // Invalid size test

        begin = "/*start*/ ".length();
        end = begin + "act".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act ".length();
        end = begin + "actor".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor ".length();
        end = begin + "begin".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin ".length();
        end = begin + "break".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break ".length();
        end = begin + "case".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case ".length();
        end = begin + "catch".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch ".length();
        end = begin + "continue".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue ".length();
        end = begin + "do".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do ".length();
        end = begin + "else".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else ".length();
        end = begin + "elseif".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif ".length();
        end = begin + "end".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end ".length();
        end = begin + "eof".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof ".length();
        end = begin + "false".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof false ".length();
        end = begin + "finally".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof false finally ".length();
        end = begin + "for".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof false finally for ".length();
        end = begin + "func".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof false finally for func ".length();
        end = begin + "if".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof false finally for func if ".length();
        end = begin + "import".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof false finally for func if import ".length();
        end = begin + "in".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof false finally for func if import in ".length();
        end = begin + "local".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof false finally for func if import in local ".length();
        end = begin + "nothing".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof false finally for func if import in local nothing ".length();
        end = begin + "of".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof false finally for func if import in local of nothing ".length();
        end = begin + "proc".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof false finally for func if import in local of nothing proc ".length();
        end = begin + "return".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof false finally for func if import in local of nothing proc return ".length();
        end = begin + "self".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof false finally for func if import in local of nothing proc return self ".length();
        end = begin + "skip".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof false finally for func if import in local of nothing proc return self skip ".length();
        end = begin + "spawn".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof false finally for func if import in local of nothing proc return self skip spawn ".length();
        end = begin + "then".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof false finally for func if import in local of nothing proc return self skip spawn then ".length();
        end = begin + "throw".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof false finally for func if import in local of nothing proc return self skip spawn then throw ".length();
        end = begin + "true".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof false finally for func if import in local of nothing proc return self skip spawn then throw true ".length();
        end = begin + "try".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof false finally for func if import in local of nothing proc return self skip spawn then throw true try ".length();
        end = begin + "var".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof false finally for func if import in local of nothing proc return self skip spawn then throw true try var ".length();
        end = begin + "when".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));

        begin = "/*start*/ act actor begin break case catch continue do else elseif end eof false finally for func if import in local of nothing proc return self skip spawn then throw true try var when ".length();
        end = begin + "while".length();
        assertTrue(SymbolsAndKeywords.isKeyword(source, begin, end));
    }

}
