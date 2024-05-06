/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.klvm.CompleteRec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public final class ActorSystemBuilder {

    private static final Map<String, CompleteRec> DEFAULT_MODULES_MAP;

    static {
        DEFAULT_MODULES_MAP = new HashMap<>();
        DEFAULT_MODULES_MAP.put("system", SystemMod.moduleRec);
        DEFAULT_MODULES_MAP.put("system.Procs", SystemProcsMod.moduleRec);
    }

    private final Map<String, CompleteRec> modulesMap = new HashMap<>();
    private String name;
    private Executor executor;

    public final ActorSystemBuilder addDefaultModules() {
        modulesMap.putAll(DEFAULT_MODULES_MAP);
        return this;
    }

    public ActorSystemBuilder addModule(String path, CompleteRec module) {
        if (modulesMap.containsKey(path)) {
            throw new IllegalArgumentException("Module already exists:" + path);
        }
        modulesMap.put(path, module);
        return this;
    }

    public final ActorSystem build() {
        Map<String, CompleteRec> effectiveModulesMap = modulesMap.isEmpty() ? DEFAULT_MODULES_MAP : modulesMap;
        List<ModuleEntry> modules = new ArrayList<>(effectiveModulesMap.size());
        for (Map.Entry<String, CompleteRec> entry : effectiveModulesMap.entrySet()) {
            modules.add(new ModuleEntry(entry.getKey(), entry.getValue()));
        }
        return new BasicActorSystem(name, executor, modules);
    }

    public final Executor executor() {
        return executor;
    }

    public final Map<String, CompleteRec> modules() {
        return Map.copyOf(modulesMap);
    }

    public final String name() {
        return name;
    }

    public final ActorSystemBuilder setExecutor(Executor executor) {
        this.executor = executor;
        return this;
    }

    public final ActorSystemBuilder setName(String name) {
        this.name = name;
        return this;
    }

}
