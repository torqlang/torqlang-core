/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCompleteTupleBuilder {

    @Test
    public void test() throws Exception {
        CompleteTuple t;
        Str testLabel = Str.of("test-label");

        t = Rec.completeTupleBuilder().build();
        assertTrue(CompleteTuple.create(List.of()).entails(t, null));

        t = Rec.completeTupleBuilder().addValue(Int32.I32_0).build();
        assertTrue(CompleteTuple.create(List.of(Int32.I32_0)).entails(t, null));

        t = Rec.completeTupleBuilder().addValue(Int32.I32_0).addValue(Int32.I32_1).build();
        assertTrue(CompleteTuple.create(List.of(Int32.I32_0, Int32.I32_1)).entails(t, null));

        t = Rec.completeTupleBuilder().setLabel(testLabel).build();
        assertTrue(CompleteTuple.create(testLabel, List.of()).entails(t, null));

        t = Rec.completeTupleBuilder().setLabel(testLabel).addValue(Int32.I32_0).build();
        assertTrue(CompleteTuple.create(testLabel, List.of(Int32.I32_0)).entails(t, null));

        t = Rec.completeTupleBuilder().setLabel(testLabel).addValue(Int32.I32_0).addValue(Int32.I32_1).build();
        assertTrue(CompleteTuple.create(testLabel, List.of(Int32.I32_0, Int32.I32_1)).entails(t, null));
    }

}
