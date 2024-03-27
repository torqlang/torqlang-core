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

public interface RecPtn extends Ptn {

    @Override
    default <T, R> R accept(KernelVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitRecPtn(this, state);
    }

    @Override
    default void captureLexicallyFree(Set<Ident> knownBound, Set<Ident> lexicallyFree) {
        ValueOrIdentPtn.captureLexicallyFree(label(), knownBound, lexicallyFree);
        for (FieldPtn fieldPtn : fields()) {
            fieldPtn.captureLexicallyFree(knownBound, lexicallyFree);
        }
    }

    /**
     * This method is a polymorphic callback requesting "case Value of RecPtn then..."
     */
    @Override
    default ValueOrResolvedPtn caseNonRecOfThis(Value nonRecValue, Env env) {
        return null;
    }

    default int fieldCount() {
        return fields().size();
    }

    List<FieldPtn> fields();

    LiteralOrIdentPtn label();

    boolean partialArity();

    SourceSpan sourceSpan();
}
