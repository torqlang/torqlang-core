/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;


public final class NotExprError extends GeneratorError {

    public static final String NOT_AN_EXPRESSION = "Not an expression";

    public NotExprError(Lang lang) {
        super(NOT_AN_EXPRESSION, lang);
    }

}
