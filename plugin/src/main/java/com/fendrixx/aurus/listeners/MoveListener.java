package com.fendrixx.aurus.listeners;

import com.fendrixx.aurus.Aurus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MoveListener implements Listener {
    private final Aurus plugin;
    private final Map<UUID, Vector> lastSafeLocations = new HashMap<>();
    private static final double MOVEMENT_THRESHOLD = 0.15;

    public MoveListener(Aurus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (plugin.getMenuManager().getActiveMenu(player.getUniqueId()) != null) {
            Vector safeLoc = lastSafeLocations.get(player.getUniqueId());
            if (safeLoc == null) {
                lastSafeLocations.put(player.getUniqueId(), event.getFrom().toVector());
                return;
            }

            if (event.getFrom().distance(event.getTo()) > MOVEMENT_THRESHOLD) {
                event.setTo(event.getFrom().setDirection(event.getTo().getDirection()));
            } else {
                lastSafeLocations.put(player.getUniqueId(), event.getFrom().toVector());
            }
        } else {
            lastSafeLocations.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        lastSafeLocations.remove(event.getPlayer().getUniqueId());
    }
}