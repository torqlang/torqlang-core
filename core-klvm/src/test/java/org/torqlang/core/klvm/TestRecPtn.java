/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.junit.Test;
import org.torqlang.core.util.SourceSpan;

import java.util.List;

import static org.junit.Assert.*;

public class TestRecPtn {

    /*
     * MATCHES
     * case 'test-label'#{}
     * of ~identLabel#{}
     * where identLabel is bound to 'test-label'
     *
     * DOES NOT MATCH
     * case 'test-label'#{}
     * of ~identLabel#{}
     * where identLabel is bound to 'test-label-mismatch'
     */
    @Test
    public void testEmptyWithIdentLabel() throws Exception {

        Rec r;
        ValueOrResolvedPtn match;

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        IdentPtn labelIdent = new IdentPtn(Ident.create("labelIdent"), true);
        Str testLabel = Str.of("test-label");
        Str testLabelMismatch = Str.of("test-label-mismatch");

        RecPtn rp = new BasicRecPtn(labelIdent, List.of(), false, emptySpan);
        assertEquals(labelIdent, rp.label());
        assertEquals(0, rp.fieldCount());
        assertEquals(List.of(), rp.fields());
        assertEquals(emptySpan, rp.sourceSpan());
        assertEquals("~labelIdent#{}", rp.toString());

        // Matches

        r = CompleteRec.create(testLabel, List.of());
        Env envWithMatch = Env.create(List.of(new EnvEntry(Ident.create("labelIdent"), new Var(testLabel))));
        match = rp.caseRecOfThis(r, envWithMatch);
        assertTrue(match instanceof ResolvedRecPtn);
        ResolvedRecPtn rrp = (ResolvedRecPtn) match;
        assertEquals("'test-label'#{}", rrp.toString());

        // Does not match

        r = CompleteRec.create(testLabel, List.of());
        Env envWithMismatch = Env.create(List.of(new EnvEntry(Ident.create("labelIdent"), new Var(testLabelMismatch))));
        match = rp.caseRecOfThis(r, envWithMismatch);
        assertNull(match);
    }

    /*
     * MATCHES
     * case 'test-label'#{}
     * of 'test-label'#{}
     *
     * DOES NOT MATCH
     * case 'test-label'#{}
     * of 'test-label-mismatch'#{}
     */
    @Test
    public void testEmptyWithLabel() throws Exception {

        Rec r;
        ValueOrResolvedPtn match;

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Str testLabel = Str.of("test-label");
        Str testLabelMismatch = Str.of("test-label-mismatch");

        RecPtn rp = new BasicRecPtn(testLabel, List.of(), false, emptySpan);
        assertEquals(testLabel, rp.label());
        assertEquals(0, rp.fieldCount());
        assertEquals(List.of(), rp.fields());
        assertEquals(emptySpan, rp.sourceSpan());
        assertEquals("'test-label'#{}", rp.toString());

        // Matches

        r = CompleteRec.create(testLabel, List.of());
        match = rp.caseRecOfThis(r, Env.emptyEnv());
        assertTrue(match instanceof ResolvedRecPtn);
        ResolvedRecPtn rrp = (ResolvedRecPtn) match;
        assertEquals("'test-label'#{}", rrp.toString());

        // Does not match

        r = CompleteRec.create(testLabelMismatch, List.of());
        match = rp.caseRecOfThis(r, Env.emptyEnv());
        assertNull(match);
    }

    /*
     * MATCHES
     * case 'test-label'#{0: 'a'}
     * of 'test-label'#{0: 'a'}
     *
     * DOES NOT MATCH
     * case 'test-label'#{0: 'a'}
     * of 'test-label'#{0: 'b'}
     */
    @Test
    public void testFullArity1WithLabel() throws Exception {

        Rec r;
        FieldPtn fp;
        RecPtn rp;
        ResolvedRecPtn rrp;
        ValueOrResolvedPtn match;

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Str testLabel = Str.of("test-label");
        Str a = Str.of("a");
        Str b = Str.of("b");

        // Matches

        fp = new FieldPtn(Int32.I32_0, a, emptySpan);
        rp = new BasicRecPtn(testLabel, List.of(fp), false, emptySpan);
        assertEquals(testLabel, rp.label());
        assertEquals(1, rp.fieldCount());
        assertEquals(List.of(fp), rp.fields());
        assertEquals(emptySpan, rp.sourceSpan());
        assertEquals("'test-label'#{0: 'a'}", rp.toString());

        r = Rec.completeRecBuilder().setLabel(testLabel).addField(Int32.I32_0, a).build();
        match = rp.caseRecOfThis(r, Env.emptyEnv());
        assertTrue(match instanceof ResolvedRecPtn);
        rrp = (ResolvedRecPtn) match;
        assertEquals("'test-label'#{0: 'a'}", rrp.toString());

        // Does not match

        fp = new FieldPtn(Int32.I32_0, a, emptySpan);
        rp = new BasicRecPtn(testLabel, List.of(fp), false, emptySpan);
        assertEquals(testLabel, rp.label());
        assertEquals(1, rp.fieldCount());
        assertEquals(List.of(fp), rp.fields());
        assertEquals(emptySpan, rp.sourceSpan());
        assertEquals("'test-label'#{0: 'a'}", rp.toString());

        r = Rec.completeRecBuilder().setLabel(testLabel).addField(Int32.I32_0, b).build();
        match = rp.caseRecOfThis(r, Env.emptyEnv());
        assertNull(match);
    }

