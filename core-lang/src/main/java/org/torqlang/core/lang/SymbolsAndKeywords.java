/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import java.util.Arrays;

public final class SymbolsAndKeywords {

    static final char ACCESS_CELL_VALUE_OPER_CHAR = '@';
    static final char ADD_OPER_CHAR = '+';
    static final char BACKTICK_QUOTE_CHAR = '`';
    static final char COLON_CHAR = ':';
    static final char COMMA_CHAR = ',';
    static final char DIVIDE_OPER_CHAR = '/';
    static final char DOUBLE_QUOTE_CHAR = '"';
    static final char DOT_OPER_CHAR = '.';
    static final char GREATER_THAN_OPER_CHAR = '>';
    static final char HASH_TAG_CHAR = '#';
    static final char IDENT_ESC_CHAR = '~';
    static final char L_BRACE_CHAR = '{';
    static final char L_BRACKET_CHAR = '[';
    static final char L_PAREN_CHAR = '(';
    static final char LESS_THAN_OPER_CHAR = '<';
    static final char MODULO_OPER_CHAR = '%';
    static final char MULTIPLY_OPER_CHAR = '*';
    static final char NOT_OPER_CHAR = '!';
    static final char R_BRACE_CHAR = '}';
    static final char R_BRACKET_CHAR = ']';
    static final char R_PAREN_CHAR = ')';
    static final char SEMICOLON_CHAR = ';';
    static final char SINGLE_QUOTE_CHAR = '\'';
    static final char SUBTRACT_OPER_CHAR = '-';
    static final char UNIFY_OPER_CHAR = '=';

    static final String AND_OPER = "&&";
    static final String ASSIGN_CELL_VALUE_OPER = ":=";
    static final String EQUAL_TO_OPER = "==";
    static final String GREATER_THAN_OR_EQUAL_TO_OPER = ">=";
    static final String LESS_THAN_OR_EQUAL_TO_OPER = "<=";
    static final String NOT_EQUAL_TO_OPER = "!=";
    static final String OR_OPER = "||";
    static final String RETURN_TYPE_OPER = "->";
    static final String SUBTYPE_OPER = "<:";
    static final String SUPERTYPE_OPER = ">:";
    static final String TYPE_OPER = "::";

    static final String ACT_VALUE = "act";
    static final String ACTOR_VALUE = "actor";
    static final String AS_VALUE = "as";
    static final String ASK_VALUE = "ask";
    static final String BEGIN_VALUE = "begin";
    static final String BREAK_VALUE = "break";
    static final String CASE_VALUE = "case";
    static final String CATCH_VALUE = "catch";
    static final String CONTINUE_VALUE = "continue";
    static final String DO_VALUE = "do";
    static final String ELSE_VALUE = "else";
    static final String ELSEIF_VALUE = "elseif";
    static final String END_VALUE = "end";
    static final String EOF_VALUE = "eof";
    static final String FALSE_VALUE = "false";
    static final String FINALLY_VALUE = "finally";
    static final String FOR_VALUE = "for";
    static final String FUNC_VALUE = "func";
    static final String HANDLE_VALUE = "handle";
    static final String IF_VALUE = "if";
    static final String IMPORT_VALUE = "import";
    static final String IN_VALUE = "in";
    static final String LOCAL_VALUE = "local";
    static final String NULL_VALUE = "null";
    static final String OF_VALUE = "of";
    static final String PROC_VALUE = "proc";
    static final String RETURN_VALUE = "return";
    static final String SELF_VALUE = "self";
    static final String SKIP_VALUE = "skip";
    static final String SPAWN_VALUE = "spawn";
    static final String TELL_VALUE = "tell";
    static final String THEN_VALUE = "then";
    static final String THROW_VALUE = "throw";
    static final String TRUE_VALUE = "true";
    static final String TRY_VALUE = "try";
    static final String VAR_VALUE = "var";
    static final String WHEN_VALUE = "when";
    static final String WHILE_VALUE = "while";

    static final String PARTIAL_ARITY_OPER = "...";

