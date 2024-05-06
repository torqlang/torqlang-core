/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCompleteRecBuilder {

    @Test
    public void testCreate() throws Exception {
        CompleteRec r;
        Str testLabel = Str.of("test-label");
        Str zero = Str.of("0-zero");
        Str one = Str.of("1-one");
        Str a = Str.of("a");
        Str b = Str.of("b");

        r = Rec.completeRecBuilder().build();
        assertTrue(CompleteRec.create(List.of()).entails(r, null));

        r = Rec.completeRecBuilder().addField(new CompleteField(zero, a)).build();
        assertTrue(CompleteRec.create(List.of(new CompleteField(zero, a))).entails(r, null));
        r = Rec.completeRecBuilder().addField(zero, a).build();
        assertTrue(CompleteRec.create(List.of(new CompleteField(zero, a))).entails(r, null));

        r = Rec.completeRecBuilder().addField(new CompleteField(zero, a)).addField(new CompleteField(one, b)).build();
        assertTrue(CompleteRec.create(List.of(new CompleteField(zero, a), new CompleteField(one, b))).entails(r, null));
        r = Rec.completeRecBuilder().addField(zero, a).addField(one, b).build();
        assertTrue(CompleteRec.create(List.of(new CompleteField(zero, a), new CompleteField(one, b))).entails(r, null));

        r = Rec.completeRecBuilder().setLabel(testLabel).build();
        assertTrue(CompleteRec.create(testLabel, List.of()).entails(r, null));

        r = Rec.completeRecBuilder().setLabel(testLabel).addField(new CompleteField(zero, a)).build();
        assertTrue(CompleteRec.create(testLabel, List.of(new CompleteField(zero, a))).entails(r, null));
        r = Rec.completeRecBuilder().setLabel(testLabel).addField(zero, a).build();
        assertTrue(CompleteRec.create(testLabel, List.of(new CompleteField(zero, a))).entails(r, null));

        r = Rec.completeRecBuilder().setLabel(testLabel).addField(new CompleteField(zero, a)).addField(new CompleteField(one, b)).build();
        assertTrue(CompleteRec.create(testLabel, List.of(new CompleteField(zero, a), new CompleteField(one, b))).entails(r, null));
        r = Rec.completeRecBuilder().setLabel(testLabel).addField(zero, a).addField(one, b).build();
        assertTrue(CompleteRec.create(testLabel, List.of(new CompleteField(zero, a), new CompleteField(one, b))).entails(r, null));
    }

    @Test
    public void testCreateTuple() throws Exception {
        CompleteRec r;
        Str testLabel = Str.of("test-label");
        Str a = Str.of("a");
        Str b = Str.of("b");

        r = Rec.completeRecBuilder().build();
        assertTrue(CompleteRec.create(List.of()).entails(r, null));
        assertInstanceOf(CompleteTuple.class, r);

        r = Rec.completeRecBuilder().addField(Int32.I32_0, a).build();
        assertTrue(CompleteRec.create(List.of(new CompleteField(Int32.I32_0, a))).entails(r, null));
        assertInstanceOf(CompleteTuple.class, r);

        r = Rec.completeRecBuilder().setLabel(testLabel).addField(Int32.I32_0, a).build();
        assertTrue(CompleteRec.create(testLabel, List.of(new CompleteField(Int32.I32_0, a))).entails(r, null));
        assertInstanceOf(CompleteTuple.class, r);

        r = Rec.completeRecBuilder().addField(Int32.I32_0, a).addField(Int32.I32_1, b).build();
        assertTrue(CompleteRec.create(List.of(new CompleteField(Int32.I32_0, a), new CompleteField(Int32.I32_1, b))).entails(r, null));
        assertInstanceOf(CompleteTuple.class, r);

        r = Rec.completeRecBuilder().setLabel(testLabel).addField(Int32.I32_0, a).addField(Int32.I32_1, b).build();
        assertTrue(CompleteRec.create(testLabel, List.of(new CompleteField(Int32.I32_0, a), new CompleteField(Int32.I32_1, b))).entails(r, null));
        assertInstanceOf(CompleteTuple.class, r);
    }

    @Test
    public void testCreateWithFields() throws Exception {
        CompleteRec r;
        Str testLabel = Str.of("test-label");
        Str zero = Str.of("0-zero");
        Str one = Str.of("1-one");
        Str a = Str.of("a");
        Str b = Str.of("b");

        r = Rec.completeRecBuilder(null, List.of(new CompleteField(zero, a))).build();
        assertTrue(CompleteRec.create(List.of(new CompleteField(zero, a))).entails(r, null));

        r = Rec.completeRecBuilder(testLabel, List.of(new CompleteField(zero, a))).build();
        assertTrue(CompleteRec.create(testLabel, List.of(new CompleteField(zero, a))).entails(r, null));

        r = Rec.completeRecBuilder(null, List.of(new CompleteField(zero, a), new CompleteField(one, b))).build();
        assertTrue(CompleteRec.create(List.of(new CompleteField(zero, a), new CompleteField(one, b))).entails(r, null));

        r = Rec.completeRecBuilder(testLabel, List.of(new CompleteField(zero, a), new CompleteField(one, b))).build();
        assertTrue(CompleteRec.create(testLabel, List.of(new CompleteField(zero, a), new CompleteField(one, b))).entails(r, null));
    }

}
