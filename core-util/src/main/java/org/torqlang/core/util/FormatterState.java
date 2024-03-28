/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.util;

import java.io.IOException;
import java.io.Writer;

public class FormatterState {

    public static final int INLINE_VALUE = -1;
    public static final char NEWLINE = '\n';
    public static final char SPACE = ' ';

    private static final int INDENT_SIZE = 4;

    private final int level;
    private final Writer writer;

    public FormatterState(Writer writer) {
        this(writer, 0);
    }

    private FormatterState(Writer writer, int level) {
        this.writer = writer;
        this.level = level;
    }

    public final void flush()
        throws IOException
    {
        writer.flush();
    }

    public final FormatterState inline() {
        return level == INLINE_VALUE ? this :
            new FormatterState(writer, INLINE_VALUE);
    }

    public final int level() {
        return level;
    }

    public final FormatterState nextLevel() {
        return level == INLINE_VALUE ? this :
            new FormatterState(writer, level + 1);
    }

    public final void write(char c)
        throws IOException
    {
        writer.write(c);
    }

    public final void write(String s)
        throws IOException
    {
        writer.write(s);
    }

    public final void writeAfterNewLineAndIdent(String s)
        throws IOException
    {
        writeNewLine();
        writeIndent();
        write(s);
    }

    public final void writeIndent()
        throws IOException
    {
        if (level == 0 || level == INLINE_VALUE) {
            return;
        }
        int totalIndent = level * INDENT_SIZE;
        for (int i = 0; i < totalIndent; i++) {
            writer.write(SPACE);
        }
    }

    public final void writeNewLine()
        throws IOException
    {
        if (level == INLINE_VALUE) {
            writer.write(SPACE);
        } else {
            writer.write(NEWLINE);
        }
    }

    public final void writeNewLineAndIndent()
        throws IOException
    {
        writeNewLine();
        writeIndent();
    }

}
