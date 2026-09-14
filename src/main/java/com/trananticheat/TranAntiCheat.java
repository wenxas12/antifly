package com.trananticheat;

import com.trananticheat.check.CombatCheck;
import com.trananticheat.check.FlyCheck;
import com.trananticheat.command.TranAntiCheatCommand;
import com.trananticheat.config.PluginConfig;
import com.trananticheat.data.PlayerData;
import com.trananticheat.detection.BrandDetector;
import com.trananticheat.detection.LogManager;
import com.trananticheat.detection.ModuleDetector;
import com.trananticheat.listener.PlayerListener;
import com.trananticheat.punish.PunishManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TranAntiCheat extends JavaPlugin {

    private PluginConfig pluginConfig;
    private LogManager logManager;
    private PunishManager punishManager;
    private BrandDetector brandDetector;
    private ModuleDetector moduleDetector;
    private FlyCheck flyCheck;
    private CombatCheck combatCheck;
    private PlayerListener playerListener;

    private final Map<UUID, PlayerData> dataMap = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        reloadCore();

        TranAntiCheatCommand command = new TranAntiCheatCommand(this);
        if (getCommand("trananticheat") != null) {
            getCommand("trananticheat").setExecutor(command);
            getCommand("trananticheat").setTabCompleter(command);
        }

        getServer().getPluginManager().registerEvents(playerListener, this);
        getServer().getPluginManager().registerEvents(combatCheck, this);

        getServer().getScheduler().runTaskTimer(this, () -> {
            long now = System.currentTimeMillis();
            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerData d = dataMap.computeIfAbsent(p.getUniqueId(), k -> new PlayerData(k));
                d.player(p);
                flyCheck.sample(p, d, now);
                combatCheck.sample(p, d);
            }
        }, 1L, 1L);

        getLogger().info("TranAntiCheat v" + getDescription().getVersion()
                + " aktif | FLY + KillAura + Aim + TriggerBot tespitleri acik");
    }

    public void reloadCore() {
        reloadConfig();
        saveDefaultConfig();

        this.pluginConfig = new PluginConfig(this);
        this.logManager = new LogManager(this);
        this.punishManager = new PunishManager(this, pluginConfig, logManager);
        this.brandDetector = new BrandDetector();
        this.moduleDetector = new ModuleDetector(pluginConfig);
        this.flyCheck = new FlyCheck(this, pluginConfig, punishManager);
        this.combatCheck = new CombatCheck(this, pluginConfig, punishManager);
        this.playerListener = new PlayerListener(this, dataMap, brandDetector, moduleDetector, punishManager);
    }

    @Override
    public void onDisable() {
        if (logManager != null) {
            logManager.flushAll();
        }
        getLogger().info("TranAntiCheat kapatildi.");
    }

    public PluginConfig pluginConfig() {
        return pluginConfig;
    }

    public LogManager logManager() {
        return logManager;
    }

    public PunishManager punishManager() {
        return punishManager;
    }

    public BrandDetector brandDetector() {
        return brandDetector;
    }

    public ModuleDetector moduleDetector() {
        return moduleDetector;
    }

    public PlayerData data(UUID uuid) {
        return dataMap.computeIfAbsent(uuid, k -> new PlayerData(k));
    }

    public void removeData(UUID uuid) {
        dataMap.remove(uuid);
    }
}
