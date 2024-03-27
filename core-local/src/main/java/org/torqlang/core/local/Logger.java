/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

public interface Logger {

    void info(String message);

    void info(String caller, String message);

    void error(String message);

    void error(String caller, String message);

    void warn(String message);

    void warn(String caller, String message);

}
