/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;
import org.torqlang.core.klvm.*;

import static org.junit.Assert.*;

/*
 * This test class should be in the kernel package, but we test from the lang package to use the Evaluator.
 */
public class TestKernelProcs {

    @Test
    public void testAssertBoundFailure() throws Exception {
        String source = """
            begin
                assert_bound(x)
                y = 'success'
            end""";
        EvaluatorGenerated g = Evaluator.builder()
            .addVar(KernelProcs.ASSERT_BOUND_IDENT, new Var(KernelProcs.ASSERT_BOUND_PROC))
            .addVar(Ident.create("x"))
            .addVar(Ident.create("y"))
            .setSource(source)
            .generate();
        assertEquals(source, g.sntcOrExpr().toString());
        String expected = """
            assert_bound(x)
            $bind('success', y)""";
        assertEquals(expected, g.kernel().toString());
        MachineHaltError mhe = assertThrows(MachineHaltError.class, g::perform);
        assertTrue(mhe.nativeCause() instanceof NotBoundError);
        assertEquals("Not bound error", mhe.nativeCause().getMessage());
    }

    @Test
    public void testAssertBoundSuccess() throws Exception {
        String source = """
            begin
                x = 1
                assert_bound(x)
                y = 'success'
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(KernelProcs.ASSERT_BOUND_IDENT, new Var(KernelProcs.ASSERT_BOUND_PROC))
            .addVar(Ident.create("x"))
            .addVar(Ident.create("y"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            $bind(1, x)
            assert_bound(x)
            $bind('success', y)""";
        assertEquals(expected, e.kernel().toString());
        Str y = (Str) e.varAtName("y").valueOrVarSet();
        assertEquals("success", y.value);
    }

    @Test
    public void testIsDet() throws Exception {
        String source = """
            begin
                x = {'0-zero': 'zero'}
                y = is_det(x)
                z = is_bound(x)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(KernelProcs.IS_DET_IDENT, new Var(KernelProcs.IS_DET_PROC))
            .addVar(KernelProcs.IS_BOUND_IDENT, new Var(KernelProcs.IS_BOUND_PROC))
            .addVar(Ident.create("x"))
            .addVar(Ident.create("y"))
            .addVar(Ident.create("z"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            $bind({'0-zero': 'zero'}, x)
            is_det(x, y)
            is_bound(x, z)""";
        assertEquals(expected, e.kernel().toString());
        Bool y = (Bool) e.varAtName("y").valueOrVarSet();
        assertTrue(y.value);
        Bool z = (Bool) e.varAtName("z").valueOrVarSet();
        assertTrue(z.value);
    }

    @Test
    public void testIsNotDet() throws Exception {
        String source = """
            begin
                var a
                x = {a: 'zero'}
                y = is_det(x)
                z = is_bound(x)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(KernelProcs.IS_DET_IDENT, new Var(KernelProcs.IS_DET_PROC))
            .addVar(KernelProcs.IS_BOUND_IDENT, new Var(KernelProcs.IS_BOUND_PROC))
            .addVar(Ident.create("x"))
            .addVar(Ident.create("y"))
            .addVar(Ident.create("z"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local a in
                $create_rec({a: 'zero'}, x)
                is_det(x, y)
                is_bound(x, z)
            end""";
        assertEquals(expected, e.kernel().toString());
        Bool y = (Bool) e.varAtName("y").valueOrVarSet();
        assertFalse(y.value);
        Bool z = (Bool) e.varAtName("z").valueOrVarSet();
        assertTrue(z.value);
    }

}