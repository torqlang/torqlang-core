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
    private List<AskSntc> askHandlers;
    private List<TellSntc> tellHandlers;

    public ActorLang(List<Pat> formalArgs, List<SntcOrExpr> body, SourceSpan sourceSpan) {
        super(sourceSpan);
        this.formalArgs = nullSafeCopyOf(formalArgs);
        this.body = nullSafeCopyOf(body);
    }

    public final List<? extends AskSntc> askHandlers() {
        lazyLoad();
        return askHandlers;
    }

    public final List<? extends SntcOrExpr> initializer() {
        lazyLoad();
        return initializer;
    }

    private void lazyLoad() {
        if (initializer != null) {
            return;
        }
        initializer = new ArrayList<>(body.size());
        askHandlers = new ArrayList<>(body.size());
        tellHandlers = new ArrayList<>(body.size());
        for (SntcOrExpr sox : body) {
            if (sox instanceof AskSntc askHandler) {
                askHandlers.add(askHandler);
            } else if (sox instanceof TellSntc tellHandler) {
                tellHandlers.add(tellHandler);
            } else {
                initializer.add(sox);
            }
        }
    }

    public final List<TellSntc> tellHandlers() {
        lazyLoad();
        return tellHandlers;
    }

}
