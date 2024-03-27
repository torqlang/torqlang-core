/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestStringTools {

    @Test
    public void testHexString() {
        StringBuilder sb;

        sb = new StringBuilder();
        StringTools.appendHexString('\n', sb);
        //noinspection UnnecessaryUnicodeEscape
        assertEquals("000a", sb.toString());
    }

    @Test
    public void testPadBuffer() {
        StringBuilder sb;

        sb = new StringBuilder();
        StringTools.appendWithPadLeft("", '0', 0, sb);
        assertEquals("", sb.toString());

        // blank

        sb = new StringBuilder();
        StringTools.appendWithPadLeft("", '0', 3, sb);
        assertEquals("000", sb.toString());

        sb = new StringBuilder();
        StringTools.appendWithPadRight("", '0', 3, sb);
        assertEquals("000", sb.toString());

        sb = new StringBuilder();
        StringTools.appendWithPadLeft("", '0', 2, sb);
        assertEquals("00", sb.toString());

        sb = new StringBuilder();
        StringTools.appendWithPadRight("", '0', 2, sb);
        assertEquals("00", sb.toString());

        sb = new StringBuilder();
        StringTools.appendWithPadLeft("", '0', 1, sb);
        assertEquals("0", sb.toString());

        sb = new StringBuilder();
        StringTools.appendWithPadRight("", '0', 1, sb);
        assertEquals("0", sb.toString());

        // 1

        sb = new StringBuilder();
        StringTools.appendWithPadLeft("1", '0', 3, sb);
        assertEquals("001", sb.toString());

        sb = new StringBuilder();
        StringTools.appendWithPadRight("1", '0', 3, sb);
        assertEquals("100", sb.toString());

        sb = new StringBuilder();
        StringTools.appendWithPadLeft("1", '0', 2, sb);
        assertEquals("01", sb.toString());

        sb = new StringBuilder();
        StringTools.appendWithPadRight("1", '0', 2, sb);
        assertEquals("10", sb.toString());

        sb = new StringBuilder();
        StringTools.appendWithPadLeft("1", '0', 1, sb);
        assertEquals("1", sb.toString());

        sb = new StringBuilder();
        StringTools.appendWithPadRight("1", '0', 1, sb);
        assertEquals("1", sb.toString());

        // 11

        sb = new StringBuilder();
        StringTools.appendWithPadLeft("11", '0', 3, sb);
        assertEquals("011", sb.toString());

        sb = new StringBuilder();
        StringTools.appendWithPadRight("11", '0', 3, sb);
        assertEquals("110", sb.toString());

        sb = new StringBuilder();
        StringTools.appendWithPadLeft("11", '0', 2, sb);
        assertEquals("11", sb.toString());

        sb = new StringBuilder();
        StringTools.appendWithPadRight("11", '0', 2, sb);
        assertEquals("11", sb.toString());

        sb = new StringBuilder();
        StringTools.appendWithPadLeft("11", '0', 1, sb);
        assertEquals("11", sb.toString());

        sb = new StringBuilder();
        StringTools.appendWithPadRight("11", '0', 1, sb);
        assertEquals("11", sb.toString());

        // 111

        sb = new StringBuilder();
        StringTools.appendWithPadLeft("111", '0', 3, sb);
        assertEquals("111", sb.toString());

        sb = new StringBuilder();
        StringTools.appendWithPadRight("111", '0', 3, sb);
        assertEquals("111", sb.toString());

        sb = new StringBuilder();
        StringTools.appendWithPadLeft("111", '0', 2, sb);
        assertEquals("111", sb.toString());

        sb = new StringBuilder();
        StringTools.appendWithPadRight("111", '0', 2, sb);
        assertEquals("111", sb.toString());

        sb = new StringBuilder();
        StringTools.appendWithPadLeft("111", '0', 1, sb);
        assertEquals("111", sb.toString());

        sb = new StringBuilder();
        StringTools.appendWithPadRight("111", '0', 1, sb);
        assertEquals("111", sb.toString());
    }

}
