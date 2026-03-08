package com.fendrixx.aurus.menu;

import com.fendrixx.aurus.packets.FakeEntityFactory;
import com.fendrixx.aurus.processors.ActionProcessor;
import com.fendrixx.aurus.util.ColorUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MenuButton {
    private final int entityId;
    private final UUID fakeUUID;
    private final Player viewer;
    private final String rawText;
    private final Runnable onClick;
    private final String type;
    private final String variableName;
    private final ConfigurationSection config;
    private final ActionProcessor actionProcessor;
    private final double baseX;
    private final double baseY;
    private double baseZ = 1.0;

    public MenuButton(int entityId, UUID fakeUUID, Player viewer, String rawText, Runnable onClick,
                      String type, String variableName, ConfigurationSection config,
                      ActionProcessor actionProcessor, double baseX, double baseY) {
        this.entityId = entityId;
        this.fakeUUID = fakeUUID;
        this.viewer = viewer;
        this.rawText = rawText;
        this.onClick = onClick;
        this.type = type;
        this.variableName = variableName;
        this.config = config;
        this.actionProcessor = actionProcessor;
        this.baseX = baseX;
        this.baseY = baseY;
    }

    public void updateText(Player player) {
        if (rawText != null && ("TEXT".equals(type) || "BUTTON".equals(type) || "INPUT".equals(type))) {
            FakeEntityFactory.updateTextDisplayText(viewer, entityId,
                    ColorUtils.format(actionProcessor.parse(player, rawText)));
        }
    }

    public int getEntityId() {
        return entityId;
    }

    public String getType() {
        return type;
    }

    public String getVariableName() {
        return variableName;
    }

    public ConfigurationSection getConfig() {
        return config;
    }

    public double getBaseX() {
        return baseX;
    }

    public double getBaseY() {
        return baseY;
    }

    public double getBaseZ() {
        return baseZ;
    }

    public void setBaseZ(double baseZ) {
        this.baseZ = baseZ;
    }

    public Player getViewer() {
        return viewer;
    }

    public void remove() {
        FakeEntityFactory.destroyEntities(viewer, entityId);
        if (fakeUUID != null) {
            FakeEntityFactory.removePlayerInfo(viewer, fakeUUID);
        }
    }

    public void onClick() {
        if (onClick != null)
            onClick.run();
    }
}
