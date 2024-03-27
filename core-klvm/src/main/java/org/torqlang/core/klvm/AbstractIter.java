/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import java.util.Collection;
import java.util.List;

public abstract class AbstractIter implements Proc {

    private static final int EXPECTED_ARG_COUNT = 1;

    private final List<ValueOrVar> values;
    private int nextIndex;

    public AbstractIter(Collection<? extends ValueOrVar> values) {
        this.values = List.copyOf(values);
        this.nextIndex = 0;
    }

    @Override
    public void apply(List<CompleteOrIdent> ys, Env env, Machine machine) throws WaitException {
        if (ys.size() != EXPECTED_ARG_COUNT) {
            throw new InvalidArgCountError(EXPECTED_ARG_COUNT, ys, this);
        }
        ValueOrVar next;
        int size = values.size();
        if (nextIndex < size) {
            next = values.get(nextIndex);
            nextIndex++;
        } else {
            next = Eof.SINGLETON;
        }
        ValueOrVar target = ys.get(0).resolveValueOrVar(env);
        target.bindToValueOrVar(next, null);
    }

}
