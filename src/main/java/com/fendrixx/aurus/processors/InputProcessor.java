package com.fendrixx.aurus.processors;

import com.fendrixx.aurus.Aurus;
import com.fendrixx.aurus.debug.DebugManager;
import com.fendrixx.aurus.util.ColorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InputProcessor implements Listener {
    private final Aurus plugin;
    private final Map<UUID, String> playersEditing = new ConcurrentHashMap<>();
    private final Map<String, String> savedValues = new ConcurrentHashMap<>();

    public InputProcessor(Aurus plugin) {
        this.plugin = plugin;
    }

    public void startInput(Player player, String variableName, String fallbackMessage) {
        playersEditing.put(player.getUniqueId(), variableName);
        if (fallbackMessage != null && !fallbackMessage.isEmpty()) {
            player.sendMessage(ColorUtils.format(fallbackMessage));
        } else {
            player.sendMessage(ColorUtils.format("<dark_gray>[<yellow>!<dark_gray>] <gray>Write in chat the input for "
                    + variableName + " <dark_gray>(or put 'cancel')<gray>."));
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!playersEditing.containsKey(uuid))
            return;

        event.setCancelled(true);
        String message = event.getMessage();
        String variableName = playersEditing.get(uuid);

        DebugManager debug = plugin.getDebugManager();
        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(ColorUtils.format("<dark_gray>[<red>✘<dark_gray>] <red>Canceled"));
            if (debug.isEnabled(uuid)) {
                debug.log(player.getName() + " cancelled input for variable=" + variableName);
            }
        } else {
            savedValues.put(variableName, message);
            player.sendMessage(
                    ColorUtils.format("<dark_gray>[<green>✔<dark_gray>] <gray>Input saved in " + variableName));
            if (debug.isEnabled(uuid)) {
                debug.log(player.getName() + " set variable=" + variableName + " value=" + message);
            }
        }

        playersEditing.remove(uuid);
    }

    public String getValue(String varName) {
        return savedValues.getOrDefault(varName, "...");
    }
}
