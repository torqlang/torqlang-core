/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class VarSet implements ValueOrVarSet, Iterable<Var> {

    public static final VarSet EMPTY_VAR_SET = new VarSet(new Var[0], 0);

    private final Var[] vars;
    private final int size;

    private VarSet(Var[] vars, int size) {
        this.vars = vars;
        this.size = size;
    }

    private static boolean contains(Var[] vars, int size, Var var) {
        for (int i = 0; i < size; i++) {
            if (vars[i] == var) {
                return true;
            }
        }
        return false;
    }

    static VarSet createPrivatelyForKlvm(Var[] vars, int size) {
        return new VarSet(vars, size);
    }

    public static VarSet union(VarSet es1, VarSet es2) {
        if (es1 == es2) {
            return es1;
        }
        Var[] vs = new Var[es1.size() + es2.size()];
        int size = 0;
        for (Var v : es1) {
            if (!contains(vs, size, v)) {
                vs[size] = v;
                size++;
            }
        }
        for (Var v : es2) {
            if (!contains(vs, size, v)) {
                vs[size] = v;
                size++;
            }
        }
        return new VarSet(vs, size);
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state) throws Exception {
        return visitor.visitVarSet(this, state);
    }

    public VarSet add(Var var) {
        if (contains(vars, size, var)) {
            return this;
        }
        int newSize = size + 1;
        Var[] newVars = new Var[newSize];
        System.arraycopy(vars, 0, newVars, 0, size);
        newVars[size] = var;
        return new VarSet(newVars, newSize);
    }

    public final boolean contains(Var var) {
        return contains(vars, size, var);
    }

    public final String formatValue() {
        StringBuilder sb = new StringBuilder();
        if (this == VarSet.EMPTY_VAR_SET) {
            sb.append("<<$var_set>>");
        } else {
            sb.append("<<$var_set ");
            Iterator<Var> iter = iterator();
            while (iter.hasNext()) {
                Var var = iter.next();
                sb.append(var.toString());
                if (iter.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append(">>");
        }
        return sb.toString();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public final Iterator<Var> iterator() {
        return new VarSetIterator();
    }

    public final int size() {
        return size;
    }

    @Override
    public final String toString() {
        return formatValue();
    }

    private final class VarSetIterator implements Iterator<Var> {
        private int index = 0;

        @Override
        public final boolean hasNext() {
            return index < size;
        }

        @Override
        public final Var next() {
            if (index < size) {
                return vars[index++];
            }
            throw new NoSuchElementException("Next element is not present");
        }
    }

}
