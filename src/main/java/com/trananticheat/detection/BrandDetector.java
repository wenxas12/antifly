package com.trananticheat.detection;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class BrandDetector {

    private final Map<UUID, String> brandCache = new ConcurrentHashMap<>();

    public BrandDetector() {
    }

    public String detect(Player p) {
        UUID uuid = p.getUniqueId();
        String cached = brandCache.get(uuid);
        if (cached != null) return cached;
        String brand = "vanilla";
        try {
            if (p.getClientBrandName() != null && !p.getClientBrandName().isEmpty()) {
                brand = p.getClientBrandName().toLowerCase();
            }
        } catch (NoSuchMethodError ignored) {
        }
        brandCache.put(uuid, brand);
        return brand;
    }

    public String getCached(UUID uuid) {
        return brandCache.getOrDefault(uuid, "unknown");
    }

    public void remove(UUID uuid) {
        brandCache.remove(uuid);
    }
}