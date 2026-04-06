package com.fendrixx.aurus.debug;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DebugManager {
    public static final DebugManager INSTANCE = new DebugManager();
    private final Set<UUID> enabled = new HashSet<>();
    private static final ComponentBuilder prefix = MiniMessage.miniMessage().deserialize("<dark_gray>[<gradient:dark_purple:yellow> Aurus </gradient><dark_gray>] <aqua>[DEBUG] ").toBuilder();

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
        Bukkit.getConsoleSender().sendMessage(prefix.append(Component.text(message).style(Style.style().color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE))).asComponent());
    }
}
