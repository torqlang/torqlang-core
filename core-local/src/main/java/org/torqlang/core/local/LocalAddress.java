/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

final class LocalAddress implements Address {

    private final String path;

    private LocalAddress(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Invalid path: " + path);
        }
        // Trim beginning '/'
        int begin = 0;
        if (path.charAt(begin) == '/') {
            begin = 1;
        }
        // Trim ending '/'
        int length = path.length();
        if (path.charAt(length - 1) == '/') {
            length = length - 1;
        }
        if (begin >= length) {
            throw new IllegalArgumentException("Invalid path: " + path);
        }
        for (int i = begin; i < length; i++) {
            char c = path.charAt(i);
            if (!isValidPathChar(c)) {
                throw new IllegalArgumentException("Invalid path: " + path);
            }
            if (c == '/') {
                // Empty segments are not allowed. Recall that beginning and ending '/' have already been trimmed.
                // Therefore, no range check is necessary because there must be preceding and succeeding characters.
                if (path.charAt(i - 1) == '/') {
                    throw new IllegalArgumentException("Invalid path: " + path);
                }
                if (path.charAt(i + 1) == '/') {
                    throw new IllegalArgumentException("Invalid path: " + path);
                }
            }
        }
        this.path = path.substring(begin, length);
    }

    static LocalAddress create(String path) {
        return new LocalAddress(path);
    }

    static LocalAddress create(Address parent, String path) {
        if (path.charAt(0) == '/') {
            path = path.substring(1);
        }
        return new LocalAddress(parent.path() + "/" + path);
    }

    private static boolean isValidPathChar(char c) {
        // a-z
        // A-Z
        // 0-9
        // _ - . /
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' ||
            c == '_' || c == '-' || c == '.' || c == '/';
    }

    @Override
    public int compareTo(Address address) {
        return path.compareTo(address.path());
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        LocalAddress that = (LocalAddress) other;
        return path.equals(that.path);
    }

    @Override
    public final int hashCode() {
        return path.hashCode();
    }

    @Override
    public final String path() {
        return path;
    }

    @Override
    public final String toString() {
        return path;
    }

}
