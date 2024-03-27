/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public interface SourceSpan {

    static SourceSpan adjoin(Collection<? extends SourceSpan> sourceSpans) {
        Iterator<? extends SourceSpan> iter = sourceSpans.iterator();
        SourceSpan answer = iter.next();
        while (iter.hasNext()) {
            answer = answer.adjoin(iter.next());
        }
        return answer;
    }

    static SourceSpan emptySourceSpan() {
        return EmptySourceSpan.EMPTY_SOURCE_SPAN;
    }

    private static String lineNrStr(int lineNr, int width) {
        StringBuilder sb = new StringBuilder(width);
        StringTools.appendWithPadLeft(String.valueOf(lineNr), '0', width, sb);
        return sb.toString();
    }

    private static boolean showLine(int i, int lineIndex, int showBefore, int showAfter) {
        return i == lineIndex ||
            (i < lineIndex && (lineIndex - i) <= showBefore) ||
            (i > lineIndex && (i - lineIndex) <= showAfter);
    }

    private static List<String> toSourceLines(String source, int baseLineNr, int lineNrWidth) {
        int lineNr = baseLineNr;
        List<String> answer = new ArrayList<>();
        StringBuilder sourceLine = null;
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (c == '\n') {
                String lineNrStr = lineNrStr(lineNr, lineNrWidth);
                if (sourceLine == null) {
                    answer.add(lineNrStr);
                } else {
                    answer.add(lineNrStr + " " + sourceLine);
                    sourceLine = null;
                }
                lineNr++;
            } else {
                if (sourceLine == null) {
                    sourceLine = new StringBuilder();
                }
                sourceLine.append(c);
            }
        }
        if (sourceLine != null) {
            answer.add(lineNrStr(lineNr, lineNrWidth) + " " + sourceLine);
        }
        return answer;
    }

    private static SourceLocation toSourceLocation(SourceSpan sourceSpan, int baseLineNr) {
        int lineNumber = baseLineNr;
        int charPosInLine = 0;
        int i = 0;
        while (i < sourceSpan.source().length() && i < sourceSpan.begin()) {
            char c = sourceSpan.source().charAt(i);
            if (c == '\n') {
                lineNumber++;
                charPosInLine = 0;
            } else {
                charPosInLine++;
            }
            i++;
        }
        return new SourceLocation(lineNumber, charPosInLine);
    }

    default SourceSpan adjoin(SourceSpan other) {
        return new AdjoinedSourceSpan(this, other);
    }

    int begin();

    int end();

    default String formatWithMessage(String message, int lineNrWidth, int showBefore, int showAfter) {
        SourceLocation location = toSourceLocation(this, 0);
        StringBuilder answerBuf = new StringBuilder();
        int lineIndex = location.lineIndex;
        List<String> sourceLines = SourceSpan.toSourceLines(source(), 1, lineNrWidth);
        boolean lineAppended = false;
        for (int i = 0; i < sourceLines.size(); i++) {
            String line = sourceLines.get(i);
            if (showLine(i, lineIndex, showBefore, showAfter)) {
                if (lineAppended) {
                    answerBuf.append('\n');
                }
                answerBuf.append(line);
                lineAppended = true;
            }
            if (i == lineIndex) {
                if (lineAppended) {
                    answerBuf.append('\n');
                }
                StringBuilder indentBuf = new StringBuilder();
                int indentLength = lineNrWidth + 1 + location.charIndexInLine;
                while (indentBuf.length() < indentLength) {
                    indentBuf.insert(0, " ");
                }
                answerBuf.append(indentBuf);
                answerBuf.append("^__ ");
                answerBuf.append(message != null ? message : "Error");
                lineAppended = true;
            }
        }
        return answerBuf.toString();
    }

    String source();

    SourceSpan toSourceSpanBegin();

    SourceSpan toSourceSpanEnd();

}
