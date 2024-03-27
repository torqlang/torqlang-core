/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import java.io.PrintStream;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class ConsoleLogger implements Logger {

    public final static ConsoleLogger SINGLETON = new ConsoleLogger();

    private static final Clock TICK_MILLIS = Clock.tickMillis(ZoneId.systemDefault());

    private ConsoleLogger() {
    }

    private static String prefix(String level, String caller) {
        LocalDateTime time = LocalDateTime.now(TICK_MILLIS);
        return "[" + level + "]" + "[" + time + "]" + "[" + Thread.currentThread().getName() + "]" +
            (caller != null ? "[" + caller + "]" : "");
    }

    @Override
    public void error(String message) {
        log(System.err, prefix("ERROR", null), message);
    }

    @Override
    public void error(String caller, String message) {
        log(System.err, prefix("ERROR", caller), message);
    }

    @Override
    public void info(String message) {
        log(System.out, prefix("INFO ", null), message);
    }

    @Override
    public void info(String caller, String message) {
        log(System.out, prefix("INFO ", caller), message);
    }

    private void log(PrintStream stream, String prefix, String message) {
        stream.println(prefix + " " + message);
    }

    @Override
    public void warn(String message) {
        log(System.out, prefix("WARN ", null), message);
    }

    @Override
    public void warn(String caller, String message) {
        log(System.out, prefix("WARN ", caller), message);
    }

}
