/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.examples;

import org.torqlang.core.klvm.FailedValue;
import org.torqlang.core.util.ErrorWithSourceSpan;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public abstract class AbstractExample {

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

    public static void checkNotFailedValue(Object response) {
        if (response instanceof FailedValue failedValue) {
            String error = "Invalid response: " + response + "\n" + failedValue.toDetailsString();
            throw new IllegalStateException(error);
        }
    }

    /*
     * This method depends on the working-directory setting. The relativePath will be combined with the working
     * directory to form an absolute path. For example, to run while developing with IntelliJ, the working directory is
     * set to something like "C:\Users\USERNAME\developer\projects\idf-console-bff\src\main\resources"
     */
    public static String readTextFromFile(String relativePath) throws IOException {
        Path absolutePath = Paths.get(relativePath).toAbsolutePath();
        return Files.readString(absolutePath);
    }

    public static String readTextFromResource(String absolutePath) throws IOException {
        URL url = NorthwindCache.class.getResource(absolutePath);
        if (url == null) {
            throw new FileNotFoundException(absolutePath);
        }
        try (InputStream s = url.openStream()) {
            return new String(s.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static void showErrorAndRethrow(Exception exc) throws Exception {
        if (exc instanceof ErrorWithSourceSpan errorWithSourceSpan) {
            System.err.println(errorWithSourceSpan.formatWithSource(5, 5, 5));
        }
        throw exc;
    }

    public abstract void perform() throws Exception;

    public final void performWithErrorCheck() throws Exception {
        try {
            perform();
        } catch (Exception exc) {
            showErrorAndRethrow(exc);
        }
    }

}
