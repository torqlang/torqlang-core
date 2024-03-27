/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.util.SourceSpan;

public abstract class SelectExpr extends AbstractLang implements BuiltInApplyExpr {

    public final SntcOrExpr recExpr;
    public final SntcOrExpr featureExpr;

    public SelectExpr(SntcOrExpr recExpr, SntcOrExpr featureExpr, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.recExpr = recExpr;
        this.featureExpr = featureExpr;
    }

}
