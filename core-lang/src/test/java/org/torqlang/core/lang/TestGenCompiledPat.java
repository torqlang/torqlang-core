/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;
import org.torqlang.core.klvm.*;
import org.torqlang.core.util.SourceSpan;

import static org.junit.Assert.*;

public class TestGenCompiledPat {

    @Test
    public void testFeatureAsPat() {
        Generator generator = new Generator();
        IntAsPat intAsPat = new IntAsPat(Int32.I32_0, SourceSpan.emptySourceSpan());
        CompiledPat compiledPat = new CompiledPat(intAsPat, generator);
        assertEquals(intAsPat, compiledPat.source());
        compiledPat.compile();
        assertTrue(compiledPat.root() instanceof Int32);
        assertEquals(0, compiledPat.children().size());
        Int32 result = (Int32) compiledPat.root();
        assertEquals(Int32.I32_0, result);
    }

    @Test
    public void testIdentAsPat() {
        Generator generator = new Generator();
        IdentAsPat identAsPat = new IdentAsPat(Ident.create("x"), false, SourceSpan.emptySourceSpan());
        CompiledPat compiledPat = new CompiledPat(identAsPat, generator);
        compiledPat.compile();
        assertTrue(compiledPat.root() instanceof IdentPtn);
        assertEquals(0, compiledPat.children().size());
        IdentPtn result = (IdentPtn) compiledPat.root();
        assertEquals(Ident.create("x"), result.ident);
        assertFalse(result.escaped);

        generator = new Generator();
        identAsPat = new IdentAsPat(Ident.create("x"), true, SourceSpan.emptySourceSpan());
        compiledPat = new CompiledPat(identAsPat, generator);
        compiledPat.compile();
        assertTrue(compiledPat.root() instanceof IdentPtn);
        assertEquals(0, compiledPat.children().size());
        result = (IdentPtn) compiledPat.root();
        assertEquals(Ident.create("x"), result.ident);
        assertTrue(result.escaped);
    }

    @Test
    public void testRecPatComplex() {

        // Practical language pattern:
        //     {'0_zero': {'1_one': y}}
        // Compiled kernel pattern:
        //     root = {'0_zero': $V0}
        //     $V0  = {'1_one': y}

        RecPatBuilder builder;
        SourceSpan empty = SourceSpan.emptySourceSpan();
        Generator generator = new Generator();
        builder = RecPatBuilder.builder();
        builder.addFieldPat(new StrAsPat(Str.of("1_one"), empty),
            new IdentAsPat(Ident.create("y"), false, empty));
        RecPat yRecPat = builder.build();
        builder = RecPatBuilder.builder();
        builder.addFieldPat(new StrAsPat(Str.of("0_zero"), empty), yRecPat);
        RecPat xRecPat = builder.build();
        assertEquals("{'0_zero': {'1_one': y}}", xRecPat.toString());

        Ident dollarV0 = Ident.createSystemVarIdent(0);

        CompiledPat compiledPat = new CompiledPat(xRecPat, generator);
        compiledPat.compile();

        assertTrue(compiledPat.root() instanceof RecPtn);
        assertEquals(1, compiledPat.children().size());
        RecPtn result = (RecPtn) compiledPat.root();
        assertEquals(1, result.fieldCount());
        assertEquals(Str.of("0_zero"), result.fields().get(0).feature);
        IdentPtn valuePtn = (IdentPtn) result.fields().get(0).value;
        assertEquals(dollarV0, valuePtn.ident);

        CompiledPat.ChildPtn childPtn = compiledPat.children().get(0);
        assertEquals(dollarV0, childPtn.arg);
        assertEquals(1, childPtn.recPtn.fields().size());
        FieldPtn fieldPtn = childPtn.recPtn.fields().get(0);
        assertEquals(Str.of("1_one"), fieldPtn.feature);
        assertEquals(Ident.create("y"), ((IdentPtn) fieldPtn.value).ident);
    }

    @Test
    public void testRecPatSimple() {

        // {'0_zero': x}

        SourceSpan empty = SourceSpan.emptySourceSpan();
        Generator generator = new Generator();
        RecPatBuilder builder = RecPatBuilder.builder();
        builder.addFieldPat(new StrAsPat(Str.of("0_zero"), empty),
            new IdentAsPat(Ident.create("x"), false, empty));
        RecPat recPat = builder.build();
        assertEquals("{'0_zero': x}", recPat.toString());

        CompiledPat compiledPat = new CompiledPat(recPat, generator);
        compiledPat.compile();

        assertTrue(compiledPat.root() instanceof RecPtn);
        assertEquals(0, compiledPat.children().size());
        RecPtn result = (RecPtn) compiledPat.root();
        assertEquals(1, result.fieldCount());
        assertEquals(Str.of("0_zero"), result.fields().get(0).feature);
        IdentPtn valuePtn = (IdentPtn) result.fields().get(0).value;
        assertEquals(Ident.create("x"), valuePtn.ident);
        assertFalse(valuePtn.escaped);
    }

