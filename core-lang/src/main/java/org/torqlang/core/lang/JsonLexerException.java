/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

public class JsonLexerException extends RuntimeException {

    public final JsonLexerToken token;

    public JsonLexerException(JsonLexerToken token, String message) {
        super(message);
        this.token = token;
    }

}
