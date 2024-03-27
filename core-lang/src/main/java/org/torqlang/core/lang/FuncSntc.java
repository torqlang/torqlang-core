/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.Ident;
import org.torqlang.core.util.SourceSpan;

import java.util.List;

public final class FuncSntc extends FuncLang implements NameDecl, Sntc {

    private final Ident name;

    public FuncSntc(Ident name, List<Pat> formalArgs, TypeAnno returnType, SeqLang body,
                    SourceSpan sourceSpan)
    {
        super(formalArgs, returnType, body, sourceSpan);
        this.name = name;
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitFuncSntc(this, state);
    }

    @Override
    public final Ident name() {
        return name;
    }

}
