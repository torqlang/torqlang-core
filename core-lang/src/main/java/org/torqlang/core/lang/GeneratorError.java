/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.util.ErrorWithSourceSpan;
import org.torqlang.core.util.SourceSpan;

public abstract class GeneratorError extends ErrorWithSourceSpan {

    private final Lang lang;

    public GeneratorError(String message, Lang lang) {
        super(message);
        this.lang = lang;
    }

    public final Lang lang() {
        return lang;
    }

    public final SourceSpan sourceSpan() {
        return lang;
    }

}
