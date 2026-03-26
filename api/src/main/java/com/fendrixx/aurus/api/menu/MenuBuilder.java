package com.fendrixx.aurus.api.menu;

import com.fendrixx.aurus.api.action.MenuAction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MenuBuilder {
    private final String id;
    private double distance = 2.5;
    private boolean updatePlaceholders = true;
    private String location;
    private final List<MenuAction> onOpenActions = new ArrayList<>();
    private final List<MenuAction> onCloseActions = new ArrayList<>();
    private final Map<String, AreaData> areas = new LinkedHashMap<>();

    public MenuBuilder(String id) {
        this.id = id;
    }

    public MenuBuilder distance(double distance) { this.distance = distance; return this; }
    public MenuBuilder updatePlaceholders(boolean update) { this.updatePlaceholders = update; return this; }
    public MenuBuilder location(String location) { this.location = location; return this; }

    public MenuBuilder onOpen(String actionStr) { this.onOpenActions.add(new MenuAction(actionStr)); return this; }
    public MenuBuilder onOpen(MenuAction action) { this.onOpenActions.add(action); return this; }
    public MenuBuilder onClose(String actionStr) { this.onCloseActions.add(new MenuAction(actionStr)); return this; }
    public MenuBuilder onClose(MenuAction action) { this.onCloseActions.add(action); return this; }

    public MenuBuilder addArea(String areaId, Consumer<AreaBuilder> config) {
        AreaBuilder builder = new AreaBuilder(areaId);
        config.accept(builder);
        areas.put(areaId, builder.build());
        return this;
    }

    public MenuBuilder addArea(AreaData areaData) {
        areas.put(areaData.getId(), areaData);
        return this;
    }

    public MenuData build() {
        return new MenuData(id, distance, updatePlaceholders,
                location, onOpenActions, onCloseActions, areas);
    }
}
