package com.fendrixx.aurus.listeners;

import com.fendrixx.aurus.Aurus;
import com.fendrixx.aurus.api.component.AreaType;
import com.fendrixx.aurus.api.event.ComponentClickEvent;
import com.fendrixx.aurus.menu.Menu;
import com.fendrixx.aurus.menu.MenuArea;
import com.fendrixx.aurus.menu.MenuButton;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
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

        double[] local = menu.getBasis().getCursorXY(pYaw, pPitch, dist);
        double cursorX = local[0];
        double cursorY = local[1];

        for (MenuArea area : menu.getAreas()) {
            if (!area.containsCursor(cursorX, cursorY)) continue;

            List<MenuButton> sorted = area.getVisibleButtons().stream()
                    .sorted(Comparator.comparingDouble(MenuButton::getBaseZ))
                    .toList();

            for (MenuButton btn : sorted) {
                double effectiveX = area.getAreaX() + btn.getAreaLocalX();
                double effectiveY = area.getAreaY() + btn.getAreaLocalY() + area.getScrollOffset();
                double dx = cursorX - effectiveX;
                double dy = cursorY - effectiveY;

                if (Math.abs(dx) < btn.getHitboxHalfW() && Math.abs(dy) < btn.getHitboxHalfH()) {
                    String componentId = null;
                    for (Map.Entry<String, MenuButton> entry : area.getButtonMap().entrySet()) {
                        if (entry.getValue() == btn) {
                            componentId = entry.getKey();
                            break;
                        }
                    }

                    ComponentClickEvent clickEvent = new ComponentClickEvent(
                            player, menu.getMenuId(), area.getId(), componentId, btn.getType());
                    Bukkit.getPluginManager().callEvent(clickEvent);
                    if (clickEvent.isCancelled()) return;

                    if (plugin.getDebugManager().isEnabled(player.getUniqueId())) {
                        plugin.getDebugManager().log(player.getName() + " clicked [" + btn.getType() + "] in area [" +
                                area.getId() + "] at (" + btn.getBaseX() + ", " + btn.getBaseY() + ") actions=" +
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
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onHotbarScroll(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        Menu menu = plugin.getMenuManager().getActiveMenu(player.getUniqueId());
        if (menu == null || !menu.hasScrollArea()) return;

        event.setCancelled(true);

        int prev = event.getPreviousSlot();
        int next = event.getNewSlot();
        int diff = next - prev;
        // Handle wraparound (0→8 = scroll down, 8→0 = scroll up)
        if (diff > 4) diff -= 9;
        if (diff < -4) diff += 9;

        double scrollDelta = diff > 0 ? 1.0 : -1.0;

        float pYaw = player.getLocation().getYaw();
        float pPitch = player.getLocation().getPitch();
        double dist = menu.getMenuDistance();
        double[] local = menu.getBasis().getCursorXY(pYaw, pPitch, dist);
        double cursorX = local[0];
        double cursorY = local[1];

        for (MenuArea area : menu.getAreas()) {
            if (area.getType() == AreaType.SCROLL && area.containsCursor(cursorX, cursorY)) {
                area.scroll(scrollDelta, menu, player);
                break;
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getMenuManager().closeMenu(event.getPlayer());
    }
}
