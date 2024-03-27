/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.util.SourceSpan;

public abstract class MatchClause extends AbstractLang {

    public final Pat pat;
    public final SntcOrExpr guard;
    public final SeqLang body;

    public MatchClause(Pat pat, SntcOrExpr guard, SeqLang body, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.pat = pat;
        this.guard = guard;
        this.body = body;
    }

}
