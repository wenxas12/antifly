package com.trananticheat.command;

import com.trananticheat.TranAntiCheat;
import com.trananticheat.config.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class TranAntiCheatCommand implements CommandExecutor, TabCompleter {

    private final TranAntiCheat plugin;
    private final List<String> TAB = List.of("reload", "alerts", "help");

    public TranAntiCheatCommand(TranAntiCheat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        PluginConfig cfg = plugin.pluginConfig();
        if (args.length == 0) {
            send(sender, "&8[&cTranAntiCheat&8] &7v" + plugin.getDescription().getVersion());
            send(sender, "&e/trananticheat reload &7- Configi yenile");
            send(sender, "&e/trananticheat alerts &7- Alert durumunu degistir");
            send(sender, "&e/trananticheat help &7- Bu mesaj");
            return true;
        }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "reload":
                if (!sender.hasPermission("trananticheat.admin")) {
                    send(sender, cfg.noPermission());
                    return true;
                }
                plugin.reloadCore();
                send(sender, cfg.reloaded());
                break;
            case "alerts":
                if (!(sender instanceof Player)) {
                    send(sender, "&cKonsoldan calistirilamaz.");
                    return true;
                }
                Player p = (Player) sender;
                if (!p.hasPermission("trananticheat.alert")) {
                    send(sender, cfg.noPermission());
                    return true;
                }
                boolean currentlyOn = p.hasMetadata("tac_alerts")
                        && p.getMetadata("tac_alerts").size() > 0
                        && p.getMetadata("tac_alerts").get(0).asBoolean();
                if (currentlyOn) {
                    p.removeMetadata("tac_alerts", plugin);
                    send(sender, cfg.noAlerts());
                } else {
                    p.setMetadata("tac_alerts", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
                    send(sender, cfg.joinedAlerts());
                }
                break;
            case "help":
                send(sender, "&8[&cTranAntiCheat&8] &7Fly-only anti-cheat");
                send(sender, "&e/ta reload &7- Reload config");
                send(sender, "&e/ta alerts &7- Toggle alerts");
                send(sender, "&7Yazar: wenxas");
                break;
            default:
                send(sender, "&cBilinmeyen komut: &f" + sub);
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            List<String> result = new ArrayList<>();
            for (String s : TAB) {
                if (s.startsWith(partial)) result.add(s);
            }
            return result;
        }
        return List.of();
    }

    private void send(CommandSender sender, String msg) {
        sender.sendMessage(plugin.pluginConfig().prefix() + msg);
    }
}