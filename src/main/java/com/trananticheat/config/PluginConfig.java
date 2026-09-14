package com.trananticheat.config;

import com.trananticheat.TranAntiCheat;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public final class PluginConfig {

    private final TranAntiCheat plugin;
    private final FileConfiguration yml;

    public PluginConfig(TranAntiCheat plugin) {
        this.plugin = plugin;
        this.yml = plugin.getConfig();
    }

    private String turkuaz(String s) {
        if (s == null) {
            return "";
        }
        return s.replaceAll("(?i)&([0-9a-fk-orx])", "\u00A7$1");
    }

    public String prefix() {
        return turkuaz(yml.getString("messages.prefix", "&8[&cTranAntiCheat&8]&r "));
    }

    public String noPermission() {
        return turkuaz(yml.getString("messages.no-permission", "&cYetkin yok."));
    }

    public String unknownPlayer() {
        return turkuaz(yml.getString("messages.unknown-player", "&cOyuncu cevrimici degil."));
    }

    public String reloaded() {
        return turkuaz(yml.getString("messages.reloaded", "&aConfig yenilendi."));
    }

    public String noAlerts() {
        return turkuaz(yml.getString("messages.no-alerts", "&7Alert kapali."));
    }

    public String joinedAlerts() {
        return turkuaz(yml.getString("messages.joined-alerts", "&aAlert acik."));
    }

    public String joinKickMessage() {
        return turkuaz(yml.getString("join-scan.kick-message", "&cLiteratura uymayan istemci."));
    }

    public String kickReason() {
        return turkuaz(yml.getString("punishments.kick-reason", "&cFLY tespit edildi."));
    }

    public String banReason() {
        return turkuaz(yml.getString("punishments.ban-reason", "&cFLY tespit edildi."));
    }

    // --- FLY ---
    public boolean flyEnabled() {
        return yml.getBoolean("checks.fly.enabled", true);
    }

    public double flyMaxVerticalAscend() {
        return yml.getDouble("checks.fly.max-vertical-ascend", 0.45);
    }

    public int flyVerticalAscendTicks() {
        return yml.getInt("checks.fly.vertical-ascend-ticks", 6);
    }

    public int flyHoverTicks() {
        return yml.getInt("checks.fly.hover-ticks", 24);
    }

    public double flyHoverMaxDy() {
        return yml.getDouble("checks.fly.hover-max-dy", 0.03);
    }

    public double flyHoverMaxHorizontal() {
        return yml.getDouble("checks.fly.hover-max-horizontal", 0.30);
    }

    public double flyDecaySeconds() {
        return yml.getDouble("checks.fly.decay-seconds", 90.0);
    }

    public int flyMaxAirTicks() {
        return yml.getInt("checks.fly.max-air-ticks", 120);
    }

    public String flyAlertMessage() {
        return yml.getString("checks.fly.alert-message", "&e%p% &7| &6FLY &f%type% &7(x%vl%)");
    }

    // --- NUFUZ AYARLARI ---
    public int alertAfter() {
        return yml.getInt("punishments.alert-after", 1);
    }

    public int kickAfter() {
        return yml.getInt("punishments.kick-after", 8);
    }

    public int banAfter() {
        return yml.getInt("punishments.ban-after", 16);
    }

    public long banDurationSeconds() {
        return yml.getLong("punishments.ban-duration-seconds", -1L);
    }

    public int autoBanOnJoinLog() {
        return yml.getInt("join-scan.auto-ban-on-join-log", 20);
    }

    public int logWindowHours() {
        return yml.getInt("join-scan.log-window-hours", 24);
    }

    public boolean joinScanEnabled() {
        return yml.getBoolean("join-scan.enabled", true);
    }

    public List<String> blockedClients() {
        return yml.getStringList("join-scan.blocked-clients");
    }

    public List<String> blockedModules() {
        return yml.getStringList("join-scan.blocked-modules");
    }
}
