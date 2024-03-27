/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.SourceSpan;

import java.util.HashSet;
import java.util.Set;

public interface Stmt extends DeclOrStmt, SourceSpan {

    /*
     * This is a convenience method. Capture the lexically free identifiers from a collection of statements.
     * Free identifiers are captured from each peer statement by resetting the knownBound set to the original
     * set passed to this method.
     *
     * stmts          collection of statements from which we are collecting free identifiers
     * knownBound     identifiers known so far to be bound in the closure
     * lexicallyFree  free identifiers captured so far in the closure
     */
    static void captureLexicallyFree(StmtList stmts, Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        for (StmtList.Entry current = stmts.firstEntry(); current != null; current = current.next()) {
            // Reset knownBound for each peer statement
            current.stmt().captureLexicallyFree(new HashSet<>(knownBound), lexicallyFree);
        }
    }

    void compute(Env env, Machine machine) throws WaitException;

    void pushStackEntries(Machine machine, Env env);

}
