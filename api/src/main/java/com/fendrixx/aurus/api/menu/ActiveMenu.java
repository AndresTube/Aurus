package com.fendrixx.aurus.api.menu;

import org.bukkit.entity.Player;

import java.util.Set;

public interface ActiveMenu {

    String getMenuId();

    Player getPlayer();

    Set<String> getAreaIds();

    Set<String> getComponentIds();

    void updateComponentText(String componentId, String newText);

    void close();
}
