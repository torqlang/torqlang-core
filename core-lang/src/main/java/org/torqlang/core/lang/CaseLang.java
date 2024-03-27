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

public final class CaseLang extends AbstractLang implements SntcOrExpr {

    public final SntcOrExpr arg;
    public final CaseClause caseClause;
    public final List<CaseClause> altCaseClauses;
    public final SeqLang elseSeq;

    public CaseLang(SntcOrExpr arg, CaseClause caseClause, List<CaseClause> altCaseClauses,
                    SeqLang elseSeq, SourceSpan sourceSpan)
    {
        super(sourceSpan);
        this.arg = arg;
        this.caseClause = caseClause;
        this.altCaseClauses = nullSafeCopyOf(altCaseClauses);
        this.elseSeq = elseSeq;
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitCaseLang(this, state);
    }

}
