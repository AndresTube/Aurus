package com.fendrixx.aurus.menu;

import com.fendrixx.aurus.packets.FakeEntityFactory;
import com.fendrixx.aurus.processors.ActionProcessor;
import com.fendrixx.aurus.util.ColorUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MenuButton {
    private int entityId;
    private UUID fakeUUID;
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
    private boolean isHovered = false;
    private final ConfigurationSection hoverConfig;
    private final double hitboxHalfW;
    private final double hitboxHalfH;
    private final MenuRenderer renderer;
    private final Location spawnLocation;
    private final Runnable closeAction;
    private int hoverEntityId;
    private UUID hoverFakeUUID;
    private boolean hoverActive = false;

    public MenuButton(int entityId, UUID fakeUUID, Player viewer, String rawText, Runnable onClick,
                      String type, String variableName, ConfigurationSection config,
                      ActionProcessor actionProcessor, double baseX, double baseY,
                      MenuRenderer renderer, Location spawnLocation, Runnable closeAction) {
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
        this.hoverConfig = config.getConfigurationSection("hover");
        this.renderer = renderer;
        this.spawnLocation = spawnLocation;
        this.closeAction = closeAction;

        double size = config.getDouble("size", 1.0);
        ConfigurationSection hitbox = config.getConfigurationSection("hitbox");
        if (hitbox != null && hitbox.contains("width")) {
            this.hitboxHalfW = hitbox.getDouble("width") / 2.0;
        } else if (rawText != null && ("TEXT".equals(type) || "BUTTON".equals(type) || "INPUT".equals(type))) {
            String visible = MiniMessage.miniMessage().stripTags(actionProcessor.parse(viewer, rawText));
            int charCount = Math.max(visible.length(), 1);
            this.hitboxHalfW = size * 0.025 * charCount;
        } else {
            this.hitboxHalfW = size * 0.5;
        }
        if (hitbox != null && hitbox.contains("height")) {
            this.hitboxHalfH = hitbox.getDouble("height") / 2.0;
        } else {
            this.hitboxHalfH = size * 0.35;
        }
    }

    public void updateText(Player player) {
        if (rawText != null && ("TEXT".equals(type) || "BUTTON".equals(type) || "INPUT".equals(type))) {
            int targetEntityId = hoverActive ? hoverEntityId : entityId;
            FakeEntityFactory.updateTextDisplayText(viewer, targetEntityId,
                    ColorUtils.format(actionProcessor.parse(player, rawText)));
        }
    }

    private void updateAppearance() {
        if (hoverConfig == null || renderer == null || spawnLocation == null) return;

        if (isHovered) {
            FakeEntityFactory.destroyEntities(viewer, entityId);
            if (fakeUUID != null) {
                FakeEntityFactory.removePlayerInfo(viewer, fakeUUID);
            }
            String hoverType = hoverConfig.getString("type", type).toUpperCase();
            MenuButton hoverBtn = renderer.createComponent(viewer, hoverType, hoverConfig, spawnLocation,
                    baseX, baseY, closeAction);
            if (hoverBtn != null) {
                hoverEntityId = hoverBtn.getEntityId();
                hoverFakeUUID = hoverBtn.getFakeUUID();
                hoverActive = true;
            }
        } else {
            FakeEntityFactory.destroyEntities(viewer, hoverEntityId);
            if (hoverFakeUUID != null) {
                FakeEntityFactory.removePlayerInfo(viewer, hoverFakeUUID);
            }
            hoverActive = false;
            String originalType = config.getString("type", "BUTTON").toUpperCase();
            MenuButton original = renderer.createComponent(viewer, originalType, config, spawnLocation,
                    baseX, baseY, closeAction);
            if (original != null) {
                entityId = original.getEntityId();
                fakeUUID = original.getFakeUUID();
            }
        }
    }

    public int getEntityId() {
        return hoverActive ? hoverEntityId : entityId;
    }

    public UUID getFakeUUID() {
        return fakeUUID;
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

    public double getHitboxHalfW() {
        return hitboxHalfW;
    }

    public double getHitboxHalfH() {
        return hitboxHalfH;
    }

    public void remove() {
        FakeEntityFactory.destroyEntities(viewer, entityId);
        if (fakeUUID != null) {
            FakeEntityFactory.removePlayerInfo(viewer, fakeUUID);
        }
        if (hoverActive) {
            FakeEntityFactory.destroyEntities(viewer, hoverEntityId);
            if (hoverFakeUUID != null) {
                FakeEntityFactory.removePlayerInfo(viewer, hoverFakeUUID);
            }
        }
    }

    public void onClick() {
        if (onClick != null)
            onClick.run();
    }

    public void setHovered(boolean status) {
        if (this.isHovered != status) {
            this.isHovered = status;
            updateAppearance();
        }
    }
}
