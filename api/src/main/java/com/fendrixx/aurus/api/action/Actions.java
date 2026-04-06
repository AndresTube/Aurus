package com.fendrixx.aurus.api.action;

import com.fendrixx.aurus.api.component.ActionType;
import com.fendrixx.aurus.api.menu.MenuRegistry;
import com.fendrixx.aurus.api.menu.PlayerMenuCache;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public final class Actions {

  private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

  public ActionType actionType() {
    return actionType;
  }

  private final ActionType actionType;
  private final ActionRunnable[] actions;

  public Actions(ActionType type, List<String> actions) {
    this.actionType = type;
    this.actions = new ActionRunnable[actions.size()];
    for (int i = 0; i < actions.size(); i++) {
      this.actions[i] = process(actions.get(i));
    }
  }

  public void action(Player player) {
    for (ActionRunnable action : actions) {
      if (action == null) continue;
      action.run(player);
    }
  }

  private ActionRunnable process(String action) {
    final int pos = action.indexOf(" ");
    final String command = action.substring(0, pos);
    final String arguments = action.substring(pos);
    if (command.equalsIgnoreCase("[close]")) {
      return (player) -> PlayerMenuCache.INSTANCE.menu(player.getUniqueId()).removeViewer(player);
    }

    if (command.startsWith("[console]")) {
      return (player) -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), arguments);
    } else if (command.startsWith("[player]")) {
      return (player) -> player.performCommand(arguments);
    } else if (command.startsWith("[broadcast]")) {
      return (player) -> Bukkit.broadcast(MINI_MESSAGE.deserialize(arguments));
    } else if (command.startsWith("[message]")) {
      return (player) -> player.sendMessage(MINI_MESSAGE.deserialize(arguments));
    } else if (command.startsWith("[openmenu]")) {
      String menuId = arguments.trim();
      return (player) -> {
        PlayerMenuCache.INSTANCE.menu(player.getUniqueId()).removeViewer(player);
        MenuRegistry.INSTANCE.get(menuId).render(player);
      };
    } else if (command.startsWith("[sound]")) {
      String[] parts = arguments.split(", ");
      return (player) -> {
        try {
          String key = parts[0].trim();
          if (!key.contains(":")) key = "minecraft:" + key;
          float vol = parts.length > 1 ? Float.parseFloat(parts[1].trim()) : 1.0f;
          float pitch = parts.length > 2 ? Float.parseFloat(parts[2].trim()) : 1.0f;
          player.playSound(player.getLocation(), key, vol, pitch);
        } catch (Exception ignored) {
        }
      };
    } else return null;
  }
}
