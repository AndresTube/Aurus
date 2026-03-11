package com.fendrixx.aurus.listeners;

import com.fendrixx.aurus.Aurus;
import com.fendrixx.aurus.menu.Menu;
import com.fendrixx.aurus.menu.MenuButton;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InteractionListener implements Listener {
    private final Aurus plugin;
    private final Map<UUID, Long> lastClick = new HashMap<>();

    public InteractionListener(Aurus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Menu menu = plugin.getMenuManager().getActiveMenu(player.getUniqueId());

        if (menu == null)
            return;

        event.setCancelled(true);

        if (event.getAction() == Action.PHYSICAL)
            return;

        long now = System.currentTimeMillis();
        if (lastClick.containsKey(player.getUniqueId()) && now - lastClick.get(player.getUniqueId()) < 250)
            return;
        lastClick.put(player.getUniqueId(), now);

        processMenuClick(player, menu);
    }

    public void handle3DClick(Player player) {
        Menu menu = plugin.getMenuManager().getActiveMenu(player.getUniqueId());
        if (menu == null)
            return;
        long now = System.currentTimeMillis();
        if (lastClick.containsKey(player.getUniqueId()) && now - lastClick.get(player.getUniqueId()) < 250)
            return;
        lastClick.put(player.getUniqueId(), now);

        processMenuClick(player, menu);
    }

    private void processMenuClick(Player player, Menu menu) {
        float pYaw = player.getLocation().getYaw();
        float pPitch = player.getLocation().getPitch();
        double dist = menu.getMenuDistance();

        double[] local = menu.getBasis().projectToPlane(pYaw, pPitch);
        double cursorX = local[0] * dist;
        double cursorY = local[1] * dist;

        List<MenuButton> sorted = menu.getButtons().stream()
                .sorted(Comparator.comparingDouble(MenuButton::getBaseZ))
                .toList();

        for (MenuButton btn : sorted) {
            double dx = cursorX - btn.getBaseX();
            double dy = cursorY - btn.getBaseY();

            if (Math.abs(dx) < btn.getHitboxHalfW() && Math.abs(dy) < btn.getHitboxHalfH()) {
                if (plugin.getDebugManager().isEnabled(player.getUniqueId())) {
                    plugin.getDebugManager().log(player.getName() + " clicked [" + btn.getType() + "] at (" +
                            btn.getBaseX() + ", " + btn.getBaseY() + ") actions=" +
                            btn.getConfig().getStringList("actions"));
                }
                if ("INPUT".equalsIgnoreCase(btn.getType())) {
                    plugin.getInputProcessor().startInput(player, btn.getVariableName(),
                            btn.getConfig().getString("fallback-message"));
                    if (plugin.getDebugManager().isEnabled(player.getUniqueId())) {
                        plugin.getDebugManager().log("  INPUT variable=" + btn.getVariableName());
                    }
                }
                btn.onClick();
                try {
                    String clicksound = btn.getConfig().getString("sound", "minecraft:ui.button.click");
                    if (!clicksound.contains(":")) clicksound = "minecraft:" + clicksound;
                    player.playSound(player.getLocation(), clicksound, 0.6f, 1.2f);
                } catch (Exception ignored) {
                }
                break;
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getMenuManager().closeMenu(event.getPlayer());
    }
}
