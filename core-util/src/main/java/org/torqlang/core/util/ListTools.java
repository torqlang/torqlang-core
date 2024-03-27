/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ListTools {

    public static <T> List<T> append(Class<T> elementClass, List<? extends T> a, T b) {
        @SuppressWarnings("unchecked")
        T[] content = (T[]) Array.newInstance(elementClass, a.size() + 1);
        int aSize = a.size();
        for (int i = 0; i < aSize; i++) {
            content[i] = a.get(i);
        }
        content[aSize] = b;
        return List.of(content);
    }

    public static <T> List<T> concat(Class<T> elementClass, List<? extends T> a, List<? extends T> b) {
        @SuppressWarnings("unchecked")
        T[] content = (T[]) Array.newInstance(elementClass, a.size() + b.size());
        int aSize = a.size();
        for (int i = 0; i < aSize; i++) {
            content[i] = a.get(i);
        }
        int bSize = b.size();
        for (int i = 0; i < bSize; i++) {
            content[i + aSize] = b.get(i);
        }
        return List.of(content);
    }

    public static <T> T last(List<? extends T> list) {
        return list.get(list.size() - 1);
    }

    public static <T> List<T> nullSafeCopyOf(Collection<? extends T> list) {
        return list == null ? Collections.emptyList() : List.copyOf(list);
    }

}
