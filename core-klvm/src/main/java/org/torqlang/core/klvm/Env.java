/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.*;

/*
 * An environment is a read-only mapping of identifiers to variables.
 */
public interface Env extends Kernel, Iterable<EnvEntry> {

    /*
     * Create an environment with the given parent environment and bindings, but if the parent environment is
     * the empty environment, use null in its place to remove a level of unnecessary indirection.
     */
    static Env create(Env parentEnv, List<EnvEntry> bindings) {
        if (parentEnv == ArrayEnv.EMPTY_ENV) {
            parentEnv = null;
        }
        if (bindings.isEmpty()) {
            return parentEnv == null ? ArrayEnv.EMPTY_ENV : parentEnv;
        }
        return new ArrayEnv(parentEnv, bindings);
    }

    /*
     * Create an environment with the given parent environment and bindings, but if the parent environment is
     * the empty environment, use null in its place to remove a level of unnecessary indirection.
     */
    static Env create(Env parentEnv, Map<Ident, Var> bindings) {
        if (parentEnv == ArrayEnv.EMPTY_ENV) {
            parentEnv = null;
        }
        if (bindings.isEmpty()) {
            return parentEnv == null ? ArrayEnv.EMPTY_ENV : parentEnv;
        }
        return new ArrayEnv(parentEnv, bindings);
    }

    static Env create(Env parentEnv, EnvEntry e1) {
        if (e1 == null) {
            throw new NullPointerException();
        }
        return new ArrayEnv(parentEnv, new EnvEntry[]{e1});
    }

    static Env create(Env parentEnv, EnvEntry e1, EnvEntry e2) {
        if (e1 == null || e2 == null) {
            throw new NullPointerException();
        }
        return new ArrayEnv(parentEnv, new EnvEntry[]{e1, e2});
    }

    static Env create(Env parentEnv, EnvEntry e1, EnvEntry e2, EnvEntry e3) {
        if (e1 == null || e2 == null || e3 == null) {
            throw new NullPointerException();
        }
        return new ArrayEnv(parentEnv, new EnvEntry[]{e1, e2, e3});
    }

    static Env create(List<EnvEntry> bindings) {
        if (bindings.isEmpty()) {
            return ArrayEnv.EMPTY_ENV;
        }
        return new ArrayEnv(null, bindings);
    }

    /*
     * Create a root environment with the given bindings, but if the bindings are empty, return an empty environment.
     */
    static Env create(Map<Ident, Var> bindings) {
        return bindings.isEmpty() ? ArrayEnv.EMPTY_ENV : new ArrayEnv(null, bindings);
    }

    static Env create(EnvEntry e1) {
        if (e1 == null) {
            throw new NullPointerException();
        }
        return new ArrayEnv(null, new EnvEntry[]{e1});
    }

    static Env create(EnvEntry e1, EnvEntry e2) {
        if (e1 == null || e2 == null) {
            throw new NullPointerException();
        }
        return new ArrayEnv(null, new EnvEntry[]{e1, e2});
    }

    /*
     * Create an Env with these exact parameters. This method is KLVM internal-use-only that avoids creating an extra
     * collection instance when computing an Env for computing local statements, closures, and native statements.
     */
    static Env createPrivatelyForKlvm(Env parentEnv, EnvEntry[] bindings) {
        return new ArrayEnv(parentEnv, bindings);
    }

    /*
     * Return the empty environment (a private singleton).
     */
    static Env emptyEnv() {
        return ArrayEnv.EMPTY_ENV;
    }

    Env add(EnvEntry entry);

    Set<Ident> collectIdents(Var var);

    void collectIdents(Var var, Set<Ident> collector);

    boolean contains(Ident ident);

    String formatValue();

    Var get(Ident ident);

    EnvEntry getEnvEntry(int index);

    Env parentEnv();

    Env rootEnv();

    /*
     * Return a copy of the current environment but extended with 'rootEnv'
     */
    Env setRootEnv(Env rootEnv);

    int shallowSize();

    class ArrayEnv implements Env {

        private static final Env EMPTY_ENV = new ArrayEnv();

        private final Env parentEnv;
        private final EnvEntry[] bindings;

        private ArrayEnv() {
            parentEnv = null;
            bindings = new EnvEntry[0];
        }

        private ArrayEnv(Env parentEnv, EnvEntry[] bindings) {
            this.parentEnv = parentEnv;
            this.bindings = bindings;
        }

        private ArrayEnv(Env parentEnv, Map<Ident, Var> bindings) {
            this(parentEnv, makeBindingsArray(bindings));
        }

