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

public final class TryLang extends AbstractLang implements SntcOrExpr {

    public final SeqLang body;
    public final List<CatchClause> catchClauses;
    public final SeqLang finallySeq;

    public TryLang(SeqLang body, List<CatchClause> catchClauses, SeqLang finallySeq, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.body = body;
        this.catchClauses = nullSafeCopyOf(catchClauses);
        this.finallySeq = finallySeq;
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitTryLang(this, state);
    }

}
