package com.fendrixx.aurus.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ComponentClickEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final String menuId;
    private final String areaId;
    private final String componentId;
    private final String componentType;
    private boolean cancelled = false;

    public ComponentClickEvent(Player player, String menuId, String areaId, String componentId, String componentType) {
        this.player = player;
        this.menuId = menuId;
        this.areaId = areaId;
        this.componentId = componentId;
        this.componentType = componentType;
    }

    public Player getPlayer() { return player; }
    public String getMenuId() { return menuId; }
    public String getAreaId() { return areaId; }
    public String getComponentId() { return componentId; }
    public String getComponentType() { return componentType; }

    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }
    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
