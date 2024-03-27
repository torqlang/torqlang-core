/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class StmtList implements Iterable<Stmt> {

    private Entry first;
    private Entry last;
    private int size;

    public StmtList() {
    }

    public StmtList(Iterable<Stmt> stmts) {
        addAll(stmts);
    }

    public final void add(Stmt stmt) {
        Entry e = new Entry(stmt);
        e.prev = last;
        if (first == null) {
            first = e;
        }
        if (last != null) {
            last.next = e;
        }
        last = e;
        size++;
    }

    public final void addAll(Iterable<Stmt> stmts) {
        for (Stmt s : stmts) {
            add(s);
        }
    }

    final StmtList.Entry firstEntry() {
        return first;
    }

    @Override
    public final Iterator<Stmt> iterator() {
        return new StmtIterator(first);
    }

    final StmtList.Entry lastEntry() {
        return last;
    }

    public final int size() {
        return size;
    }

    static class Entry {
        private final Stmt stmt;
        private Entry prev;
        private Entry next;

        private Entry(Stmt stmt) {
            this.stmt = stmt;
        }

        final Entry next() {
            return next;
        }

        final Entry prev() {
            return prev;
        }

        final Stmt stmt() {
            return stmt;
        }
    }

    public static class StmtIterator implements Iterator<Stmt> {

        private Entry next;

        StmtIterator(Entry first) {
            next = first;
        }

        @Override
        public final boolean hasNext() {
            return next != null;
        }

        @Override
        public final Stmt next() {
            if (next == null) {
                throw new NoSuchElementException("Next element is not present");
            }
            Stmt answer = next.stmt;
            next = next.next;
            return answer;
        }
    }

}
