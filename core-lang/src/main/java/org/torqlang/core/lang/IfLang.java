/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.util.SourceSpan;

import java.util.List;

import static org.torqlang.core.util.ListTools.nullSafeCopyOf;

public final class IfLang extends AbstractLang implements SntcOrExpr {

    public final IfClause ifClause;
    public final List<IfClause> altIfClauses;
    public final SeqLang elseSeq;

    public IfLang(IfClause ifClause, List<IfClause> altIfClauses, SeqLang elseSeq, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.ifClause = ifClause;
        this.altIfClauses = nullSafeCopyOf(altIfClauses);
        this.elseSeq = elseSeq;
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitIfLang(this, state);
    }

}
