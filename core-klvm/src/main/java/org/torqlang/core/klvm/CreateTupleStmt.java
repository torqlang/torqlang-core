/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.SourceSpan;

import java.util.List;
import java.util.Set;

public final class CreateTupleStmt extends AbstractStmt implements CreateStmt {

    public final Ident x;
    public final TupleDef tupleDef;

    public CreateTupleStmt(Ident x, TupleDef tupleDef, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.x = x;
        this.tupleDef = tupleDef;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitCreateTupleStmt(this, state);
    }

    @Override
    public final void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        Ident.captureLexicallyFree(x, knownBound, lexicallyFree);
        tupleDef.captureLexicallyFree(knownBound, lexicallyFree);
    }

    @Override
    public final void compute(Env env, Machine machine) throws WaitException {
        LiteralOrVar labelRes = (LiteralOrVar) tupleDef.label.resolveValueOrVar(env);
        boolean isComplete = labelRes instanceof Literal;
        List<ValueDef> valueDefs = tupleDef.valueDefs;
        int size = valueDefs.size();
        ValueOrVar[] partialValues = new ValueOrVar[size];
        Complete[] completeValues = new Complete[size];
        for (int i = 0; i < size; i++) {
            ValueDef td = valueDefs.get(i);
            ValueOrVar valueOrVar = td.value.resolveValueOrVar(env);
            if (valueOrVar instanceof Complete complete) {
                completeValues[i] = complete;
            } else {
                isComplete = false;
            }
            partialValues[i] = valueOrVar;
        }
        Tuple tuple;
        if (isComplete) {
            tuple = BasicCompleteTuple.createPrivatelyForKlvm((Literal) labelRes, completeValues);
        } else {
            tuple = BasicPartialTuple.createPrivatelyForKlvm(labelRes, partialValues);
        }
        ValueOrVar xRes = x.resolveValueOrVar(env);
        xRes.bindToValue(tuple, null);
    }

}
