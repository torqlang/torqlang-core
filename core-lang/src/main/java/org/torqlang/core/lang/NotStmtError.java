/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;


public final class NotStmtError extends GeneratorError {

    public static final String NOT_AN_STATEMENT = "Not a statement";

    public NotStmtError(Lang lang) {
        super(NOT_AN_STATEMENT, lang);
    }

}
