/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.util.SourceSpan;

public abstract class AbstractLang implements Lang {

    private final SourceSpan sourceSpan;

    private TypeOrTypeVar infrType;

    public AbstractLang(SourceSpan sourceSpan) {
        // infrType begins as a Java null
        this.sourceSpan = sourceSpan;
    }

    @Override
    public final int begin() {
        return sourceSpan.begin();
    }

    @Override
    public final int end() {
        return sourceSpan.end();
    }

    @Override
    public final TypeOrTypeVar infrType() {
        return infrType;
    }

    @Override
    public final void setInfrType(TypeOrTypeVar infrType) {
        this.infrType = infrType;
    }

    @Override
    public final String source() {
        return sourceSpan.source();
    }

    @Override
    public final SourceSpan toSourceSpanBegin() {
        return sourceSpan.toSourceSpanBegin();
    }

    @Override
    public final SourceSpan toSourceSpanEnd() {
        return sourceSpan.toSourceSpanEnd();
    }

    @Override
    public final String toString() {
        return LangFormatter.SINGLETON.format(this);
    }

}
