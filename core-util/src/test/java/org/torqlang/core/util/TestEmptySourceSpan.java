/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestEmptySourceSpan {

    @Test
    public void test() {
        assertEquals(0, SourceSpan.emptySourceSpan().begin());
        assertEquals(0, SourceSpan.emptySourceSpan().end());
        assertEquals("", SourceSpan.emptySourceSpan().source());
        assertEquals(SourceSpan.emptySourceSpan(), SourceSpan.emptySourceSpan().toSourceSpanBegin());
        assertEquals(SourceSpan.emptySourceSpan(), SourceSpan.emptySourceSpan().toSourceSpanEnd());
    }

}
