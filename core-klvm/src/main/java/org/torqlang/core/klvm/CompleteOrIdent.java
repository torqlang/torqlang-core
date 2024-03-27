/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.Set;

public interface CompleteOrIdent extends ValueOrIdent {

    /*
     * This is a convenience method. If the given argument is actually an identifier, call
     * `Ident.captureLexicallyFree(Ident, Set, Set)`
     *
     * completeOrIdent    the value or identifier being evaluated as bound or free
     * knownBound         the identifiers known so far to be bound in the closure
     * lexicallyFree      the free identifiers captured so far in the closure
     */
    static void captureLexicallyFree(CompleteOrIdent completeOrIdent, Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        if (completeOrIdent instanceof Ident ident) {
            Ident.captureLexicallyFree(ident, knownBound, lexicallyFree);
        }
    }

    Var toVar(Env env);
}
