package com.fendrixx.aurus.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MenuOpenEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final String menuId;
    private boolean cancelled = false;

    public MenuOpenEvent(Player player, String menuId) {
        this.player = player;
        this.menuId = menuId;
    }

    public Player getPlayer() { return player; }
    public String getMenuId() { return menuId; }

    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }
    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
