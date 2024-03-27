/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

/*
 * JSON Syntax Grammar
 * ===================
 *
 * McKeeman Form is a notation for expressing grammars. It was proposed by Bill McKeeman of
 * Dartmouth College. It is a simplified Backus-Naur Form with significant whitespace and minimal
 * use of metacharacters. See https://www.crockford.com/mckeeman.html
 *
 * json
 *     element
 *
 * value
 *     object
 *     array
 *     string
 *     number
 *     "true"
 *     "false"
 *     "null"
 *
 * object
 *     '{' ws '}'
 *     '{' members '}'
 *
 * members
 *     member
 *     member ',' members
 *
 * member
 *     ws string ws ':' element
 *
 * array
 *     '[' ws ']'
 *     '[' elements ']'
 *
 * elements
 *     element
 *     element ',' elements
 *
 * element
 *     ws value ws
 *
 * string
 *     '"' characters '"'
 *
 * characters
 *     ""
 *     character characters
 *
 * character
 *     '0020' . '10FFFF' - '"' - '\'
 *     '\' escape
 *
 * escape
 *     '"'
 *     '\'
 *     '/'
 *     'b'
 *     'f'
 *     'n'
 *     'r'
 *     't'
 *     'u' hex hex hex hex
 *
 * hex
 *     digit
 *     'A' . 'F'
 *     'a' . 'f'
 *
 * number
 *     integer fraction exponent
 *
 * integer
 *     digit
 *     onenine digits
 *     '-' digit
 *     '-' onenine digits
 *
 * digits
 *     digit
 *     digit digits
 *
 * digit
 *     '0'
 *     onenine
 *
 * onenine
 *     '1' . '9'
 *
 * fraction
 *     ""
 *     '.' digits
 *
 * exponent
 *     ""
 *     'E' sign digits
 *     'e' sign digits
 *
 * sign
 *     ""
 *     '+'
 *     '-'
 *
 * ws
 *     ""
 *     '0020' ws
 *     '000A' ws
 *     '000D' ws
 *     '0009' ws
 *
 * Unicode Character Ranges
 * ========================
 *
 * Control characters are in the inclusive range \u0000 through \u009F.
 *
 * Common escape sequences:
 *     \ b (backspace BS, Unicode \u0008)
 *     \ t (horizontal tab HT, Unicode \u0009)
 *     \ n (linefeed LF, Unicode \u000a)
 *     \ f (form feed FF, Unicode \u000c)
 *     \ r (carriage return CR, Unicode \u000d)
 *     \ " (double quote ", Unicode \u0022)
 *     \ ' (single quote ', Unicode \u0027)
 *     \ \ (backslash \, Unicode \u005c)
 *
 * Java Rules for Escaped Characters
 * =================================
 *
 * Java Language Specification, section 3.10.7.
 *
 * It is a compile-time error if the character following a backslash in an escape sequence
 * is not a LineTerminator or an ASCII b, s, t, n, f, r, ", ', \, 0, 1, 2, 3, 4, 5, 6, or 7.
 *
 * LineTerminator:
 *     the ASCII LF character, also known as "newline"
 *     the ASCII CR character, also known as "return"
 *     the ASCII CR character followed by the ASCII LF character
 */
public class JsonLexer {

    public static final String INVALID_BOOLEAN_EXPRESSION = "Invalid boolean expression";
    public static final String INVALID_FLOATING_POINT_EXPRESSION = "Invalid floating point expression";
    public static final String INVALID_KEYWORD_EXPRESSION = "Invalid keyword -- not a true, false, or null";
    public static final String INVALID_NULL_EXPRESSION = "Invalid null expression";
    public static final String INVALID_NUMBER_EXPRESSION = "Invalid number expression";
    public static final String STRING_IS_MISSING_CLOSING_QUOTE = "String is missing closing quote";

    private final String source;
    private int charPos = 0;

    public JsonLexer(String source) {
        this.source = source;
    }

    public final int charPos() {
        return charPos;
    }