    /*
     * DOES NOT MATCH
     * case 'test-label'#{0: 'a', 1: 'b'}
     * of 'test-label'#{0: 'a'}
     */
    @Test
    public void testGreaterPatternArity() throws Exception {

        Rec r;
        ValueOrResolvedPtn match;

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Str testLabel = Str.of("test-label");
        Str a = Str.of("a");
        Str b = Str.of("b");

        FieldPtn fp1 = new FieldPtn(Int32.I32_0, a, emptySpan);
        FieldPtn fp2 = new FieldPtn(Int32.I32_1, b, emptySpan);
        RecPtn rp = new BasicRecPtn(testLabel, List.of(fp1, fp2), false, emptySpan);
        assertEquals(testLabel, rp.label());
        assertEquals(2, rp.fieldCount());
        assertEquals(List.of(fp1, fp2), rp.fields());
        assertEquals("'test-label'#{0: 'a', 1: 'b'}", rp.toString());

        r = Rec.completeRecBuilder()
            .setLabel(testLabel)
            .addField(Int32.I32_0, a)
            .build();
        match = rp.caseRecOfThis(r, Env.emptyEnv());
        assertNull(match);
    }

    /*
     * DOES NOT MATCH
     * case 'test-label'#{0: 'a'}
     * of 'test-label'#{0: 'a', 1: 'b', ...}
     */
    @Test
    public void testGreaterPatternArityWithPartialArity() throws Exception {

        Rec r;
        ValueOrResolvedPtn match;

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Str testLabel = Str.of("test-label");
        Str a = Str.of("a");
        Str b = Str.of("b");

        FieldPtn fp1 = new FieldPtn(Int32.I32_0, a, emptySpan);
        FieldPtn fp2 = new FieldPtn(Int32.I32_1, b, emptySpan);
        RecPtn rp = new BasicRecPtn(testLabel, List.of(fp1, fp2), true, emptySpan);
        assertEquals(testLabel, rp.label());
        assertEquals(2, rp.fieldCount());
        assertEquals(List.of(fp1, fp2), rp.fields());
        assertEquals("'test-label'#{0: 'a', 1: 'b', ...}", rp.toString());

        r = Rec.completeRecBuilder()
            .setLabel(testLabel)
            .addField(Int32.I32_0, a)
            .build();
        match = rp.caseRecOfThis(r, Env.emptyEnv());
        assertNull(match);
    }

    /*
     * DOES NOT MATCH
     * case 'test-label'#{0: 'a', 1: 'b'}
     * of 'test-label'#{0: 'a'}
     */
    @Test
    public void testGreaterRecordArity() throws Exception {

        Rec r;
        ValueOrResolvedPtn match;

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Str testLabel = Str.of("test-label");
        Str a = Str.of("a");
        Str b = Str.of("b");

        FieldPtn fp = new FieldPtn(Int32.I32_0, a, emptySpan);
        RecPtn rp = new BasicRecPtn(testLabel, List.of(fp), false, emptySpan);
        assertEquals(testLabel, rp.label());
        assertEquals(1, rp.fieldCount());
        assertEquals(List.of(fp), rp.fields());
        assertEquals("'test-label'#{0: 'a'}", rp.toString());

        r = Rec.completeRecBuilder()
            .setLabel(testLabel)
            .addField(Int32.I32_0, a)
            .addField(Int32.I32_1, b)
            .build();
        match = rp.caseRecOfThis(r, Env.emptyEnv());
        assertNull(match);
    }

