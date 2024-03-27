/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import java.util.Map;

public enum SumOper {
    ADD("+"),
    SUBTRACT("-");

    private static final Map<String, SumOper> symbolToValueMap = Map.of(
        ADD.symbol(), ADD,
        SUBTRACT.symbol(), SUBTRACT
    );

    private final String symbol;

    SumOper(String symbol) {
        this.symbol = symbol;
    }

    public static SumOper valueForSymbol(String symbol) {
        return symbolToValueMap.get(symbol);
    }

    public final String symbol() {
        return symbol;
    }
}