    @Test
    public void testRecPatSimpleAnonymous() {

        // {'0_zero': _}

        SourceSpan empty = SourceSpan.emptySourceSpan();
        Generator generator = new Generator();
        RecPatBuilder builder = RecPatBuilder.builder();
        builder.addFieldPat(new StrAsPat(Str.of("0_zero"), empty),
            new IdentAsPat(Ident.create("_"), false, empty));
        RecPat recPat = builder.build();
        assertEquals("{'0_zero': _}", recPat.toString());

        Ident dollarA0 = Ident.createSystemAnonymousIdent(0);

        CompiledPat compiledPat = new CompiledPat(recPat, generator);
        compiledPat.compile();

        assertTrue(compiledPat.root() instanceof RecPtn);
        assertEquals(0, compiledPat.children().size());
        RecPtn result = (RecPtn) compiledPat.root();
        assertEquals(1, result.fieldCount());
        assertEquals(Str.of("0_zero"), result.fields().get(0).feature);
        IdentPtn valuePtn = (IdentPtn) result.fields().get(0).value;
        assertEquals(dollarA0, valuePtn.ident);
        assertFalse(valuePtn.escaped);
    }

    @Test
    public void testRecPatSimpleEscapedFeature() {

        // {~x: 0}

        SourceSpan empty = SourceSpan.emptySourceSpan();
        Generator generator = new Generator();
        RecPatBuilder builder = RecPatBuilder.builder();
        builder.addFieldPat(new IdentAsPat(Ident.create("x"), true, empty),
            new IntAsPat(Int32.I32_0, empty));
        RecPat recPat = builder.build();
        assertEquals("{~x: 0}", recPat.toString());

        CompiledPat compiledPat = new CompiledPat(recPat, generator);
        compiledPat.compile();

        assertTrue(compiledPat.root() instanceof RecPtn);
        assertEquals(0, compiledPat.children().size());
        RecPtn result = (RecPtn) compiledPat.root();
        assertEquals(1, result.fieldCount());
        FieldPtn fieldPtn0 = result.fields().get(0);
        assertEquals(Ident.create("x"), ((IdentPtn) fieldPtn0.feature).ident);
        assertTrue(((IdentPtn) fieldPtn0.feature).escaped);
        assertEquals(Int32.I32_0, fieldPtn0.value);

        // {x: 0}

        generator = new Generator();
        builder = RecPatBuilder.builder();
        builder.addFieldPat(new IdentAsPat(Ident.create("x"), false, empty),
            new IntAsPat(Int32.I32_0, empty));
        recPat = builder.build();
        assertEquals("{x: 0}", recPat.toString());

        compiledPat = new CompiledPat(recPat, generator);
        Exception exc = assertThrows(IllegalStateException.class, compiledPat::compile);
        assertEquals("A pattern feature must be a literal or an escaped identifier", exc.getMessage());
    }

    @Test
    public void testRecPatSimpleEscapedValue() {

        // {'0_zero': ~x}

        SourceSpan empty = SourceSpan.emptySourceSpan();
        Generator generator = new Generator();
        RecPatBuilder builder = RecPatBuilder.builder();
        builder.addFieldPat(new StrAsPat(Str.of("0_zero"), empty),
            new IdentAsPat(Ident.create("x"), true, empty));
        RecPat recPat = builder.build();
        assertEquals("{'0_zero': ~x}", recPat.toString());

        CompiledPat compiledPat = new CompiledPat(recPat, generator);
        compiledPat.compile();

        assertTrue(compiledPat.root() instanceof RecPtn);
        assertEquals(0, compiledPat.children().size());
        RecPtn result = (RecPtn) compiledPat.root();
        assertEquals("{'0_zero': ~x}", result.toString());
        assertEquals(1, result.fieldCount());
        FieldPtn fieldPtn0 = result.fields().get(0);
        assertEquals(Str.of("0_zero"), fieldPtn0.feature);
        IdentPtn valuePtn = (IdentPtn) fieldPtn0.value;
        assertEquals(Ident.create("x"), valuePtn.ident);
        assertTrue(valuePtn.escaped);
    }

