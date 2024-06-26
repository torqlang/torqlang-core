/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.examples;

import org.junit.jupiter.api.Test;

public class TestHelloWorld {

    @Test
    public void test() throws Exception {
        new HelloWorld().performWithErrorCheck();
    }

}
