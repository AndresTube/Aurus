package com.fendrixx.aurus.commands;

import com.fendrixx.aurus.Aurus;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AurusCommand implements CommandExecutor, TabCompleter {
    private final Aurus plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final String prefix = "<dark_gray>[<gradient:dark_purple:yellow> Aurus </gradient><dark_gray>] ";

    public AurusCommand(Aurus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            String[] args) {
        if (!(sender instanceof Player player))
            return true;

        if (!player.hasPermission("aurus.admin")) {
            plugin.adventure().player(player).sendMessage(
                    mm.deserialize(prefix + "<dark_gray>[<red>✘<dark_gray>] <red>You do not have permission."));
            return true;
        }

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("open")) {
                if (args.length < 2) {
                    plugin.adventure().player(player).sendMessage(mm.deserialize(
                            prefix + "<dark_gray>[<yellow>?<dark_gray>] <gray>Usage: /aurus open <menu_id>"));
                    return true;
                }

                String menuId = args[1];
                if (plugin.getConfigHandler().getMenuSection(menuId) == null) {
                    plugin.adventure().player(player).sendMessage(mm.deserialize(prefix
                            + "<dark_gray>[<red>✘<dark_gray>] <red>Menu <yellow>" + menuId + "</yellow> not found."));
                    return true;
                }

                plugin.getMenuManager().openMenu(player, menuId);
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                plugin.getMenuManager().closeAll();
                plugin.getConfigHandler().reload();
                plugin.adventure().player(player).sendMessage(mm.deserialize(
                        prefix + "<dark_gray>[<green>✔<dark_gray>] <green>Configuration and menus reloaded."));
                return true;
            }

            if (args[0].equalsIgnoreCase("close")) {
                if (plugin.getMenuManager().getActiveMenu(player.getUniqueId()) != null) {
                    plugin.getMenuManager().closeMenu(player);
                    plugin.adventure().player(player).sendMessage(
                            mm.deserialize(prefix + "<dark_gray>[<green>✔<dark_gray>] <green>Menu closed."));
                } else {
                    plugin.adventure().player(player).sendMessage(mm.deserialize(
                            prefix + "<dark_gray>[<yellow>!<dark_gray>] <red>You don't have an active menu."));
                }
                return true;
            }
        }

        plugin.adventure().player(player).sendMessage(mm.deserialize(
                prefix + "<dark_gray>[<yellow>?<dark_gray>] <gray>Available commands: <white>open, close, reload"));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias,
            String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.addAll(List.of("open", "reload", "close"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("open")) {
            suggestions.addAll(plugin.getConfigHandler().getMenuKeys());
        }

        String lastArg = args[args.length - 1].toLowerCase();
        return suggestions.stream().filter(s -> s.toLowerCase().startsWith(lastArg)).toList();
    }
}