/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {

    private final JsonLexer lexer;
    private JsonLexerToken currentToken;

    public JsonParser(String source) {
        this.lexer = new JsonLexer(source);
    }

    public static Object parse(String source) {
        return new JsonParser(source).parse();
    }

    public static <T> T parseAndCast(String source) {
        return new JsonParser(source).parseAndCast();
    }

    private void nextToken() {
        currentToken = lexer.nextToken();
    }

    public final Object parse() {
        currentToken = lexer.nextToken();
        Object answer = parseAny();
        if (currentToken.type() != JsonLexerTokenType.EOF) {
            throw new IllegalArgumentException("Unexpected token: " + currentToken);
        }
        return answer;
    }

    @SuppressWarnings("unchecked")
    public final <T> T parseAndCast() {
        return (T) parse();
    }

    private Object parseAny() {
        if (currentToken.type() == JsonLexerTokenType.STRING) {
            String answer = unquote(currentToken);
            nextToken(); // accept string
            return answer;
        }
        if (currentToken.type() == JsonLexerTokenType.NUMBER) {
            Number answer;
            String s = currentToken.substring();
            if (s.indexOf('.') > -1) {
                answer = Double.parseDouble(s);
            } else {
                answer = Long.parseLong(s);
            }
            nextToken(); // accept number
            return answer;
        }
        if (currentToken.type() == JsonLexerTokenType.BOOLEAN) {
            Boolean answer = currentToken.firstChar() == 't';
            nextToken(); // accept boolean
            return answer;
        }
        if (currentToken.type() == JsonLexerTokenType.NULL) {
            nextToken(); // accept null
            return JsonNull.SINGLETON;
        }
        Object answer;
        if (currentToken.firstChar() == '{') {
            answer = parseObject();
        } else if (currentToken.firstChar() == '[') {
            answer = parseArray();
        } else {
            throw new IllegalArgumentException("Unexpected delimiter: " + currentToken.firstChar());
        }
        return answer;
    }

    private List<Object> parseArray() {
        List<Object> answer = new ArrayList<>();
        nextToken(); // accept '['
        while (!currentToken.isRightBracketChar()) {
            Object value = parseAny();
            answer.add(value);
            if (currentToken.isCommaChar()) {
                nextToken();
            }
        }
        nextToken(); // accept ']'
        return answer;
    }

    private Map<String, Object> parseObject() {
        Map<String, Object> answer = new HashMap<>();
        nextToken();
        while (!currentToken.isRightBraceChar()) {
            if (currentToken.type() != JsonLexerTokenType.STRING) {
                throw new IllegalArgumentException("String expected - " + currentToken);
            }
            String key = unquote(currentToken);
            nextToken(); // accept key
            if (!currentToken.isColonChar()) {
                throw new IllegalArgumentException(": expected - " + currentToken);
            }
            nextToken(); // accept ':'
            Object value = parseAny();
            answer.put(key, value);
            if (currentToken.isCommaChar()) {
                nextToken();
            }
        }
        nextToken(); // accept '}'
        return answer;
    }

    private String unquote(JsonLexerToken stringToken) {
        String source = stringToken.source();
        int begin = stringToken.begin() + 1;
        int end = stringToken.end() - 1;
        StringBuilder sb = new StringBuilder((end - begin) * 2);
        int i = begin;
        while (i < end) {
            char c1 = source.charAt(i);
            if (c1 == '\\') {
                char c2 = source.charAt(i + 1);
                if (c2 != 'u') {
                    if (c2 == 't') {
                        c1 = '\t';
                    } else if (c2 == 'b') {
                        c1 = '\b';
                    } else if (c2 == 'n') {
                        c1 = '\n';
                    } else if (c2 == 'r') {
                        c1 = '\r';
                    } else if (c2 == 'f') {
                        c1 = '\f';
                    } else if (c2 == '\\') {
                        c1 = '\\';
                    } else if (c2 == '/') {
                        c1 = '/';
                    } else if (c2 == '"') {
                        c1 = '"';
                    } else {
                        throw new IllegalArgumentException("Invalid escape sequence: " + c1 + c2);
                    }
                    sb.append(c1);
                    i += 2;
                } else {
                    int code = Integer.parseInt("" + source.charAt(i + 2) + source.charAt(i + 3) +
                        source.charAt(i + 4) + source.charAt(i + 5), 16);
                    sb.append(Character.toChars(code));
                    i += 6;
                }
            } else {
                sb.append(c1);
                i++;
            }
        }
        return sb.toString();
    }

}