    /*
     * DOES NOT MATCH
     * case 'test-label'#{0: 'a'}
     * of 'test-label'#{'0-zero': 'a'}
     */
    @Test
    public void testNoMatchFeatures() throws Exception {

        Rec r;
        ValueOrResolvedPtn match;

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Str testLabel = Str.of("test-label");
        Str zero = Str.of("0-zero");
        Str a = Str.of("a");

        FieldPtn fp1 = new FieldPtn(zero, a, emptySpan);
        RecPtn rp = new BasicRecPtn(testLabel, List.of(fp1), false, emptySpan);
        assertEquals(testLabel, rp.label());
        assertEquals(1, rp.fieldCount());
        assertEquals(List.of(fp1), rp.fields());
        assertEquals("'test-label'#{'0-zero': 'a'}", rp.toString());

        r = Rec.completeRecBuilder()
            .setLabel(testLabel)
            .addField(Int32.I32_0, a)
            .build();
        match = rp.caseRecOfThis(r, Env.emptyEnv());
        assertNull(match);
    }

    /*
     * DOES NOT MATCH
     * case 'test-label'#{0: 'a'}
     * of 'test-label'#{'0-zero': 'a', ...}
     */
    @Test
    public void testNoMatchFeaturesWithPartialArity() throws Exception {

        Rec r;
        ValueOrResolvedPtn match;

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Str testLabel = Str.of("test-label");
        Str zero = Str.of("0-zero");
        Str a = Str.of("a");

        FieldPtn fp1 = new FieldPtn(zero, a, emptySpan);
        RecPtn rp = new BasicRecPtn(testLabel, List.of(fp1), true, emptySpan);
        assertEquals(testLabel, rp.label());
        assertEquals(1, rp.fieldCount());
        assertEquals(List.of(fp1), rp.fields());
        assertEquals("'test-label'#{'0-zero': 'a', ...}", rp.toString());

        r = Rec.completeRecBuilder()
            .setLabel(testLabel)
            .addField(Int32.I32_0, a)
            .build();
        match = rp.caseRecOfThis(r, Env.emptyEnv());
        assertNull(match);
    }

    /*
     * MATCHES
     * case 'test-label'#{'0-zero': 'a', '1-one': 'b'}
     * of 'test-label'#{'0-zero': 'a', ...}
     *
     * MATCHES
     * case 'test-label'#{'0-zero': 'a', '1-one': 'b'}
     * of 'test-label'#{'1-zero': 'b', ...}
     */
    @Test
    public void testPartialArity1WithLabel() throws Exception {

        Rec r;
        ValueOrResolvedPtn match;

        FieldPtn fp;
        RecPtn rp;
        ResolvedRecPtn rrp;

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Str testLabel = Str.of("test-label");
        Str zero = Str.of("0-zero");
        Str one = Str.of("1-one");
        Str a = Str.of("a");
        Str b = Str.of("b");

        // Match partial on 'a'

        fp = new FieldPtn(zero, a, emptySpan);
        rp = new BasicRecPtn(testLabel, List.of(fp), true, emptySpan);
        assertEquals(testLabel, rp.label());
        assertEquals(1, rp.fieldCount());
        assertEquals(List.of(fp), rp.fields());
        assertEquals("'test-label'#{'0-zero': 'a', ...}", rp.toString());

        // 'test-label'#{'0-zero': 'a', '1-one': 'b'}
        r = Rec.completeRecBuilder()
            .setLabel(testLabel)
            .addField(zero, a)
            .addField(one, b)
            .build();
        match = rp.caseRecOfThis(r, Env.emptyEnv());
        assertTrue(match instanceof ResolvedRecPtn);
        rrp = (ResolvedRecPtn) match;
        assertEquals("'test-label'#{'0-zero': 'a', ...}", rrp.toString());

        // Match partial on 'b'

        fp = new FieldPtn(one, b, emptySpan);
        rp = new BasicRecPtn(testLabel, List.of(fp), true, emptySpan);
        assertEquals(testLabel, rp.label());
        assertEquals(1, rp.fieldCount());
        assertEquals(List.of(fp), rp.fields());
        assertEquals("'test-label'#{'1-one': 'b', ...}", rp.toString());

        // 'test-label'#{'0-zero': 'a', '1-one': 'b'}
        r = Rec.completeRecBuilder()
            .setLabel(testLabel)
            .addField(zero, a)
            .addField(one, b)
            .build();
        match = rp.caseRecOfThis(r, Env.emptyEnv());
        assertTrue(match instanceof ResolvedRecPtn);
        rrp = (ResolvedRecPtn) match;
        assertEquals("'test-label'#{'1-one': 'b', ...}", rrp.toString());
    }

}
