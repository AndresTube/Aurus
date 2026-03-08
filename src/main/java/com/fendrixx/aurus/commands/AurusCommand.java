package com.fendrixx.aurus.commands;

import com.fendrixx.aurus.Aurus;
import com.fendrixx.aurus.util.ColorUtils;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

@Command("aurus")
public class AurusCommand {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final String PREFIX = "<dark_gray>[<gradient:dark_purple:yellow> Aurus </gradient><dark_gray>] ";

    private final Aurus plugin;

    public AurusCommand(Aurus plugin) {
        this.plugin = plugin;
    }

    @Command("aurus")
    public void defaultCommand(BukkitCommandActor actor) {
        actor.reply(MM.deserialize(PREFIX + "<dark_gray>[<yellow>?<dark_gray>] <gray>Available commands: <white>open, close, reload, debug"));
    }

    @Subcommand("open")
    @CommandPermission("aurus.admin")
    public void openMenu(BukkitCommandActor actor, String menuId, @Optional Player target) {
        if (!actor.isPlayer()) return;
        Player sender = actor.asPlayer();

        if (plugin.getConfigHandler().getMenuSection(menuId) == null) {
            actor.reply(MM.deserialize(PREFIX + "<dark_gray>[<red>✘<dark_gray>] <red>Menu <yellow>" + menuId + "</yellow> not found."));
            return;
        }

        Player targetPlayer = (target != null) ? target : sender;
        plugin.getMenuManager().openMenu(targetPlayer, menuId);

        if (!targetPlayer.equals(sender)) {
            actor.reply(MM.deserialize(PREFIX + "<dark_gray>[<green>✔<dark_gray>] <green>Opened <yellow>" + menuId + "</yellow> for <yellow>" + targetPlayer.getName() + "</yellow>."));
        }
    }

    @Subcommand("reload")
    @CommandPermission("aurus.admin")
    public void reload(BukkitCommandActor actor) {
        plugin.getMenuManager().closeAll();
        plugin.getConfigHandler().reload();
        actor.reply(MM.deserialize(PREFIX + "<dark_gray>[<green>✔<dark_gray>] <green>Configuration and menus reloaded."));
    }

    @Subcommand("close")
    @CommandPermission("aurus.admin")
    public void closeMenu(BukkitCommandActor actor, @Optional Player target) {
        if (!actor.isPlayer()) return;
        Player sender = actor.asPlayer();
        Player targetPlayer = (target != null) ? target : sender;

        if (plugin.getMenuManager().getActiveMenu(targetPlayer.getUniqueId()) != null) {
            plugin.getMenuManager().closeMenu(targetPlayer);
            actor.reply(MM.deserialize(PREFIX + "<dark_gray>[<green>✔<dark_gray>] <green>Menu closed for <yellow>" + targetPlayer.getName() + "</yellow>."));
        } else {
            actor.reply(MM.deserialize(PREFIX + "<dark_gray>[<yellow>!<dark_gray>] <red>No active menu found."));
        }
    }

    @Subcommand("debug")
    @CommandPermission("aurus.admin")
    public void toggleDebug(BukkitCommandActor actor) {
        if (!actor.isPlayer()) {
            actor.reply(ColorUtils.format(PREFIX + "This command only can be used by players"));
            return;
        }

        Player player = actor.asPlayer();
        boolean nowEnabled = plugin.getDebugManager().toggle(player.getUniqueId());

        if (nowEnabled) {
            actor.reply(MM.deserialize(PREFIX + "<dark_gray>[<aqua>⚙<dark_gray>] <green>Debug enabled"));
        } else {
            actor.reply(MM.deserialize(PREFIX + "<dark_gray>[<aqua>⚙<dark_gray>] <red>Debug disabled"));
        }
    }
}