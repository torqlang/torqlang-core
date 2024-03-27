/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

/*
 * The two composite types are objects and records. A composite feature will always resolve to the same bound value
 * allowing a feature to be selected once and reused many times.
 *
 * Stateful objects can be implemented using features that resolve to bound procedures. The feature set and bound
 * procedures are deterministic. Bound procedures can manipulate state hidden in their objects. For example, the
 * HashMap put method can be resolved and used again and again.
 *
 *     var put = number_map.put
 *     put(0, 'zero')
 *     put(1, 'one')
 *
 * Entailment and unification traverse composite records but not composite objects. This distinction allows values like
 * Str to behave as an object but participate as a value in records.
 */
public interface Composite extends Value {

    ValueOrVar select(Feature feature) throws WaitException;

}
