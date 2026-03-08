package com.fendrixx.aurus.menu;

import com.fendrixx.aurus.packets.FakeEntityFactory;
import com.fendrixx.aurus.processors.ActionProcessor;
import com.fendrixx.aurus.util.ColorUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MenuCursor {
    private int entityId;
    private Player viewer;

    public void spawn(Player player, Location loc, ConfigurationSection conf, ActionProcessor actionProcessor) {
        this.viewer = player;
        String type = conf != null ? conf.getString("type", "TEXT").toUpperCase() : "TEXT";
        String value = conf != null ? conf.getString("value", "●") : "●";
        float size = (float) (conf != null ? conf.getDouble("size", 1.0) : 1.0);

        switch (type) {
            case "ITEM" -> {
                Material mat = Material.matchMaterial(actionProcessor.parse(player, value));
                if (mat == null) mat = Material.ARROW;
                this.entityId = FakeEntityFactory.spawnFakeItemDisplay(player, loc,
                        new ItemStack(mat), size, 0, 0, 0);
            }
            case "BLOCK" -> {
                Material mat = Material.matchMaterial(actionProcessor.parse(player, value));
                if (mat == null) mat = Material.STONE;
                int blockStateId = com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState
                        .getByString("minecraft:" + mat.name().toLowerCase()).getGlobalId();
                this.entityId = FakeEntityFactory.spawnFakeBlockDisplay(player, loc,
                        blockStateId, size, 0, 0, 0);
            }
            default -> {
                String text = ColorUtils.format(actionProcessor.parse(player, value));
                this.entityId = FakeEntityFactory.spawnFakeTextDisplay(player, loc, text,
                        0, false, (byte) 0, size, 0, 0, 0);
            }
        }
    }

    public void teleport(Location loc) {
        if (viewer != null && viewer.isOnline()) {
            FakeEntityFactory.teleportEntity(viewer, entityId, loc);
        }
    }

    public void remove() {
        if (viewer != null) {
            FakeEntityFactory.destroyEntities(viewer, entityId);
        }
    }

    public int getEntityId() {
        return entityId;
    }
}
