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

public final class ActorSntc extends ActorLang implements NameDecl, Sntc {

    public final Ident name;

    public ActorSntc(Ident name, List<Pat> formalArgs, List<SntcOrExpr> body, SourceSpan sourceSpan) {
        super(formalArgs, body, sourceSpan);
        this.name = name;
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitActorSntc(this, state);
    }

    @Override
    public final Ident name() {
        return name;
    }

}
