/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;


public final class ContinueNotAllowedError extends GeneratorError {

    public static final String CONTINUE_NOT_ALLOWED = "Continue not allowed";

    public ContinueNotAllowedError(Lang lang) {
        super(CONTINUE_NOT_ALLOWED, lang);
    }

}
