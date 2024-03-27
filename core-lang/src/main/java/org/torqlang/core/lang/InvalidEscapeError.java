/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;


public final class InvalidEscapeError extends GeneratorError {

    public static final String INVALID_ESCAPE_ERROR = "Invalid escape error";

    public InvalidEscapeError(Lang lang) {
        super(INVALID_ESCAPE_ERROR, lang);
    }

}
