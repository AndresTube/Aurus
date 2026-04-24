package com.fendrixx.aurus.processors;

import com.fendrixx.aurus.Aurus;
import com.fendrixx.aurus.debug.DebugManager;
import com.fendrixx.aurus.util.ColorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InputProcessor implements Listener {
    private final Aurus plugin;
    private final Map<UUID, InputSession> playersEditing = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, String>> savedValues = new ConcurrentHashMap<>();

    public InputProcessor(Aurus plugin) {
        this.plugin = plugin;
    }

    public void startSession(Player player, InputSession session, String fallbackMessage) {
        playersEditing.put(player.getUniqueId(), session);
        if (fallbackMessage != null && !fallbackMessage.isEmpty()) {
            player.sendMessage(ColorUtils.format(fallbackMessage));
        } else {
            player.sendMessage(ColorUtils.format("<dark_gray>[<yellow>!<dark_gray>] <gray>Write in chat the input for "
                    + session.getVariableName() + " <dark_gray>(or put 'cancel')<gray>."));
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!playersEditing.containsKey(uuid))
            return;

        event.setCancelled(true);
        String message = event.getMessage();
        InputSession session = playersEditing.get(uuid);
        String variableName = session.getVariableName();

        DebugManager debug = plugin.getDebugManager();
        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(ColorUtils.format("<dark_gray>[<red>✘<dark_gray>] <red>Canceled"));
            if (debug.isEnabled(uuid)) {
                debug.log(player.getName() + " cancelled input for variable=" + variableName);
            }
            playersEditing.remove(uuid);
        } else {
            if (!session.validate(message)) {
                player.sendMessage(ColorUtils.format(session.getErrorMessage()));
                return; // Re-prompting without removing session
            }

            savedValues.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(variableName, message);
            player.sendMessage(
                    ColorUtils.format("<dark_gray>[<green>✔<dark_gray>] <gray>Input saved in " + variableName));
            if (debug.isEnabled(uuid)) {
                debug.log(player.getName() + " set variable=" + variableName + " value=" + message);
            }
            playersEditing.remove(uuid);
        }
    }

    public void setValue(UUID uuid, String varName, String value) {
        savedValues.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(varName, value);
    }

    public String getValue(UUID uuid, String varName) {
        Map<String, String> playerVals = savedValues.get(uuid);
        return playerVals != null ? playerVals.getOrDefault(varName, "...") : "...";
    }
}
