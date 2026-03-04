package com.fendrixx.aurus.listeners;

import com.fendrixx.aurus.Aurus;
import com.fendrixx.aurus.menu.Menu;
import com.fendrixx.aurus.menu.MenuButton;
import com.fendrixx.aurus.util.MathUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
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
        float cYaw = menu.getSpawnYaw();
        float cPitch = menu.getSpawnPitch();
        double dist = menu.getMenuDistance();

        float dYaw = MathUtil.normalizeAngle(pYaw - cYaw);
        float dPitch = MathUtil.normalizeAngle(pPitch - cPitch);

        double cursorX = Math.tan(Math.toRadians(dYaw)) * dist;
        double cursorY = -Math.tan(Math.toRadians(dPitch)) * dist;

        for (MenuButton btn : menu.getButtons()) {
            double size = btn.getConfig().getDouble("size", 1.0);
            double hitRadius = 0.5 * size;

            double dx = cursorX - btn.getBaseX();
            double dy = cursorY - btn.getBaseY();
            double d2 = Math.sqrt(dx * dx + dy * dy);

            if (d2 < hitRadius) {
                if ("INPUT".equalsIgnoreCase(btn.getType())) {
                    plugin.getInputProcessor().startInput(player, btn.getVariableName());
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
