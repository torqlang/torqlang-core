/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.util;

public final class AdjoinedSourceSpan implements SourceSpan {

    public final SourceSpan beginSpan;
    public final SourceSpan endSpan;
    public int hash = -1;

    public AdjoinedSourceSpan(SourceSpan span1, SourceSpan span2) {
        if (span1.begin() < span2.begin()) {
            beginSpan = span1;
        } else {
            beginSpan = span2;
        }
        if (span1.end() > span2.end()) {
            endSpan = span1;
        } else {
            endSpan = span2;
        }
    }

    @Override
    public final int begin() {
        return beginSpan.begin();
    }

    @Override
    public final int end() {
        return endSpan.end();
    }

    @Override
    public final String source() {
        return beginSpan.source();
    }

    @Override
    public final SourceSpan toSourceSpanBegin() {
        return beginSpan.toSourceSpanBegin();
    }

    @Override
    public final SourceSpan toSourceSpanEnd() {
        return endSpan.toSourceSpanEnd();
    }

}
