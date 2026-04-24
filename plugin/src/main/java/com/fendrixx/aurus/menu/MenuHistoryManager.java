package com.fendrixx.aurus.menu;

import org.bukkit.entity.Player;

import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MenuHistoryManager {
    private final Map<UUID, Deque<String>> history = new ConcurrentHashMap<>();

    public void push(Player player, String menuId) {
        if (menuId == null) return;
        history.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentLinkedDeque<>()).push(menuId);
    }

    public String pop(Player player) {
        Deque<String> stack = history.get(player.getUniqueId());
        if (stack != null && !stack.isEmpty()) {
            return stack.pop();
        }
        return null;
    }

    public void clear(Player player) {
        history.remove(player.getUniqueId());
    }
}
