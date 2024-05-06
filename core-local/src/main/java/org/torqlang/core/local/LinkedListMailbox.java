/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import java.util.Comparator;

public class LinkedListMailbox implements Mailbox {

    private final Comparator<Envelope> priorityComparator;

    private Entry first;
    private Entry last;
    private int size;

    public LinkedListMailbox(Comparator<Envelope> priorityComparator) {
        this.priorityComparator = priorityComparator;
    }

    @Override
    public final void add(Envelope envelope) {
        Entry before = null;
        Entry after = last;
        if (priorityComparator != null) {
            for (; after != null; after = after.prev) {
                if (priorityComparator.compare(envelope, after.message) >= 0) {
                    break;
                }
                before = after;
            }
        }
        insert(new Entry(envelope), before, after);
    }

    public final Entry firstEntry() {
        return first;
    }

    private void insert(Entry entry, Entry before, Entry after) {
        entry.prev = after;
        if (after != null) {
            after.next = entry;
        } else {
            first = entry;
        }
        entry.next = before;
        if (before != null) {
            before.prev = entry;
        } else {
            last = entry;
        }
        size++;
    }

    @Override
    public final boolean isEmpty() {
        return size == 0;
    }

    public final Entry lastEntry() {
        return last;
    }

    @Override
    public final Envelope peekNext() {
        return first != null ? first.message : null;
    }

    @Override
    public final Envelope removeNext() {
        if (first == null) {
            return null;
        }
        Envelope answer = first.message;
        if (first == last) {
            // We are a list of one
            first = null;
            last = null;
        } else {
            // We are a list greater than one
            first = first.next;
            if (first != null) {
                first.prev = null;
            }
        }
        size--;
        return answer;
    }

    @Override
    public final int size() {
        return size;
    }

    public final static class Entry {

        private final Envelope message;
        private Entry next;
        private Entry prev;

        private Entry(Envelope message) {
            this.message = message;
        }

        public final Envelope message() {
            return message;
        }

        public final Entry next() {
            return next;
        }

        public final Entry prev() {
            return prev;
        }

    }

}
