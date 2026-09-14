package com.trananticheat.command;

import com.trananticheat.config.PluginConfig;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DiscordCommand implements CommandExecutor {

    private final PluginConfig config;

    public DiscordCommand(PluginConfig config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Bu komut sadece oyuncular icin.");
            return true;
        }

        String url = config.discordUrl();
        if (url == null || url.isEmpty()) {
            p.sendMessage(config.prefix() + ChatColor.RED + "Discord linki ayarlanmamis.");
            return true;
        }

        TextComponent tc = new TextComponent(config.discordMessage());
        tc.setColor(ChatColor.GREEN);
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatColor.YELLOW + "Tiklayarak Discord'a git")));
        p.spigot().sendMessage(tc);
        return true;
    }
}