    @Test
    public void testRecPatSimpleWithLabel() {

        // ~label#{'0_zero': x}

        SourceSpan empty = SourceSpan.emptySourceSpan();
        Generator generator = new Generator();
        RecPatBuilder builder = RecPatBuilder.builder();
        builder.setLabelPat(new IdentAsPat(Ident.create("label"), true, empty));
        builder.addFieldPat(new StrAsPat(Str.of("0_zero"), empty),
            new IdentAsPat(Ident.create("x"), false, empty));
        RecPat recPat = builder.build();
        assertEquals("~label#{'0_zero': x}", recPat.toString());

        CompiledPat compiledPat = new CompiledPat(recPat, generator);
        compiledPat.compile();

        assertTrue(compiledPat.root() instanceof RecPtn);
        assertEquals(0, compiledPat.children().size());
        RecPtn result = (RecPtn) compiledPat.root();
        assertEquals(1, result.fieldCount());
        IdentPtn labelPtn = (IdentPtn) result.label();
        assertEquals(Ident.create("label"), labelPtn.ident);
        assertEquals(Str.of("0_zero"), result.fields().get(0).feature);
        IdentPtn valuePtn = (IdentPtn) result.fields().get(0).value;
        assertEquals(Ident.create("x"), valuePtn.ident);
        assertFalse(valuePtn.escaped);

        // label#{'0_zero': x}

        generator = new Generator();
        builder = RecPatBuilder.builder();
        builder.setLabelPat(new IdentAsPat(Ident.create("label"), false, empty));
        builder.addFieldPat(new StrAsPat(Str.of("0_zero"), empty),
            new IdentAsPat(Ident.create("x"), false, empty));
        recPat = builder.build();
        assertEquals("label#{'0_zero': x}", recPat.toString());


        compiledPat = new CompiledPat(recPat, generator);
        Exception exc = assertThrows(IllegalStateException.class, compiledPat::compile);
        assertEquals("A pattern label must be a literal or an escaped identifier", exc.getMessage());
    }

    @Test
    public void testTuplePatComplex() {

        // [x, [y]]

        SourceSpan empty = SourceSpan.emptySourceSpan();
        Generator generator = new Generator();
        TuplePatBuilder builder = TuplePatBuilder.builder();
        builder.addValuePat(new IdentAsPat(Ident.create("y"), false, empty));
        TuplePat yTuplePat = builder.build();
        builder = TuplePatBuilder.builder();
        builder.addValuePat(new IdentAsPat(Ident.create("x"), false, empty));
        builder.addValuePat(yTuplePat);
        TuplePat xTuplePat = builder.build();
        assertEquals("[x, [y]]", xTuplePat.toString());

        Ident dollarV0 = Ident.createSystemVarIdent(0);

        CompiledPat compiledPat = new CompiledPat(xTuplePat, generator);
        compiledPat.compile();

        assertTrue(compiledPat.root() instanceof RecPtn);
        assertEquals(1, compiledPat.children().size());
        RecPtn result = (RecPtn) compiledPat.root();
        assertEquals(2, result.fieldCount());
        FieldPtn fieldPtn0 = result.fields().get(0);
        assertEquals(Int32.I32_0, fieldPtn0.feature);
        IdentPtn valuePtn = (IdentPtn) fieldPtn0.value;
        assertEquals(Ident.create("x"), valuePtn.ident);
        assertFalse(valuePtn.escaped);
        FieldPtn fieldPtn1 = result.fields().get(1);
        assertEquals(Int32.I32_1, fieldPtn1.feature);
        valuePtn = (IdentPtn) fieldPtn1.value;
        assertEquals(dollarV0, valuePtn.ident);

        CompiledPat.ChildPtn childPtn = compiledPat.children().get(0);
        assertEquals(dollarV0, childPtn.arg);
        assertEquals(1, childPtn.recPtn.fields().size());
        FieldPtn fieldPtn = childPtn.recPtn.fields().get(0);
        assertEquals(Int32.I32_0, fieldPtn.feature);
        assertEquals(Ident.create("y"), ((IdentPtn) fieldPtn.value).ident);
    }

