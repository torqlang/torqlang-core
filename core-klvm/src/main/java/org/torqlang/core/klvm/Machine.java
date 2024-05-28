/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

public final class Machine {

    private final Object owner;

    private Stack stack;
    private Stack current;
    private long computeCount;

    public Machine(Object owner, Stack stack) {
        this(owner, stack, 0);
    }

    public Machine(Object owner, Stack stack, long computeCount) {
        this.owner = owner;
        this.stack = stack;
        this.computeCount = computeCount;
    }

    public Machine(Stack stack) {
        this(null, stack, 0);
    }

    public Machine(Stack stack, long computeCount) {
        this(null, stack, computeCount);
    }

    public static void compute(Machine machine, long timeSlice) {
        ComputeAdvice advice = machine.compute(timeSlice);
        while (advice == ComputePreempt.SINGLETON) {
            advice = machine.compute(timeSlice);
        }
        if (advice.isWait()) {
            throw new IllegalStateException("Machine is waiting on a variable");
        }
        if (advice.isHalt()) {
            throw new MachineHaltError((ComputeHalt) advice);
        }
    }

    public static void compute(Stack stack, long timeSlice) {
        compute(new Machine(stack), timeSlice);
    }

    public final ComputeAdvice compute(long timeSlice) {
        if (stack == null) {
            return ComputeEnd.SINGLETON;
        }
        long computeAllowed = computeCount + timeSlice;
        while (computeCount < computeAllowed) {
            computeCount++;
            current = stack;
            stack = stack.next;
            try {
                current.stmt.compute(current.env, this);
            } catch (WaitException wx) {
                stack = current;
                current = null;
                return new ComputeWait(wx.barrier());
            } catch (NativeThrow nt) {
                ThrowStmt ts = new ThrowStmt(nt.error, nt, current.stmt);
                stack = new Stack(ts, current.env, current);
            } catch (MachineError error) {
                return error.asComputeHalt(current);
            } catch (Throwable throwable) {
                Complete ne = new NativeError(throwable);
                ThrowStmt ts = new ThrowStmt(ne, throwable, current.stmt);
                stack = new Stack(ts, current.env, current);
            }
            if (stack == null) {
                // INVARIANT: Even though we completed the computation, the field 'current' still holds the last
                // instruction.
                return ComputeEnd.SINGLETON;
            }
        }
        return ComputePreempt.SINGLETON;
    }

    public final long computeCount() {
        return computeCount;
    }

    public final Stack current() {
        return current;
    }

    @SuppressWarnings("unchecked")
    public final <T> T owner() {
        return (T) owner;
    }

    public final void pushStackEntries(StmtList stmtList, Env env) {
        for (StmtList.Entry current = stmtList.lastEntry(); current != null; current = current.prev()) {
            stack = new Stack(current.stmt(), env, stack);
        }
    }

    public final void pushStackEntry(Stmt stmt, Env env) {
        stack = new Stack(stmt, env, stack);
    }

    public final Stack stack() {
        return stack;
    }

    final void unwindToJumpCatchStmt(JumpThrowStmt jumpThrowStmt) {
        int jumpThrowId = jumpThrowStmt.id;
        while (stack != null) {
            if (stack.stmt instanceof JumpCatchStmt jumpCatchStmt && jumpCatchStmt.id == jumpThrowId) {
                break;
            }
            stack = stack.next;
        }
        if (stack == null) {
            // If this condition occurs, we generated an invalid program containing unmatched jump-throw/jump-catch
            // statements. The field 'current' will hold the instruction that issued the unmatched jump-throw.
            throw new UnmatchedJumpThrowError(jumpThrowStmt);
        }
    }

    final void unwindToNextCatchStmt(Complete error, Throwable nativeCause) {
        while (stack != null) {
            if (stack.stmt instanceof CatchStmt catchStmt) {
                Env catchEnv = Env.createPrivatelyForKlvm(stack.env,
                    new EnvEntry[]{new EnvEntry(catchStmt.arg, new Var(error))});
                stack = new Stack(catchStmt.caseStmt, catchEnv, stack.next);
                break;
            }
            stack = stack.next;
        }
        if (stack == null) {
            // INVARIANT: Even though we have unwound the stack, the field 'current' still holds the
            // instruction that threw the error.
            throw new UncaughtThrowError(error, nativeCause);
        }
    }

}