    private static final String[][] KEYWORDS_BY_LENGTH = new String[][]{
        {},
        {},
        {DO_VALUE, IF_VALUE, IN_VALUE, OF_VALUE},
        {ACT_VALUE, END_VALUE, EOF_VALUE, FOR_VALUE, TRY_VALUE, VAR_VALUE},
        {CASE_VALUE, ELSE_VALUE, FUNC_VALUE, NULL_VALUE, PROC_VALUE, SELF_VALUE, SKIP_VALUE, THEN_VALUE, TRUE_VALUE, WHEN_VALUE},
        {ACTOR_VALUE, BEGIN_VALUE, BREAK_VALUE, CATCH_VALUE, FALSE_VALUE, LOCAL_VALUE, SPAWN_VALUE, THROW_VALUE, WHILE_VALUE},
        {ELSEIF_VALUE, IMPORT_VALUE, RETURN_VALUE},
        {FINALLY_VALUE},
        {CONTINUE_VALUE}
    };

    private static final char[] ONE_CHAR_SYMBOLS_SORTED;

    static {
        char[] symbols = new char[]{
            ACCESS_CELL_VALUE_OPER_CHAR, ADD_OPER_CHAR, BACKTICK_QUOTE_CHAR, COLON_CHAR, COMMA_CHAR, DIVIDE_OPER_CHAR,
            DOUBLE_QUOTE_CHAR, DOT_OPER_CHAR, GREATER_THAN_OPER_CHAR, HASH_TAG_CHAR, IDENT_ESC_CHAR, L_BRACE_CHAR,
            L_BRACKET_CHAR, L_PAREN_CHAR, LESS_THAN_OPER_CHAR, MODULO_OPER_CHAR, MULTIPLY_OPER_CHAR, NOT_OPER_CHAR,
            R_BRACE_CHAR, R_BRACKET_CHAR, R_PAREN_CHAR, SEMICOLON_CHAR, SINGLE_QUOTE_CHAR, SUBTRACT_OPER_CHAR,
            UNIFY_OPER_CHAR
        };
        Arrays.sort(symbols);
        ONE_CHAR_SYMBOLS_SORTED = symbols;
    }

    private SymbolsAndKeywords() {
    }

    public static boolean isContextualKeyword(String source, int begin, int end) {
        return substringEquals(source, begin, end, HANDLE_VALUE) ||
            substringEquals(source, begin, end, ASK_VALUE) ||
            substringEquals(source, begin, end, TELL_VALUE) ||
            substringEquals(source, begin, end, AS_VALUE);
    }

    public static boolean isKeyword(String source, int begin, int end) {
        int length = end - begin;
        if (length >= KEYWORDS_BY_LENGTH.length) {
            return false;
        }
        String[] keywords = KEYWORDS_BY_LENGTH[length];
        char firstSourceChar = source.charAt(begin);
        for (String keyword : keywords) {
            char firstKeywordChar = keyword.charAt(0);
            if (firstKeywordChar < firstSourceChar) {
                continue;
            }
            if (firstKeywordChar > firstSourceChar) {
                return false;
            }
            if (substringEquals(source, begin, end, keyword)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOneCharSymbol(char c) {
        return Arrays.binarySearch(ONE_CHAR_SYMBOLS_SORTED, c) > -1;
    }

    public static boolean isThreeCharSymbol(char c1, char c2, char c3) {
        // PARTIAL_ARITY: '...';
        return c1 == '.' && c2 == '.' && c3 == '.';
    }

    public static boolean isTwoCharSymbol(char c1, char c2) {
        // '=='
        if (c1 == '=') {
            return c2 == '=';
        }
        // '!='
        if (c1 == '!') {
            return c2 == '=';
        }
        // '>='
        // '>:'
        if (c1 == '>') {
            return c2 == '=' || c2 == ':';
        }
        // '<='
        // '<:'
        if (c1 == '<') {
            return c2 == '=' || c2 == ':';
        }
        // '&&'
        if (c1 == '&') {
            return c2 == '&';
        }
        // '||'
        if (c1 == '|') {
            return c2 == '|';
        }
        // ':='
        // '::'
        if (c1 == ':') {
            return c2 == '=' || c2 == ':';
        }
        // '->'
        if (c1 == '-') {
            return c2 == '>';
        }
        return false;
    }

    private static boolean substringEquals(String source, int begin, int end, String value) {
        if (end - begin != value.length()) {
            return false;
        }
        int x = 0;
        for (int i = begin; i < end; i++) {
            if (source.charAt(i) != value.charAt(x)) {
                return false;
            }
            x++;
        }
        return true;
    }

}
