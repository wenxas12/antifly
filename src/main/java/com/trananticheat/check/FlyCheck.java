package com.trananticheat.check;

import com.trananticheat.TranAntiCheat;
import com.trananticheat.config.PluginConfig;
import com.trananticheat.data.PlayerData;
import com.trananticheat.punish.PunishManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Location;

public final class FlyCheck {

    private final TranAntiCheat plugin;
    private final PluginConfig config;
    private final PunishManager punishManager;

    public FlyCheck(TranAntiCheat plugin, PluginConfig config, PunishManager punishManager) {
        this.plugin = plugin;
        this.config = config;
        this.punishManager = punishManager;
    }

    public void sample(Player p, PlayerData d, long now) {
        if (!config.flyEnabled()) {
            return;
        }
        if (isPermitted(p)) {
            d.airTicks(0);
            d.verticalAscendTicks(0);
            d.hoverTicks(0);
            d.hoverSince(0);
            d.lastSample(null);
            d.sampleTime(0);
            return;
        }

        Location prev = d.lastSample();
        Location cur = p.getLocation();
        if (prev == null || !prev.getWorld().equals(cur.getWorld())) {
            d.lastSample(cur);
            d.sampleTime(now);
            return;
        }

        double dx = cur.getX() - prev.getX();
        double dy = cur.getY() - prev.getY();
        double dz = cur.getZ() - prev.getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);

        if (isSwimming(p, cur) || isClimbing(p)) {
            d.airTicks(0);
            d.verticalAscendTicks(0);
            d.hoverTicks(0);
            d.hoverSince(0);
            d.lastSample(cur);
            d.sampleTime(now);
            return;
        }

        if (p.isOnGround()) {
            d.airTicks(0);
            d.verticalAscendTicks(0);
            d.hoverTicks(0);
            d.hoverSince(0);
            d.violations(decay(d.violations(), d.lastAlert(), now));
            d.lastSample(cur);
            d.sampleTime(now);
            return;
        }

        d.airTicks(d.airTicks() + 1);

        if (d.airTicks() >= config.flyMaxAirTicks()) {
            flag(p, d, "AirTime", "ticks=" + d.airTicks(), now);
            d.airTicks(0);
        }

        if (dy > config.flyMaxVerticalAscend()) {
            d.verticalAscendTicks(d.verticalAscendTicks() + 1);
            if (d.verticalAscendTicks() >= config.flyVerticalAscendTicks()) {
                flag(p, d, "Ascend", "dy=%.3f".formatted(dy), now);
                d.verticalAscendTicks(0);
            }
        } else {
            d.verticalAscendTicks(0);
        }

        if (Math.abs(dy) <= config.flyHoverMaxDy()) {
            d.hoverTicks(d.hoverTicks() + 1);
            if (d.hoverTicks() >= config.flyHoverTicks() && horizontal <= config.flyHoverMaxHorizontal()) {
                flag(p, d, "Hover", "hor=%.3f".formatted(horizontal), now);
                d.hoverTicks(0);
            }
        } else {
            d.hoverTicks(0);
        }

        d.lastSample(cur);
        d.sampleTime(now);
    }

    private void flag(Player p, PlayerData d, String type, String detail, long now) {
        d.violations(d.violations() + 1);
        punishManager.flag(p, "Fly", type, detail);
        d.lastAlert(now);
    }

    private double decay(double v, long lastAlert, long now) {
        if (v <= 0 || lastAlert <= 0) {
            return v;
        }
        double elapsed = (now - lastAlert) / 1000.0;
        double max = config.flyDecaySeconds();
        if (max <= 0) {
            return v;
        }
        double drop = Math.floor(elapsed / max);
        return Math.max(0, v - drop);
    }

    private boolean isPermitted(Player p) {
        if (p.isFlying() || p.getAllowFlight()) {
            return true;
        }
        if (p.isGliding()) {
            return true;
        }
        if (p.isInsideVehicle()) {
            return true;
        }
        if (p.getGameMode() == org.bukkit.GameMode.CREATIVE
                || p.getGameMode() == org.bukkit.GameMode.SPECTATOR) {
            return true;
        }
        if (hasLevitation(p)) {
            return true;
        }
        return false;
    }

    private boolean isSwimming(Player p, Location loc) {
        if (p.isSwimming()) {
            return true;
        }
        Material at = loc.getBlock().getType();
        return at == Material.WATER || at == Material.LAVA;
    }

    private boolean isClimbing(Player p) {
        return isOnLadder(p) || isOnClimbable(p.getLocation());
    }

    private boolean isOnLadder(Player p) {
        return p.getLocation().subtract(0, 0.1, 0).getBlock().getType() == Material.LADDER;
    }

    private boolean isOnClimbable(Location loc) {
        Material t = loc.getBlock().getType();
        return t == Material.LADDER || t == Material.VINE || t == Material.SCAFFOLDING;
    }

    private boolean hasLevitation(Player p) {
        return p.hasPotionEffect(PotionEffectType.LEVITATION);
    }
}