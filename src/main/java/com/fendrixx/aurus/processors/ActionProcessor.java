package com.fendrixx.aurus.processors;
import com.fendrixx.aurus.Aurus;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import java.util.List;
public class ActionProcessor {
    private final Aurus plugin;
    public ActionProcessor(Aurus plugin) { this.plugin = plugin; }
    public void process(Player player, String action, Runnable closeCallback) {
        String cmd = action.replace("%player%", player.getName());
        if (cmd.equalsIgnoreCase("[close]")) {
            if (closeCallback != null) {
                closeCallback.run();
            }
            return;
        }
        if (cmd.startsWith("[console] ")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.substring(10));
        } else if (cmd.startsWith("[player] ")) {
            player.performCommand(cmd.substring(9));
        } else if (cmd.startsWith("[broadcast] ")) {
            plugin.adventure().all().sendMessage(MiniMessage.miniMessage().deserialize(cmd.substring(12)));
        } else if (cmd.startsWith("[message] ")) {
            plugin.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize(cmd.substring(10)));
        } else if (cmd.startsWith("[openmenu] ")) {
            String menuId = cmd.substring(11).trim();
            if (closeCallback != null) closeCallback.run();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) plugin.getMenuManager().openMenu(player, menuId);
            }, 2L);
        } else if (cmd.startsWith("[sound] ")) {
            String[] parts = cmd.substring(8).split(", ");
            try {
                Sound sound = Sound.valueOf(parts[0].toUpperCase());
                float vol = parts.length > 1 ? Float.parseFloat(parts[1]) : 1.0f;
                float pitch = parts.length > 2 ? Float.parseFloat(parts[2]) : 1.0f;
                player.playSound(player.getLocation(), sound, vol, pitch);
            } catch (Exception ignored) {}
        }
        // give me more ideas about more actions i can add, i run out of ideas lol
    }
    public void processList(Player player, List<String> actions, Runnable closeCallback) {
        if (actions == null) return;
        for (String action : actions) process(player, action, closeCallback);
    }
    public String parse(Player player, String text) {
        if (text == null) return "";
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }
}