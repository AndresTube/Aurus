package com.fendrixx.aurus.api.menu;

import com.fendrixx.aurus.api.action.MenuAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MenuData {
    private final String id;
    private final double distance;
    private final boolean updatePlaceholders;
    private final String location;
    private final List<MenuAction> onOpenActions;
    private final List<MenuAction> onCloseActions;
    private final Map<String, AreaData> areas;

    MenuData(String id, double distance, boolean updatePlaceholders,
             String location, List<MenuAction> onOpenActions, List<MenuAction> onCloseActions,
             Map<String, AreaData> areas) {
        this.id = id;
        this.distance = distance;
        this.updatePlaceholders = updatePlaceholders;
        this.location = location;
        this.onOpenActions = Collections.unmodifiableList(new ArrayList<>(onOpenActions));
        this.onCloseActions = Collections.unmodifiableList(new ArrayList<>(onCloseActions));
        this.areas = Collections.unmodifiableMap(new LinkedHashMap<>(areas));
    }

    public String getId() { return id; }
    public double getDistance() { return distance; }
    public boolean shouldUpdatePlaceholders() { return updatePlaceholders; }
    public String getLocation() { return location; }
    public List<MenuAction> getOnOpenActions() { return onOpenActions; }
    public List<MenuAction> getOnCloseActions() { return onCloseActions; }
    public Map<String, AreaData> getAreas() { return areas; }
}
