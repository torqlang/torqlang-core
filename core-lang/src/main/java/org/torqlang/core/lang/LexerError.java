/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.util.ErrorWithSourceSpan;
import org.torqlang.core.util.SourceSpan;

public class LexerError extends ErrorWithSourceSpan {

    public final LexerToken token;
    public final SourceSpan sourceSpan;

    public LexerError(LexerToken token, String message) {
        super(message);
        this.token = token;
        this.sourceSpan = token;
    }

    public final SourceSpan sourceSpan() {
        return sourceSpan;
    }

}
