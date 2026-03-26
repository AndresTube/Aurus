package com.fendrixx.aurus.menu;

import com.fendrixx.aurus.Aurus;
import com.fendrixx.aurus.api.event.MenuOpenEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MenuManager {
    private final Aurus plugin;
    private final Map<UUID, Menu> activeMenus = new HashMap<>();

    public MenuManager(Aurus plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player player, String menuId) {
        if (plugin.getConfigHandler().getMenuSection(menuId) == null) return;

        MenuOpenEvent event = new MenuOpenEvent(player, menuId);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        closeMenu(player);

        Menu menu = new Menu(plugin, player);
        menu.open(menuId);
        activeMenus.put(player.getUniqueId(), menu);
    }

    public Menu openMenuFromSection(Player player, String menuId, ConfigurationSection section) {
        MenuOpenEvent event = new MenuOpenEvent(player, menuId);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return null;

        closeMenu(player);

        Menu menu = new Menu(plugin, player);
        menu.openFromSection(menuId, section);
        activeMenus.put(player.getUniqueId(), menu);
        return menu;
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

    public String getActiveMenuString(UUID uuid) {
        Menu menu = activeMenus.get(uuid);
        return menu != null ? menu.toString() : "";
    }

    public void removeMenu(UUID uuid) {
        activeMenus.remove(uuid);
    }

    public Set<UUID> getActivePlayerUUIDs() {
        return Collections.unmodifiableSet(activeMenus.keySet());
    }

    public void closeAll() {
        for (Menu menu : new java.util.ArrayList<>(activeMenus.values())) {
            menu.close();
        }
        activeMenus.clear();
    }
}
