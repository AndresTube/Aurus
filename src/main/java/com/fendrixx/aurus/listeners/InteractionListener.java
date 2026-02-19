package com.fendrixx.aurus.listeners;
import com.fendrixx.aurus.Aurus;
import com.fendrixx.aurus.menu.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
public class InteractionListener implements Listener {
    private final Aurus plugin;
    public InteractionListener(Aurus plugin) {
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBukkitClick(PlayerInteractEvent event) {
        if (plugin.getMenuManager().getActiveMenu(event.getPlayer().getUniqueId()) != null) {
            event.setCancelled(true);
        }
    }
}