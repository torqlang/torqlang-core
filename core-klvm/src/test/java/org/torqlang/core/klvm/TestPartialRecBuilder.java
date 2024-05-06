/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestPartialRecBuilder {

    @Test
    public void testCreateComplete() throws Exception {
        Rec r;
        Str testLabel = Str.of("test-label");
        Str zero = Str.of("0-zero");
        Str one = Str.of("1-one");
        Str a = Str.of("a");
        Str b = Str.of("b");

        r = Rec.partialRecBuilder().build();
        assertTrue(CompleteRec.create(List.of()).entails(r, null));

        r = Rec.partialRecBuilder().addField(new CompleteField(zero, a)).build();
        assertTrue(CompleteRec.create(List.of(new CompleteField(zero, a))).entails(r, null));
        r = Rec.partialRecBuilder().addField(zero, a).build();
        assertTrue(CompleteRec.create(List.of(new CompleteField(zero, a))).entails(r, null));

        r = Rec.partialRecBuilder().addField(new CompleteField(zero, a)).addField(new CompleteField(one, b)).build();
        assertTrue(CompleteRec.create(List.of(new CompleteField(zero, a), new CompleteField(one, b))).entails(r, null));
        r = Rec.partialRecBuilder().addField(zero, a).addField(one, b).build();
        assertTrue(CompleteRec.create(List.of(new CompleteField(zero, a), new CompleteField(one, b))).entails(r, null));

        r = Rec.partialRecBuilder().setLabel(testLabel).build();
        assertTrue(CompleteRec.create(testLabel, List.of()).entails(r, null));

        r = Rec.partialRecBuilder().setLabel(testLabel).addField(new CompleteField(zero, a)).build();
        assertTrue(CompleteRec.create(testLabel, List.of(new CompleteField(zero, a))).entails(r, null));
        r = Rec.partialRecBuilder().setLabel(testLabel).addField(zero, a).build();
        assertTrue(CompleteRec.create(testLabel, List.of(new CompleteField(zero, a))).entails(r, null));

        r = Rec.partialRecBuilder().setLabel(testLabel).addField(new CompleteField(zero, a)).addField(new CompleteField(one, b)).build();
        assertTrue(CompleteRec.create(testLabel, List.of(new CompleteField(zero, a), new CompleteField(one, b))).entails(r, null));
        r = Rec.partialRecBuilder().setLabel(testLabel).addField(zero, a).addField(one, b).build();
        assertTrue(CompleteRec.create(testLabel, List.of(new CompleteField(zero, a), new CompleteField(one, b))).entails(r, null));
    }

    @Test
    public void testCreateWithComplete1WithLabel() {
        Rec r;
        Str testLabel = Str.of("test-label");
        CompleteRec cr;

        Str zero = Str.of("0-zero");
        Str a = Str.of("a");

        r = Rec.partialRecBuilder().setLabel(testLabel).addField(zero, a).build();
        assertInstanceOf(CompleteRec.class, r);
        cr = (CompleteRec) r;
        assertEquals(testLabel, cr.label());
        assertEquals(1, cr.fieldCount());
        Field f = cr.fieldAt(0);
        assertEquals(zero, f.feature());
        assertEquals(a, f.value());
    }

    @Test
    public void testCreateWithEmptyWithFutureLabel() {
        Rec r;
        Var labelVar = new Var();
        PartialRec pr;

        r = Rec.partialRecBuilder().setLabel(labelVar).build();
        assertInstanceOf(PartialRec.class, r);
        pr = (PartialRec) r;

        assertEquals(labelVar, pr.futureLabel());
    }

    @Test
    public void testCreateWithEmptyWithLabel() {
        Rec r;
        Str testLabel = Str.of("test-label");
        CompleteRec cr;

        r = Rec.partialRecBuilder().setLabel(testLabel).build();
        assertInstanceOf(CompleteRec.class, r);
        cr = (CompleteRec) r;

        assertEquals(testLabel, cr.label());
    }

    @Test
    public void testCreateWithFuture1WithLabel() throws Exception {
        Rec r;
        Str testLabel = Str.of("test-label");
        Var zeroVar = new Var();
        Str zero = Str.of("0-zero");
        Str a = Str.of("a");
        PartialRec pr;

        FutureField futureField = new FutureField(zeroVar, a);
        assertEquals(zeroVar, futureField.feature());
        assertEquals(a, futureField.value());

        r = Rec.partialRecBuilder()
            .setLabel(testLabel)
            .addField(futureField)
            .build();
        assertInstanceOf(PartialRec.class, r);
        pr = (PartialRec) r;
        assertEquals(testLabel, pr.label());
        assertEquals(1, pr.futureFieldCount());
        assertEquals(0, pr.fieldCount());
        assertEquals(1, pr.totalFieldCount());
        {
            Exception exc = assertThrows(IndexOutOfBoundsException.class, () -> r.fieldAt(0));
            assertEquals("Index 0 out of bounds for length 0", exc.getMessage());
        }
        zeroVar.bindToValue(zero, null);
        // Select will force a checkDetermined()
        assertEquals(a, pr.select(zero));
        Field f = pr.fieldAt(0);
        assertEquals(zero, f.feature());
        assertEquals(a, f.value());
    }

    @Test
    public void testCreateWithMixed4WithLabel() throws Exception {
        Rec r;
        Str testLabel = Str.of("test-label");

        Var zeroVar = new Var();
        Str zero = Str.of("0-zero");
        Str a = Str.of("a");

        Str one = Str.of("1-one");
        Str b = Str.of("b");

        Str two = Str.of("2-two");
        Var cVar = new Var();
        Str c = Str.of("c");

        FeatureOrVar threeValueOrVar = Str.of("3-three");
        Str three = Str.of("3-three");
        Str d = Str.of("d");

        PartialRec pr;
        Field f;

        r = Rec.partialRecBuilder()
            .setLabel(testLabel)
            .addField(zeroVar, a)
            .addField(one, b)
            .addField(two, cVar)
            .addField(threeValueOrVar, d)
            .build();
        assertInstanceOf(PartialRec.class, r);
        pr = (PartialRec) r;
        assertEquals(testLabel, pr.label());
        assertEquals(1, pr.futureFieldCount());
        assertEquals(3, pr.fieldCount());
        assertEquals(4, pr.totalFieldCount());
        // Because the record is not determined, field arity is incorrect but incidentally
        // in the order added. Note that we cannot do a select() without causing a WaitVarException.
        f = pr.fieldAt(0);
        assertEquals(one, f.feature());
        assertEquals(b, f.value());
        f = pr.fieldAt(1);
        assertEquals(two, f.feature());
        assertEquals(cVar, f.value());
        {
            Exception exc = assertThrows(IndexOutOfBoundsException.class, () -> r.fieldAt(3));
            assertEquals("Index 3 out of bounds for length 3", exc.getMessage());
        }
        // Bind the zero feature
        zeroVar.bindToValue(zero, null);
        // Select will force a checkDetermined(), which will cause the record to become determined, which
        // in turn, will change record arity.
        assertEquals(a, pr.select(zero));
        f = pr.fieldAt(0);
        assertEquals(zero, f.feature());
        assertEquals(a, f.value());
        f = pr.fieldAt(1);
        assertEquals(one, f.feature());
        assertEquals(b, f.value());
        f = pr.fieldAt(2);
        assertEquals(two, f.feature());
        assertEquals(cVar, f.value());
        assertEquals(cVar, f.value().resolveValueOrVar());
        f = pr.fieldAt(3);
        assertEquals(three, f.feature());
        assertEquals(d, f.value());
        // Bind the third value
        cVar.bindToValue(c, null);
        f = pr.fieldAt(2);
        assertEquals(two, f.feature());
        assertEquals(c, f.value().resolveValueOrVar());
    }

}
