package com.fendrixx.aurus.menu;

import com.fendrixx.aurus.Aurus;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuManager {
    private final Aurus plugin;
    private final Map<UUID, Menu> activeMenus = new HashMap<>();

    public MenuManager(Aurus plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player player, String menuId) {
        if (plugin.getConfigHandler().getMenuSection(menuId) == null) return;

        closeMenu(player);

        Menu menu = new Menu(plugin, player);
        menu.open(menuId);
        activeMenus.put(player.getUniqueId(), menu);
    }

    public void closeMenu(Player player) {
        Menu menu = activeMenus.remove(player.getUniqueId());
        if (menu != null) {
            menu.close();
        }
    }

    public Menu getActiveMenu(UUID uuid) {
        return activeMenus.get(uuid);
    }

    public void removeMenu(UUID uuid) {
        activeMenus.remove(uuid);
    }

    public Map<UUID, Menu> getActiveMenusMap() {
        return activeMenus;
    }

    public void closeAll() {
        for (Menu menu : new java.util.ArrayList<>(activeMenus.values())) {
            menu.close();
        }
        activeMenus.clear();
    }
}