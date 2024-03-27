/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.math.BigDecimal;
import java.math.MathContext;

@State(Scope.Benchmark)
public class BenchJavaFactorialState {

    public final BigDecimal initial = new BigDecimal("1", MathContext.DECIMAL128);
    public final BigDecimal request = new BigDecimal("100", MathContext.DECIMAL128);
    public final int iterations = 100;

}
