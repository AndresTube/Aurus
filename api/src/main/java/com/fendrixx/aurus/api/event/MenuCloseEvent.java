package com.fendrixx.aurus.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MenuCloseEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final String menuId;

    public MenuCloseEvent(Player player, String menuId) {
        this.player = player;
        this.menuId = menuId;
    }

    public Player getPlayer() { return player; }
    public String getMenuId() { return menuId; }

    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
