package com.trananticheat.detection;

import com.trananticheat.config.PluginConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public final class ModuleDetector {

    private final Map<UUID, Set<String>> moduleCache = new ConcurrentHashMap<>();
    private final Set<String> blockedModules;

    public ModuleDetector(PluginConfig config) {
        this.blockedModules = new java.util.HashSet<>(config.blockedModules());
    }

    public boolean isBlocked(String module) {
        return blockedModules.contains(module.toLowerCase());
    }

    public void recordModules(UUID uuid, Set<String> modules) {
        moduleCache.put(uuid, modules);
    }

    public Set<String> getCached(UUID uuid) {
        return moduleCache.getOrDefault(uuid, Set.of());
    }

    public List<String> getBlockedFound(UUID uuid) {
        Set<String> cached = moduleCache.getOrDefault(uuid, Set.of());
        List<String> found = new ArrayList<>();
        for (String m : cached) {
            if (isBlocked(m)) {
                found.add(m);
            }
        }
        return found;
    }

    public void remove(UUID uuid) {
        moduleCache.remove(uuid);
    }
}