/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import java.util.Map;

public enum ProductOper {

    MULTIPLY("*"),
    DIVIDE("/"),
    MODULO("%");

    private static final Map<String, ProductOper> symbolToValueMap = Map.of(
        MULTIPLY.symbol(), MULTIPLY,
        DIVIDE.symbol(), DIVIDE,
        MODULO.symbol(), MODULO
    );

    private final String symbol;

    ProductOper(String symbol) {
        this.symbol = symbol;
    }

    public static ProductOper valueForSymbol(String symbol) {
        return symbolToValueMap.get(symbol);
    }

    public final String symbol() {
        return symbol;
    }

}
