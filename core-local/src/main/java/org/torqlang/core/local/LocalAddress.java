/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

final class LocalAddress implements Address {

    private static final String NAMESPACE = "local";
    private static final String NAMESPACE_COLON = NAMESPACE + ":";

    private final String value;

    private LocalAddress(String path) {

        StringBuilder sb = new StringBuilder();

        sb.append(NAMESPACE_COLON);

        int lastIndex = path.length() - 1;
        char prevChar = ':';

        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9') {
                sb.append(c);
            } else if (c == '_' || c == '-' || c == '.') {
                sb.append(c);
            } else if (c == '/') {
                // Empty path elements are not allowed
                if (prevChar == '/') {
                    throw new IllegalArgumentException("Invalid path: " + path);
                }
                // Trim leading and trailing '/'
                if (i > 0 && i < lastIndex) {
                    sb.append(c);
                }
            } else {
                throw new IllegalArgumentException("Invalid path: " + path);
            }
            prevChar = c;
        }

        this.value = sb.toString();
    }

    static LocalAddress create(String path) {
        return new LocalAddress(path);
    }

    static LocalAddress create(LocalAddress parentAddress, String path) {
        if (path.charAt(0) == '/') {
            path = path.substring(1);
        }
        String parentPath = parentAddress.value.substring(NAMESPACE_COLON.length());
        return new LocalAddress(parentPath + "/" + path);
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
        return value.equals(that.value);
    }

    @Override
    public final int hashCode() {
        return value.hashCode();
    }

    @Override
    public final String toString() {
        return value;
    }

}
