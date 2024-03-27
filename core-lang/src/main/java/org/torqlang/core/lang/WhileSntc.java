/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.util.SourceSpan;

public final class WhileSntc extends AbstractLang implements Sntc {

    public final SntcOrExpr cond;
    public final SeqLang body;

    public WhileSntc(SntcOrExpr cond, SeqLang body, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.cond = cond;
        this.body = body;
    }

    @Override
    public final <T, R> R accept(LangVisitor<T, R> visitor, T state)
        throws Exception
    {
        return visitor.visitWhileSntc(this, state);
    }

}