    private boolean isDelimiter(char c) {
        if (c == ',') {
            return true;
        }
        if (c == '{') {
            return true;
        }
        if (c == '}') {
            return true;
        }
        if (c == '[') {
            return true;
        }
        if (c == ']') {
            return true;
        }
        return c == ':';
    }

    private boolean isDelimiterAt(int index) {
        return index < source.length() && isDelimiter(source.charAt(index));
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isWhiteSpace(char c) {
        //noinspection UnnecessaryUnicodeEscape
        if (c > '\u0020') {
            return false;
        }
        if (c == ' ') {
            return true;
        }
        if (c == '\r') {
            return true;
        }
        if (c == '\n') {
            return true;
        }
        if (c == '\t') {
            return true;
        }
        if (c == '\f') {
            return true;
        }
        return c == '\b';
    }

    private boolean isWhiteSpaceAt(int index) {
        return index < source.length() && isWhiteSpace(source.charAt(index));
    }

    public final JsonLexerToken nextToken() {
        skipWhitespace();
        return parseToken();
    }

    private JsonLexerToken parseFractionalPart(int start, int stop) {
        // Accept first digit [0-9] and continue accepting digits until a non-digit is found
        while (++stop < source.length()) {
            if (!isDigit(source.charAt(stop))) {
                break;
            }
        }
        if (stop == source.length() || isWhiteSpaceAt(stop) || isDelimiterAt(stop)) {
            charPos = stop;
            return new JsonLexerToken(JsonLexerTokenType.NUMBER, source, start, stop);
        }
        char c = source.charAt(stop);
        if (c == 'e' || c == 'E') {
            // Accept start of exponent
            stop++;
            c = source.charAt(stop);
            if (c == '+' || c == '-') {
                // Accept sign of exponent
                stop++;
                c = source.charAt(stop);
                if (isDigit(c)) {
                    while (++stop < source.length()) {
                        if (!isDigit(source.charAt(stop))) {
                            break;
                        }
                    }
                    charPos = stop;
                    return new JsonLexerToken(JsonLexerTokenType.NUMBER, source, start, stop);
                }
            }
        }
        // We have a nonsensical fractional part
        charPos = stop + 1; // accept the nonsensical char as part of the value
        JsonLexerToken invalidToken = new JsonLexerToken(JsonLexerTokenType.INVALID, source, start, charPos);
        throw new JsonLexerException(invalidToken, INVALID_NUMBER_EXPRESSION);
    }

    public final JsonLexerToken parseNumber() {
        int start = charPos;
        int stop = start + 1;
        while (stop < source.length() && isDigit(source.charAt(stop))) {
            stop++;
        }
        if (stop < source.length() && source.charAt(stop) == '.') {
            stop++; // accept the '.' char
            if (stop == source.length()) {
                // We have a nonsensical token at end-of-file: digits followed by a period
                charPos = stop;
                JsonLexerToken invalidToken = new JsonLexerToken(JsonLexerTokenType.NUMBER, source, start, charPos);
                throw new JsonLexerException(invalidToken, INVALID_FLOATING_POINT_EXPRESSION);
            }
            char c = source.charAt(stop);
            if (!isDigit(c)) {
                // We have a nonsensical token: digits and a period followed by a non-digit
                charPos = stop + 1; // accept the invalid character
                JsonLexerToken invalidToken = new JsonLexerToken(JsonLexerTokenType.NUMBER, source, start, charPos);
                throw new JsonLexerException(invalidToken, INVALID_FLOATING_POINT_EXPRESSION);
            }
            return parseFractionalPart(start, stop);
        }
        if (stop == source.length() || isWhiteSpaceAt(stop) || isDelimiterAt(stop)) {
            charPos = stop;
            return new JsonLexerToken(JsonLexerTokenType.NUMBER, source, start, charPos);
        }
        charPos = stop + 1; // accept the nonsensical char as part of the value
        JsonLexerToken invalidToken = new JsonLexerToken(JsonLexerTokenType.INVALID, source, start, charPos);
        throw new JsonLexerException(invalidToken, INVALID_NUMBER_EXPRESSION);
    }

    public final JsonLexerToken parseOther() {
        int start = charPos;
        int stop = start + 1;
        if (source.charAt(start) == 't') {
            if (source.charAt(stop++) == 'r') {
                if (source.charAt(stop++) == 'u') {
                    if (source.charAt(stop++) == 'e') {
                        if (stop == source.length() || isWhiteSpaceAt(stop) || isDelimiterAt(stop)) {
                            charPos = stop;
                            return new JsonLexerToken(JsonLexerTokenType.BOOLEAN, source, start, stop);
                        }
                        charPos = stop + 1; // accept the nonsensical char as part of the value
                        JsonLexerToken invalidToken = new JsonLexerToken(JsonLexerTokenType.INVALID, source, start, charPos);
                        throw new JsonLexerException(invalidToken, INVALID_BOOLEAN_EXPRESSION);
                    }
                }
            }
        }
        if (source.charAt(start) == 'f') {
            if (source.charAt(stop++) == 'a') {
                if (source.charAt(stop++) == 'l') {
                    if (source.charAt(stop++) == 's') {
                        if (source.charAt(stop++) == 'e') {
                            if (stop == source.length() || isWhiteSpaceAt(stop) || isDelimiterAt(stop)) {
                                charPos = stop;
                                return new JsonLexerToken(JsonLexerTokenType.BOOLEAN, source, start, stop);
                            }
                            charPos = stop + 1; // accept the nonsensical char as part of the value
                            JsonLexerToken invalidToken = new JsonLexerToken(JsonLexerTokenType.INVALID, source, start, charPos);
                            throw new JsonLexerException(invalidToken, INVALID_BOOLEAN_EXPRESSION);
                        }
                    }
                }
            }
        }
        if (source.charAt(start) == 'n') {
            if (source.charAt(stop++) == 'u') {
                if (source.charAt(stop++) == 'l') {
                    if (source.charAt(stop++) == 'l') {
                        if (stop == source.length() || isWhiteSpaceAt(stop) || isDelimiterAt(stop)) {
                            charPos = stop;
                            return new JsonLexerToken(JsonLexerTokenType.NULL, source, start, stop);
                        }
                        charPos = stop + 1; // accept the nonsensical char as part of the value
                        JsonLexerToken invalidToken = new JsonLexerToken(JsonLexerTokenType.INVALID, source, start, charPos);
                        throw new JsonLexerException(invalidToken, INVALID_NULL_EXPRESSION);
                    }
                }
            }
        }
        throw new IllegalArgumentException(INVALID_KEYWORD_EXPRESSION);
    }

    private JsonLexerToken parseString() {
        int start = charPos;
        charPos++;
        while (charPos < source.length()) {
            if (source.charAt(charPos) == '"') {
                if (source.charAt(charPos - 1) != '\\') {
                    break;
                }
            }
            charPos++;
        }
        if (charPos == source.length()) {
            JsonLexerToken invalidToken = new JsonLexerToken(JsonLexerTokenType.STRING, source, start, charPos);
            throw new JsonLexerException(invalidToken, STRING_IS_MISSING_CLOSING_QUOTE);
        }
        charPos++;
        return new JsonLexerToken(JsonLexerTokenType.STRING, source, start, charPos);
    }

    private JsonLexerToken parseToken() {
        if (charPos == source.length()) {
            return new JsonLexerToken(JsonLexerTokenType.EOF, source, charPos, charPos);
        }
        char c = source.charAt(charPos);
        if (isDigit(c) || c == '-' || c == '+') {
            return parseNumber();
        }
        if (c == '"') {
            return parseString();
        }
        if (isDelimiter(c)) {
            int start = charPos;
            charPos += 1;
            return new JsonLexerToken(JsonLexerTokenType.DELIMITER, source, start, charPos);
        }
        return parseOther();
    }

    public final void skipWhitespace() {
        while (charPos < source.length() && isWhiteSpace(source.charAt(charPos))) {
            charPos++;
        }
    }

}
