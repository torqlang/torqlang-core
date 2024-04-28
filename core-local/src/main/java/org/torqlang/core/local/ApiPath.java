/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import java.util.ArrayList;
import java.util.List;

public final class ApiPath implements Comparable<ApiPath> {

    public final List<ApiPathParam> params;
    public final List<String> segs;

    public ApiPath(String path) {
        if (path.equals("/")) {
            this.segs = List.of();
        } else {
            if (path.isEmpty() || path.charAt(0) != '/') {
                throw new IllegalArgumentException("Does not begin with a '/': " + path);
            }
            if (path.charAt(path.length() - 1) == '/') {
                throw new IllegalArgumentException("Cannot end with a '/': " + path);
            }
            List<String> segsParsed = List.of(path.split("/"));
            this.segs = segsParsed.subList(1, segsParsed.size());
        }
        List<ApiPathParam> paramsList = new ArrayList<>();
        for (int i = 0; i < segs.size(); i++) {
            String seg = segs.get(i);
            if (isWildcard(seg)) {
                paramsList.add(new ApiPathParam(i, seg.substring(1, seg.length() - 1)));
            }
        }
        this.params = List.copyOf(paramsList);
    }

    private static int compareSeg(String seg, String targetSeg) {
        if (isWildcard(seg)) {
            return 0;
        }
        if (isWildcard(targetSeg)) {
            return 0;
        }
        return seg.compareTo(targetSeg);
    }

    private static boolean isWildcard(String seg) {
        char firstChar = seg.charAt(0);
        if (firstChar == '{') {
            char lastChar = seg.charAt(seg.length() - 1);
            return lastChar == '}';
        } else {
            return false;
        }
    }

    public final int compareSegs(List<String> targetSegs) {
        for (int i = 0; i < segs.size(); i++) {
            if (i < targetSegs.size()) {
                int compare = compareSeg(segs.get(i), targetSegs.get(i));
                if (compare != 0) {
                    return compare;
                }
            }
        }
        return Integer.compare(segs.size(), targetSegs.size());
    }

    @Override
    public final int compareTo(ApiPath apiPath) {
        return compareSegs(apiPath.segs);
    }

}
