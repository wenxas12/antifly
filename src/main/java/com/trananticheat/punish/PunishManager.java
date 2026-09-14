package com.trananticheat.punish;

import com.trananticheat.TranAntiCheat;
import com.trananticheat.config.PluginConfig;
import com.trananticheat.detection.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PunishManager {

    private final TranAntiCheat plugin;
    private final PluginConfig config;
    private final LogManager logManager;
    private final Map<UUID, ViolationCounter> counters = new ConcurrentHashMap<>();

    public PunishManager(TranAntiCheat plugin, PluginConfig config, LogManager logManager) {
        this.plugin = plugin;
        this.config = config;
        this.logManager = logManager;
    }

    public void flag(Player p, String check, String type, String detail) {
        ViolationCounter vc = counters.computeIfAbsent(p.getUniqueId(), k -> new ViolationCounter());
        vc.violations++;
        vc.lastViolation = System.currentTimeMillis();
        int vl = vc.violations;
        String msg = config.flyAlertMessage()
                .replace("%player%", p.getName())
                .replace("%check%", check)
                .replace("%type%", type)
                .replace("%detail%", detail)
                .replace("%vl%", String.valueOf(vl));
        logManager.log("FLAG", p.getName() + "|" + check + "/" + type + "|vl=" + vl + "|" + detail);
        if (vl >= config.alertAfter()) {
            broadcast(msg);
        }
        if (vl >= config.banAfter()) {
            long dur = config.banDurationSeconds();
            Date expires = dur > 0 ? new Date(System.currentTimeMillis() + dur * 1000L) : null;
            String reason = config.banReason();
            Bukkit.getScheduler().runTask(plugin, () -> {
                p.banPlayer(reason, expires, "TranAntiCheat");
            });
            logManager.log("BAN", p.getName() + "|dur=" + dur + "s");
        } else if (vl >= config.kickAfter()) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                p.kickPlayer(config.kickReason());
            });
            logManager.log("KICK", p.getName());
        }
    }

    public int getViolations(UUID uuid) {
        ViolationCounter vc = counters.get(uuid);
        return vc == null ? 0 : vc.violations;
    }

    public void remove(UUID uuid) {
        counters.remove(uuid);
    }

    public void decayAll() {
        long now = System.currentTimeMillis();
        double decaySec = config.flyDecaySeconds();
        if (decaySec <= 0) return;
        for (Map.Entry<UUID, ViolationCounter> e : counters.entrySet()) {
            long elapsed = (now - e.getValue().lastViolation) / 1000;
            int drop = (int) (elapsed / decaySec);
            if (drop > 0) {
                e.getValue().violations = Math.max(0, e.getValue().violations - drop);
            }
        }
    }

    private void broadcast(String msg) {
        for (Player op : Bukkit.getOnlinePlayers()) {
            if (op.hasPermission("trananticheat.alert")) {
                op.sendMessage(config.prefix() + msg);
            }
        }
    }

    private static final class ViolationCounter {
        int violations;
        long lastViolation;
    }
}