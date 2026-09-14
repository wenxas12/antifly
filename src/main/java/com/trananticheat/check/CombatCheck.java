package com.trananticheat.check;

import com.trananticheat.TranAntiCheat;
import com.trananticheat.config.PluginConfig;
import com.trananticheat.data.PlayerData;
import com.trananticheat.punish.PunishManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

public final class CombatCheck implements Listener {

    private final TranAntiCheat plugin;
    private final PluginConfig config;
    private final PunishManager punishManager;

    public CombatCheck(TranAntiCheat plugin, PluginConfig config, PunishManager punishManager) {
        this.plugin = plugin;
        this.config = config;
        this.punishManager = punishManager;
    }

    public void sample(Player p, PlayerData d) {
        if (!config.aimEnabled() || p.hasPermission("trananticheat.bypass")) {
            return;
        }
        float yaw = p.getLocation().getYaw();
        float prev = d.lastYaw();
        if (!Float.isNaN(prev)) {
            float diff = Math.abs(yaw - prev);
            if (diff > 180f) {
                diff = 360f - diff;
            }
            if (diff > config.aimMaxYawPerTick()) {
                d.yawExcessTicks(d.yawExcessTicks() + 1);
                if (d.yawExcessTicks() >= config.aimRequiredTicks()) {
                    punishManager.flag(p, "Aim", "Rotation", "yaw_delta=" + Math.round(diff));
                    d.yawExcessTicks(0);
                }
            } else {
                d.yawExcessTicks(0);
            }
        }
        d.lastYaw(yaw);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player p)) {
            return;
        }
        if (p.hasPermission("trananticheat.bypass")) {
            return;
        }
        long now = System.currentTimeMillis();
        UUID target = event.getEntity().getUniqueId();

        if (config.killAuraEnabled()) {
            PlayerData d = playerData(p);
            long since = now - d.lastAttackTime();

            // hedef degistirme: iki farkli hedefe cok kisa arayla saldiri
            if (d.lastAttackTarget() != null && !d.lastAttackTarget().equals(target)) {
                if (since > 0 && since < config.killAuraMultiAuraMs()) {
                    punishManager.flag(p, "KillAura", "MultiAura", "dt=" + since);
                }
            }

            // hedefe bakmadan vurma (acik acisi)
            Location eye = p.getEyeLocation();
            Vector toTarget = event.getEntity().getLocation().add(0, 1, 0)
                    .toVector().subtract(eye.toVector()).normalize();
            double angle = Math.toDegrees(Math.acos(clampDot(eye.getDirection().dot(toTarget))));
            if (angle > config.killAuraMaxAngle()) {
                punishManager.flag(p, "KillAura", "Angle", "angle=" + Math.round(angle));
            }

            // cok uzak saldiri (reach)
            double dist = eye.distance(event.getEntity().getLocation().add(0, 1, 0));
            if (dist > config.killAuraMaxReach()) {
                punishManager.flag(p, "KillAura", "Reach", "dist=" + String.format("%.2f", dist));
            }

            d.lastAttackTime(now);
            d.lastAttackTarget(target);
        }

        if (config.triggerBotEnabled()) {
            PlayerData d = playerData(p);
            long since = now - d.lastAttackTime();
            if (since < config.triggerBotMaxDelayMs()) {
                d.fastAttackCount(d.fastAttackCount() + 1);
                if (d.fastAttackCount() >= config.triggerBotRequiredHits()) {
                    punishManager.flag(p, "TriggerBot", "FastAttack", "hits=" + d.fastAttackCount());
                    d.fastAttackCount(0);
                }
            } else {
                d.fastAttackCount(0);
            }
            d.lastAttackTime(now);
        }
    }

    private PlayerData playerData(Player p) {
        return plugin.data(p.getUniqueId());
    }

    private static double clampDot(double dot) {
        return Math.max(-1.0, Math.min(1.0, dot));
    }
}