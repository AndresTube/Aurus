package com.fendrixx.aurus.listeners;
import com.fendrixx.aurus.Aurus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
public class MoveListener implements Listener {
    private final Aurus plugin;
    public MoveListener(Aurus plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getSpectatorTarget() != null) {
            player.setSpectatorTarget(null);
        }
    }
}