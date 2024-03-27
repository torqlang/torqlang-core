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

public final class ApplyLang extends AbstractLang implements SntcOrExpr {

    public final SntcOrExpr proc;
    public final List<SntcOrExpr> args;

    public ApplyLang(SntcOrExpr proc, List<? extends SntcOrExpr> args, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.proc = proc;
        this.args = nullSafeCopyOf(args);
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitApplyLang(this, state);
    }

}
