/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import java.util.NoSuchElementException;

public final class LexerIter {

    private final Lexer lexer;
    private final boolean skipComments;

    private LexerToken current;
    private LexerToken next;

    public LexerIter(String source) {
        this(source, true);
    }

    public LexerIter(String source, boolean skipComments) {
        this.lexer = new Lexer(source);
        this.skipComments = skipComments;
        this.next = lexer.nextToken(this.skipComments);
    }

    public final LexerToken current() {
        return current;
    }

    public final boolean hasNext() {
        return next != null;
    }

    public final LexerToken next() {
        if (next == null) {
            throw new NoSuchElementException();
        }
        current = next;
        if (current.isEof()) {
            next = null;
        } else {
            next = lexer.nextToken(skipComments);
        }
        return current;
    }

    public final String source() {
        return lexer.source();
    }

}
