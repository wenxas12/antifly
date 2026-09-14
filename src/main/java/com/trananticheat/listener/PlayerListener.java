package com.trananticheat.listener;

import com.trananticheat.TranAntiCheat;
import com.trananticheat.data.PlayerData;
import com.trananticheat.detection.BrandDetector;
import com.trananticheat.detection.ModuleDetector;
import com.trananticheat.punish.PunishManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class PlayerListener implements Listener {

    private final TranAntiCheat plugin;
    private final Map<UUID, PlayerData> dataMap;
    private final BrandDetector brandDetector;
    private final ModuleDetector moduleDetector;
    private final PunishManager punishManager;

    public PlayerListener(TranAntiCheat plugin, Map<UUID, PlayerData> dataMap,
                          BrandDetector brandDetector, ModuleDetector moduleDetector,
                          PunishManager punishManager) {
        this.plugin = plugin;
        this.dataMap = dataMap;
        this.brandDetector = brandDetector;
        this.moduleDetector = moduleDetector;
        this.punishManager = punishManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        UUID uuid = p.getUniqueId();
        PlayerData d = plugin.data(uuid);
        d.player(p);
        if (!plugin.pluginConfig().joinScanEnabled() || d.joinScanned()) return;
        d.joinScanned(true);

        String brand = brandDetector.detect(p);
        Set<String> modules = Set.of();
        moduleDetector.recordModules(uuid, modules);

        List<String> blockedModules = moduleDetector.getBlockedFound(uuid);
        List<String> blockedClients = plugin.pluginConfig().blockedClients();
        boolean badClient = false;
        for (String bc : blockedClients) {
            if (brand.contains(bc.toLowerCase())) {
                badClient = true;
                break;
            }
        }
        if (badClient) {
            int threshold = plugin.pluginConfig().autoBanOnJoinLog();
            int pastFlags = plugin.logManager().countFlags(uuid, plugin.pluginConfig().logWindowHours());
            if (pastFlags >= threshold) {
                p.kickPlayer(plugin.pluginConfig().joinKickMessage());
                plugin.logManager().log("JOIN-BAN", p.getName() + "|pastFlags=" + pastFlags);
            } else {
                p.kickPlayer(plugin.pluginConfig().joinKickMessage());
                plugin.logManager().log("JOIN-KICK", p.getName() + "|brand=" + brand);
            }
            return;
        }

        if (!blockedModules.isEmpty()) {
            plugin.logManager().log("MODULES", p.getName() + "|blocked=" + blockedModules);
        }

        plugin.logManager().recordJoin(uuid, brand, new ArrayList<>(blockedModules));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        plugin.removeData(uuid);
        brandDetector.remove(uuid);
        moduleDetector.remove(uuid);
        punishManager.remove(uuid);
    }
}