/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.examples;


import org.torqlang.core.klvm.FailedValue;

import java.util.Objects;

public final class ExamplesTools {

    /*
     * Argument order (expected, response) is the same order used in JUnit assert methods.
     */
    public static void checkExpectedResponse(Object expected, Object response) {
        if (!Objects.equals(response, expected)) {
            String error = "Invalid response: " + response;
            if (response instanceof FailedValue failedValue) {
                error += "\n" + failedValue.toDetailsString();
            }
            throw new IllegalStateException(error);
        }
    }

}
