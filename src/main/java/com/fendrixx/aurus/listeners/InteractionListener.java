package com.fendrixx.aurus.listeners;

import com.fendrixx.aurus.Aurus;
import com.fendrixx.aurus.menu.Menu;
import com.fendrixx.aurus.menu.MenuButton;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class InteractionListener implements Listener {
    private final Aurus plugin;

    public InteractionListener(Aurus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Menu menu = plugin.getMenuManager().getActiveMenu(player.getUniqueId());

        if (menu == null) return;

        event.setCancelled(true);

        if (event.getAction() == Action.PHYSICAL) return;

        processMenuClick(player, menu);
    }

    public void handle3DClick(Player player) {
        Menu menu = plugin.getMenuManager().getActiveMenu(player.getUniqueId());
        if (menu == null) return;
        processMenuClick(player, menu);
    }

    private void processMenuClick(Player player, Menu menu) {
        if (menu.getCursor() == null) return;

        for (MenuButton btn : menu.getButtons()) {
            if (btn.getDisplay() == null) continue;

            double dist = menu.getCursor().getLocation().distance(btn.getDisplay().getLocation());

            double clickRadius = 0.5 * btn.getConfig().getDouble("size", 1.0);

            if (dist < clickRadius) {
                if ("INPUT".equalsIgnoreCase(btn.getType())) {
                    String variable = btn.getVariableName();
                    plugin.getInputProcessor().startInput(player, variable);
                }

                btn.onClick();

                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.6f, 1.2f);

                break;
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getMenuManager().closeMenu(event.getPlayer());
    }
}
