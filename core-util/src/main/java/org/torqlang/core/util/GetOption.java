/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.util;

import java.util.ArrayList;
import java.util.List;

public final class GetOption {

    public static List<String> get(String option, List<String> args) {
        List<String> values = null;
        int i = 0;
        while (i < args.size()) {
            if (args.get(i).equals(option)) {
                values = new ArrayList<>();
                while (++i < args.size()) {
                    String a = args.get(i);
                    if (a.startsWith("-")) {
                        break;
                    }
                    values.add(a);
                }
            }
            i++;
        }
        return values != null ? List.copyOf(values) : null;
    }

    public static List<String> get(String option, List<String> args, int min, int max, List<String> allowed) {
        List<String> values = get(option, args);
        if (values == null) {
            if (min > 0) {
                throw new IllegalArgumentException("Option not found: " + option);
            }
        }
        int size = values == null ? 0 : values.size();
        if (size < min || size > max) {
            throw new IllegalArgumentException("Invalid argument count for option: " + option);
        }
        if (values != null && allowed != null) {
            for (String v : values) {
                if (!allowed.contains(v)) {
                    throw new IllegalArgumentException("Not allowed for option " + option + ": " + v);
                }
            }
        }
        return values;
    }

}
