/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.util.SourceSpan;

import java.util.ArrayList;
import java.util.List;

import static org.torqlang.core.util.ListTools.nullSafeCopyOf;

public abstract class ActorLang extends AbstractLang implements SntcOrExpr {

    public final List<Pat> formalArgs;
    public final List<SntcOrExpr> body;

    private List<SntcOrExpr> initializer;
    private List<HandlerSntc> handlers;

    public ActorLang(List<Pat> formalArgs, List<SntcOrExpr> body, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.formalArgs = nullSafeCopyOf(formalArgs);
        this.body = nullSafeCopyOf(body);
    }

    public final List<HandlerSntc> handlers() {
        if (handlers != null) {
            return handlers;
        }
        handlers = new ArrayList<>(body.size());
        for (SntcOrExpr sox : body) {
            if (sox instanceof HandlerSntc handlerSntc) {
                handlers.add(handlerSntc);
            }
        }
        return handlers;
    }

    public final List<SntcOrExpr> initializer() {
        if (initializer != null) {
            return initializer;
        }
        initializer = new ArrayList<>(body.size());
        for (SntcOrExpr sox : body) {
            if (!(sox instanceof HandlerSntc)) {
                initializer.add(sox);
            }
        }
        return initializer;
    }

}
