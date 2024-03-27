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

public abstract class FuncLang extends AbstractLang implements SntcOrExpr {

    public final List<Pat> formalArgs;
    public final TypeAnno returnType;
    public final SeqLang body;

    public FuncLang(List<Pat> formalArgs, TypeAnno returnType, SeqLang body, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.formalArgs = nullSafeCopyOf(formalArgs);
        this.returnType = returnType;
        this.body = body;
    }

}
