/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.util;

import java.util.List;
import java.util.function.Function;

public class BinarySearchTools {

    public static <T> int search(T[] array, Function<T, Integer> comparing) {
        int low = 0;
        int high = array.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            T completeField = array[mid];
            int compare = comparing.apply(completeField);
            if (compare > 0) {
                low = mid + 1;
            } else if (compare < 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -(low + 1);
    }

    public static <T> int search(List<T> array, Function<T, Integer> comparing) {
        int low = 0;
        int high = array.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            T completeField = array.get(mid);
            int compare = comparing.apply(completeField);
            if (compare > 0) {
                low = mid + 1;
            } else if (compare < 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -(low + 1);
    }

}
