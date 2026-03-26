package com.fendrixx.aurus.api;

import com.fendrixx.aurus.api.cursor.CursorConfig;
import com.fendrixx.aurus.api.menu.ActiveMenu;
import com.fendrixx.aurus.api.menu.MenuBuilder;
import com.fendrixx.aurus.api.menu.MenuData;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface AurusAPI {

    static AurusAPI get() {
        return AurusProvider.get();
    }

    ActiveMenu openMenu(Player player, String menuId);

    ActiveMenu openMenu(Player player, MenuData menuData);

    void closeMenu(Player player);

    ActiveMenu getActiveMenu(Player player);

    boolean hasActiveMenu(Player player);

    Set<UUID> getPlayersWithMenu();

    Collection<String> getRegisteredMenuIds();

    MenuBuilder createMenu(String id);

    void setVariable(Player player, String name, String value);

    String getVariable(Player player, String name);

    void setCursorConfig(Player player, CursorConfig config);
}