    @Test
    public void testTuplePatSimple() {

        // [x]

        SourceSpan empty = SourceSpan.emptySourceSpan();
        Generator generator = new Generator();
        TuplePatBuilder builder = TuplePatBuilder.builder();
        builder.addValuePat(new IdentAsPat(Ident.create("x"), false, empty));
        TuplePat tuplePat = builder.build();
        assertEquals("[x]", tuplePat.toString());

        CompiledPat compiledPat = new CompiledPat(tuplePat, generator);
        compiledPat.compile();

        assertTrue(compiledPat.root() instanceof RecPtn);
        assertEquals(0, compiledPat.children().size());
        RecPtn result = (RecPtn) compiledPat.root();
        assertEquals(1, result.fieldCount());
        assertEquals(Int32.I32_0, result.fields().get(0).feature);
        IdentPtn valuePtn = (IdentPtn) result.fields().get(0).value;
        assertEquals(Ident.create("x"), valuePtn.ident);
        assertFalse(valuePtn.escaped);
    }

    @Test
    public void testTuplePatSimpleEscapedValue() {

        // [~x]

        SourceSpan empty = SourceSpan.emptySourceSpan();
        Generator generator = new Generator();
        TuplePatBuilder builder = TuplePatBuilder.builder();
        builder.addValuePat(new IdentAsPat(Ident.create("x"), true, empty));
        TuplePat tuplePat = builder.build();
        assertEquals("[~x]", tuplePat.toString());

        CompiledPat compiledPat = new CompiledPat(tuplePat, generator);
        compiledPat.compile();

        assertTrue(compiledPat.root() instanceof RecPtn);
        assertEquals(0, compiledPat.children().size());
        RecPtn result = (RecPtn) compiledPat.root();
        assertEquals(1, result.fieldCount());
        assertEquals(Int32.I32_0, result.fields().get(0).feature);
        IdentPtn valuePtn = (IdentPtn) result.fields().get(0).value;
        assertEquals(Ident.create("x"), valuePtn.ident);
        assertTrue(valuePtn.escaped);
    }

    @Test
    public void testTuplePatSimpleWithLabel() {

        // ~label#[x]

        SourceSpan empty = SourceSpan.emptySourceSpan();
        Generator generator = new Generator();
        TuplePatBuilder builder = TuplePatBuilder.builder();
        builder.setLabelPat(new IdentAsPat(Ident.create("label"), true, empty));
        builder.addValuePat(new IdentAsPat(Ident.create("x"), false, empty));
        TuplePat tuplePat = builder.build();
        assertEquals("~label#[x]", tuplePat.toString());

        CompiledPat compiledPat = new CompiledPat(tuplePat, generator);
        compiledPat.compile();

        assertTrue(compiledPat.root() instanceof RecPtn);
        assertEquals(0, compiledPat.children().size());
        RecPtn result = (RecPtn) compiledPat.root();
        assertEquals(1, result.fieldCount());
        assertEquals(Int32.I32_0, result.fields().get(0).feature);
        IdentPtn labelPtn = (IdentPtn) result.label();
        assertEquals(Ident.create("label"), labelPtn.ident);
        IdentPtn valuePtn = (IdentPtn) result.fields().get(0).value;
        assertEquals(Ident.create("x"), valuePtn.ident);
        assertFalse(valuePtn.escaped);

        // 'my-label'#[x]
        generator = new Generator();
        builder = TuplePatBuilder.builder();
        builder.setLabelPat(new StrAsPat(Str.of("my-label"), empty));
        builder.addValuePat(new IdentAsPat(Ident.create("x"), false, empty));
        tuplePat = builder.build();
        assertEquals("'my-label'#[x]", tuplePat.toString());

        compiledPat = new CompiledPat(tuplePat, generator);
        compiledPat.compile();

        assertTrue(compiledPat.root() instanceof RecPtn);
        assertEquals(0, compiledPat.children().size());
        result = (RecPtn) compiledPat.root();
        assertEquals(1, result.fieldCount());
        assertEquals(Int32.I32_0, result.fields().get(0).feature);
        Str labelStr = (Str) result.label();
        assertEquals(Str.of("my-label"), labelStr);
        valuePtn = (IdentPtn) result.fields().get(0).value;
        assertEquals(Ident.create("x"), valuePtn.ident);
        assertFalse(valuePtn.escaped);

        // label#[x]
        generator = new Generator();
        builder = TuplePatBuilder.builder();
        builder.setLabelPat(new IdentAsPat(Ident.create("label"), false, empty));
        builder.addValuePat(new IdentAsPat(Ident.create("x"), false, empty));
        tuplePat = builder.build();
        assertEquals("label#[x]", tuplePat.toString());

        compiledPat = new CompiledPat(tuplePat, generator);
        Exception exc = assertThrows(IllegalStateException.class, compiledPat::compile);
        assertEquals("A pattern label must be a literal or an escaped identifier", exc.getMessage());
    }

}
