/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

public interface Address extends Comparable<Address> {

    static Address create(Address parent, String path) {
        return LocalAddress.create(parent, path);
    }

    static Address create(String path) {
        return LocalAddress.create(path);
    }

    String path();

}
