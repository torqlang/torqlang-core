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

public final class SeqLang extends AbstractLang implements SntcOrExpr {

    public final List<SntcOrExpr> list;

    public SeqLang(List<SntcOrExpr> list, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.list = nullSafeCopyOf(list);
    }

    @Override
    public <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitSeqLang(this, state);
    }

}
