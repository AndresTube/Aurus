package com.fendrixx.aurus.debug;

import com.fendrixx.aurus.util.ColorUtils;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DebugManager {
    private final Set<UUID> enabled = new HashSet<>();
    private final String prefix = "<dark_gray>[<gradient:dark_purple:yellow> Aurus </gradient><dark_gray>] <aqua>[DEBUG] ";

    public boolean toggle(UUID uuid) {
        if (enabled.contains(uuid)) {
            enabled.remove(uuid);
            return false;
        }
        enabled.add(uuid);
        return true;
    }

    public boolean isEnabled(UUID uuid) {
        return enabled.contains(uuid);
    }

    public void log(String message) {
        Bukkit.getConsoleSender().sendMessage(ColorUtils.format(prefix + "<gray>" + message));
    }
}
