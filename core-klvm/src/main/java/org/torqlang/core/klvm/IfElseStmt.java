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

public final class IfElseStmt extends AbstractStmt {

    public final CompleteOrIdent x;
    public final Stmt consequent;
    public final Stmt alternate;

    public IfElseStmt(CompleteOrIdent x, Stmt consequent, Stmt alternate, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.x = x;
        this.consequent = consequent;
        this.alternate = alternate;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitIfElseStmt(this, state);
    }

    @Override
    public final void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        CompleteOrIdent.captureLexicallyFree(x, knownBound, lexicallyFree);
        // Copy knownBound to hide out-of-scope identifiers from the alternate statement
        consequent.captureLexicallyFree(new HashSet<>(knownBound), lexicallyFree);
        // No need to copy knownBound since there are no more peer statements
        alternate.captureLexicallyFree(knownBound, lexicallyFree);
    }

    @Override
    public final void compute(Env env, Machine machine) throws WaitException {
        // Conditional (the if statement) [CTM p. 66]
        // If the activation condition is true, E(<x>) is determined, then
        // -- if E(<x>) is not a boolean (true or false) then throw an error
        // -- if E(<x>) is true, then push (<s>1, E) on the stack
        // -- if E(<x>) is true, then push (<s>2, E) on the stack
        Value xRes = x.resolveValue(env);
        if (!(xRes instanceof Bool bool)) {
            throw new NotBoolError(xRes, this);
        }
        if (bool.value) {
            machine.pushStackEntry(consequent, env);
        } else {
            machine.pushStackEntry(alternate, env);
        }
    }

}