        private ArrayEnv(Env parentEnv, List<EnvEntry> bindings) {
            this(parentEnv, makeBindingsArray(bindings));
        }

        private static EnvEntry[] makeBindingsArray(List<EnvEntry> bindings) {
            EnvEntry[] answer = new EnvEntry[bindings.size()];
            for (int i = 0; i < bindings.size(); i++) {
                answer[i] = bindings.get(i);
            }
            return answer;
        }

        private static EnvEntry[] makeBindingsArray(Map<Ident, Var> bindings) {
            EnvEntry[] answer = new EnvEntry[bindings.size()];
            int i = 0;
            for (Map.Entry<Ident, Var> e : bindings.entrySet()) {
                answer[i++] = new EnvEntry(e.getKey(), e.getValue());
            }
            return answer;
        }

        @Override
        public final <T, R> R accept(KernelVisitor<T, R> visitor, T state) throws Exception {
            return visitor.visitEnv(this, state);
        }

        /*
         * Return a new Env containing the old Env bindings plus the new entry.
         */
        @Override
        public final Env add(EnvEntry entry) {
            for (EnvEntry e : bindings) {
                if (entry.ident.equals(e.ident)) {
                    throw new DuplicateIdentError(entry.ident);
                }
            }
            EnvEntry[] newBindings = new EnvEntry[bindings.length + 1];
            System.arraycopy(bindings, 0, newBindings, 0, bindings.length);
            newBindings[bindings.length] = entry;
            return new ArrayEnv(parentEnv, newBindings);
        }

        @Override
        public final void collectIdents(Var var, Set<Ident> collector) {
            for (EnvEntry envEntry : bindings) {
                if (envEntry.var.equals(var)) {
                    collector.add(envEntry.ident);
                }
            }
            if (parentEnv != null) {
                parentEnv.collectIdents(var, collector);
            }
        }

        @Override
        public final Set<Ident> collectIdents(Var var) {
            Set<Ident> collector = new HashSet<>();
            collectIdents(var, collector);
            return collector;
        }

        @Override
        public final boolean contains(Ident ident) {
            for (EnvEntry envEntry : bindings) {
                if (envEntry.ident.equals(ident)) {
                    return true;
                }
            }
            return parentEnv != null && parentEnv.contains(ident);
        }

        @Override
        public final String formatValue() {
            StringBuilder sb = new StringBuilder();
            int scope = 0;
            Env currentEnv = this;
            while (currentEnv != null) {
                for (int i = 0; i < currentEnv.shallowSize(); i++) {
                    EnvEntry entry = currentEnv.getEnvEntry(i);
                    sb.append("env[");
                    sb.append(scope);
                    sb.append("]: ");
                    sb.append(entry.ident);
                    sb.append(" = ");
                    sb.append(entry.var);
                    if (i + 1 < currentEnv.shallowSize()) {
                        sb.append('\n');
                    }
                }
                scope++;
                currentEnv = currentEnv.parentEnv();
                if (currentEnv != null) {
                    sb.append('\n');
                }
            }
            return sb.toString();
        }

        @Override
        public final Var get(Ident ident) {
            for (EnvEntry envEntry : bindings) {
                if (envEntry.ident.equals(ident)) {
                    return envEntry.var;
                }
            }
            return parentEnv == null ? null : parentEnv.get(ident);
        }

        @Override
        public final EnvEntry getEnvEntry(int index) {
            return bindings[index];
        }

        @Override
        public final Iterator<EnvEntry> iterator() {
            return new ArrayEnvIterator();
        }

        @Override
        public final Env parentEnv() {
            return parentEnv;
        }

        @Override
        public final Env rootEnv() {
            return parentEnv == null ? this : parentEnv.rootEnv();
        }

        @Override
        public final Env setRootEnv(Env rootEnv) {
            if (parentEnv == null) {
                return new ArrayEnv(rootEnv, bindings);
            }
            return new ArrayEnv(parentEnv.setRootEnv(rootEnv), bindings);
        }

        @Override
        public final int shallowSize() {
            return bindings.length;
        }

        @Override
        public final String toString() {
            return formatValue();
        }

        private class ArrayEnvIterator implements Iterator<EnvEntry> {

            private int next;

            private ArrayEnvIterator() {
            }

            @Override
            public final boolean hasNext() {
                return next < bindings.length;
            }

            @Override
            public final EnvEntry next() {
                if (next >= bindings.length) {
                    throw new NoSuchElementException("Next element is not present");
                }
                return bindings[next++];
            }
        }

    }

}
