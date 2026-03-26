package com.fendrixx.aurus.api;

import com.fendrixx.aurus.Aurus;
import com.fendrixx.aurus.api.cursor.CursorConfig;
import com.fendrixx.aurus.api.menu.ActiveMenu;
import com.fendrixx.aurus.api.menu.MenuBuilder;
import com.fendrixx.aurus.api.menu.MenuData;
import com.fendrixx.aurus.menu.Menu;
import com.fendrixx.aurus.menu.MenuArea;
import com.fendrixx.aurus.menu.MenuButton;
import com.fendrixx.aurus.util.ColorUtils;
import com.fendrixx.aurus.packets.FakeEntityFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AurusAPIImpl implements AurusAPI {

    private final Aurus plugin;
    private final Map<UUID, CursorConfig> cursorOverrides = new HashMap<>();

    public AurusAPIImpl(Aurus plugin) {
        this.plugin = plugin;
    }

    @Override
    public ActiveMenu openMenu(Player player, String menuId) {
        plugin.getMenuManager().openMenu(player, menuId);
        return getActiveMenu(player);
    }

    @Override
    public ActiveMenu openMenu(Player player, MenuData menuData) {
        ConfigurationSection section = MenuDataConverter.toSection(menuData);
        Menu menu = plugin.getMenuManager().openMenuFromSection(player, menuData.getId(), section);
        if (menu == null) return null;
        return wrapMenu(menu);
    }

    @Override
    public void closeMenu(Player player) {
        plugin.getMenuManager().closeMenu(player);
    }

    @Override
    public ActiveMenu getActiveMenu(Player player) {
        Menu menu = plugin.getMenuManager().getActiveMenu(player.getUniqueId());
        if (menu == null) return null;
        return wrapMenu(menu);
    }

    @Override
    public boolean hasActiveMenu(Player player) {
        return plugin.getMenuManager().getActiveMenu(player.getUniqueId()) != null;
    }

    @Override
    public Set<UUID> getPlayersWithMenu() {
        return plugin.getMenuManager().getActivePlayerUUIDs();
    }

    @Override
    public Collection<String> getRegisteredMenuIds() {
        return plugin.getConfigHandler().getMenuKeys();
    }

    @Override
    public MenuBuilder createMenu(String id) {
        return new MenuBuilder(id);
    }

    @Override
    public void setVariable(Player player, String name, String value) {
        plugin.getInputProcessor().setValue(name, value);
    }

    @Override
    public String getVariable(Player player, String name) {
        return plugin.getInputProcessor().getValue(name);
    }

    @Override
    public void setCursorConfig(Player player, CursorConfig config) {
        cursorOverrides.put(player.getUniqueId(), config);
    }

    public CursorConfig getCursorOverride(UUID uuid) {
        return cursorOverrides.remove(uuid);
    }

    private ActiveMenu wrapMenu(Menu menu) {
        return new ActiveMenuImpl(menu);
    }

    private static class ActiveMenuImpl implements ActiveMenu {
        private final Menu menu;

        ActiveMenuImpl(Menu menu) {
            this.menu = menu;
        }

        @Override
        public String getMenuId() {
            return menu.getMenuId();
        }

        @Override
        public Player getPlayer() {
            return menu.getPlayer();
        }

        @Override
        public Set<String> getAreaIds() {
            return menu.getAreaMap().keySet();
        }

        @Override
        public Set<String> getComponentIds() {
            Set<String> ids = new HashSet<>();
            for (MenuArea area : menu.getAreas()) {
                ids.addAll(area.getButtonMap().keySet());
            }
            return ids;
        }

        @Override
        public void updateComponentText(String componentId, String newText) {
            for (MenuArea area : menu.getAreas()) {
                MenuButton btn = area.getButtonMap().get(componentId);
                if (btn != null) {
                    FakeEntityFactory.updateTextDisplayText(btn.getViewer(), btn.getEntityId(),
                            ColorUtils.format(newText));
                    return;
                }
            }
        }

        @Override
        public void close() {
            menu.close();
        }
    }
}
