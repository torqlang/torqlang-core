/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public final class IdentNotFoundError extends MachineError {

    public static final String IDENT_NOT_FOUND = "Ident not found";

    public final Ident ident;
    public final Stmt stmt;

    public IdentNotFoundError(Ident ident, Stmt stmt) {
        super(IDENT_NOT_FOUND + ": " + ident);
        this.ident = ident;
        this.stmt = stmt;
    }
}
