/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestApiPath {

    @Test
    public void test01() {

        ApiPath path;

        path = new ApiPath("/");
        assertEquals(0, path.extractParams().size());
        assertEquals(0, path.compareSegs(List.of()));
        assertEquals(0, path.compareTo(new ApiPath("/")));

        path = new ApiPath("/x");
        assertEquals(0, path.extractParams().size());
        assertEquals(0, path.compareSegs(List.of("x")));
        assertEquals(0, path.compareSegs(List.of("{}")));
        assertEquals(0, path.compareSegs(List.of("{id}")));
        assertEquals(0, path.compareTo(new ApiPath("/x")));
        assertEquals(0, path.compareTo(new ApiPath("/{}")));
        assertEquals(0, path.compareTo(new ApiPath("/{id}")));

        path = new ApiPath("/{}");
        assertEquals(1, path.extractParams().size());
        assertEquals(0, path.extractParams().get(0).pos());
        assertEquals("", path.extractParams().get(0).name());
        assertEquals(0, path.compareSegs(List.of("x")));
        assertEquals(0, path.compareSegs(List.of("{}")));
        assertEquals(0, path.compareSegs(List.of("{id}")));
        assertEquals(0, path.compareTo(new ApiPath("/x")));
        assertEquals(0, path.compareTo(new ApiPath("/{}")));
        assertEquals(0, path.compareTo(new ApiPath("/{id}")));

        path = new ApiPath("/x/y");
        assertEquals(0, path.extractParams().size());
        assertEquals(0, path.compareSegs(List.of("x", "y")));
        assertEquals(0, path.compareSegs(List.of("{}", "y")));
        assertEquals(0, path.compareSegs(List.of("x", "{}")));
        assertEquals(0, path.compareSegs(List.of("{id}", "y")));
        assertEquals(0, path.compareSegs(List.of("x", "{id}")));
        assertEquals(0, path.compareTo(new ApiPath("/x/y")));
        assertEquals(0, path.compareTo(new ApiPath("/{}/y")));
        assertEquals(0, path.compareTo(new ApiPath("/x/{}")));
        assertEquals(0, path.compareTo(new ApiPath("/{id}/y")));
        assertEquals(0, path.compareTo(new ApiPath("/x/{id}")));

        path = new ApiPath("/{foo}/y");
        assertEquals(1, path.extractParams().size());
        assertEquals(0, path.extractParams().get(0).pos());
        assertEquals("foo", path.extractParams().get(0).name());
        assertEquals(0, path.compareSegs(List.of("x", "y")));
        assertEquals(0, path.compareSegs(List.of("{}", "y")));
        assertEquals(0, path.compareSegs(List.of("x", "{}")));
        assertEquals(0, path.compareSegs(List.of("{id}", "y")));
        assertEquals(0, path.compareSegs(List.of("x", "{id}")));
        assertEquals(0, path.compareTo(new ApiPath("/x/y")));
        assertEquals(0, path.compareTo(new ApiPath("/{}/y")));
        assertEquals(0, path.compareTo(new ApiPath("/x/{}")));
        assertEquals(0, path.compareTo(new ApiPath("/{id}/y")));
        assertEquals(0, path.compareTo(new ApiPath("/x/{id}")));

        path = new ApiPath("/x/{bar}");
        assertEquals(1, path.extractParams().size());
        assertEquals(1, path.extractParams().get(0).pos());
        assertEquals("bar", path.extractParams().get(0).name());
        assertEquals(0, path.compareSegs(List.of("x", "y")));
        assertEquals(0, path.compareSegs(List.of("{}", "y")));
        assertEquals(0, path.compareSegs(List.of("x", "{}")));
        assertEquals(0, path.compareSegs(List.of("{id}", "y")));
        assertEquals(0, path.compareSegs(List.of("x", "{id}")));
        assertEquals(0, path.compareTo(new ApiPath("/x/y")));
        assertEquals(0, path.compareTo(new ApiPath("/{}/y")));
        assertEquals(0, path.compareTo(new ApiPath("/x/{}")));
        assertEquals(0, path.compareTo(new ApiPath("/{id}/y")));
        assertEquals(0, path.compareTo(new ApiPath("/x/{id}")));
    }

}
