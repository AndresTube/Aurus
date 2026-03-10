package com.fendrixx.aurus.menu;

import com.fendrixx.aurus.packets.FakeEntityFactory;
import com.fendrixx.aurus.packets.SkinFetcher;
import com.fendrixx.aurus.processors.ActionProcessor;
import com.fendrixx.aurus.util.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class MenuRenderer {
    private final ActionProcessor actionProcessor;
    private final JavaPlugin plugin;

    public MenuRenderer(ActionProcessor actionProcessor, JavaPlugin plugin) {
        this.actionProcessor = actionProcessor;
        this.plugin = plugin;
    }

    public ActionProcessor getActionProcessor() {
        return actionProcessor;
    }

    public MenuButton createComponent(Player player, String type, ConfigurationSection conf, Location loc,
            double baseX, double baseY, Runnable closeAction) {
        float size = (float) conf.getDouble("size", 1.0);
        String rawText = conf.getString("text", "");
        float rotX = (float) conf.getDouble("rotation.x", 0);
        float rotY = (float) conf.getDouble("rotation.y", 0);
        float rotZ = (float) conf.getDouble("rotation.z", 0);

        return switch (type) {
            case "TEXT" -> {
                String text = ColorUtils.format(actionProcessor.parse(player, rawText));
                int bgColor = conf.getBoolean("background", true) ? 0x40000000 : 0;
                boolean shadow = conf.getBoolean("shadow", false);
                int entityId = FakeEntityFactory.spawnFakeTextDisplay(player, loc, text,
                        bgColor, shadow, (byte) 0, size, rotX, rotY, rotZ);
                yield new MenuButton(entityId, null, player, rawText, null, "TEXT", null, conf, actionProcessor, baseX, baseY, this, loc, closeAction);
            }
            case "BUTTON" -> {
                String text = ColorUtils.format(actionProcessor.parse(player, rawText));
                int bgColor = conf.getBoolean("background", true) ? 0x40000000 : 0;
                boolean shadow = conf.getBoolean("shadow", false);
                int entityId = FakeEntityFactory.spawnFakeTextDisplay(player, loc, text,
                        bgColor, shadow, (byte) 0, size, rotX, rotY, rotZ);
                yield new MenuButton(entityId, null, player, rawText,
                        () -> actionProcessor.processList(player, conf.getStringList("actions"), closeAction),
                        "BUTTON", null, conf, actionProcessor, baseX, baseY, this, loc, closeAction);
            }
            case "INPUT" -> {
                String text = ColorUtils.format(actionProcessor.parse(player, rawText));
                int bgColor = conf.getBoolean("background", true) ? 0x40000000 : 0;
                boolean shadow = conf.getBoolean("shadow", false);
                int entityId = FakeEntityFactory.spawnFakeTextDisplay(player, loc, text,
                        bgColor, shadow, (byte) 0, size, rotX, rotY, rotZ);
                yield new MenuButton(entityId, null, player, rawText,
                        () -> actionProcessor.processList(player, conf.getStringList("actions"), closeAction),
                        "INPUT", conf.getString("variable_name"), conf, actionProcessor, baseX, baseY, this, loc, closeAction);
            }
            case "ITEM" -> {
                String mat = actionProcessor.parse(player, conf.getString("material", "STONE"));
                ItemStack item = new ItemStack(Material.matchMaterial(mat));
                if (conf.contains("model-id")) {
                    ItemMeta meta = item.getItemMeta();
                    meta.setCustomModelData(conf.getInt("model-id"));
                    item.setItemMeta(meta);
                }
                int entityId = FakeEntityFactory.spawnFakeItemDisplay(player, loc, item, size, rotX, rotY, rotZ);
                yield new MenuButton(entityId, null, player, null, null, "ITEM", null, conf, actionProcessor, baseX, baseY, this, loc, closeAction);
            }
            case "BLOCK" -> {
                String mat = actionProcessor.parse(player, conf.getString("material", "STONE"));
                int blockStateId = com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState
                        .getByString("minecraft:" + mat.toLowerCase()).getGlobalId();
                int entityId = FakeEntityFactory.spawnFakeBlockDisplay(player, loc, blockStateId, size, rotX, rotY, rotZ);
                yield new MenuButton(entityId, null, player, null, null, "BLOCK", null, conf, actionProcessor, baseX, baseY, this, loc, closeAction);
            }
            case "ENTITY" -> {
                String entityName = conf.getString("entity", "ZOMBIE");
                float headYawRot = (float) conf.getDouble("rotation.x-head", 0);
                float headPitchRot = (float) conf.getDouble("rotation.y-head", 0);
                int entityId = FakeEntityFactory.nextEntityId();
                FakeEntityFactory.spawnFakeEntity(player, entityId, loc, entityName);
                FakeEntityFactory.setScale(player, entityId, size);
                if (rotX != 0 || headPitchRot != 0 || headYawRot != 0)
                    FakeEntityFactory.rotateEntityFull(player, entityId, rotX, headPitchRot, headYawRot != 0 ? headYawRot : rotX);
                yield new MenuButton(entityId, null, player, null, null, "ENTITY", null, conf, actionProcessor, baseX, baseY, this, loc, closeAction);
            }
            case "PLAYER" -> {
                String skinTarget = actionProcessor.parse(player, conf.getString("skin", player.getName()));
                String nametag = actionProcessor.parse(player, conf.getString("nametag", ""));
                float headYawRot = (float) conf.getDouble("rotation.x-head", 0);
                float headPitchRot = (float) conf.getDouble("rotation.y-head", 0);
                int entityId = FakeEntityFactory.nextEntityId();
                UUID fakeUUID = UUID.randomUUID();
                float capturedRotX = rotX;
                float capturedHeadYaw = headYawRot;
                float capturedHeadPitch = headPitchRot;
                SkinFetcher.fetchAsync(skinTarget).thenAccept(skin ->
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (!player.isOnline()) return;
                        FakeEntityFactory.spawnFakePlayer(player, entityId, fakeUUID, loc, skin, nametag);
                        FakeEntityFactory.setScale(player, entityId, size);
                        if (capturedRotX != 0 || capturedHeadPitch != 0 || capturedHeadYaw != 0)
                            FakeEntityFactory.rotateEntityFull(player, entityId, capturedRotX, capturedHeadPitch, capturedHeadYaw != 0 ? capturedHeadYaw : capturedRotX);
                    })
                );
                yield new MenuButton(entityId, fakeUUID, player, null, null, "PLAYER", null, conf, actionProcessor, baseX, baseY, this, loc, closeAction);
            }
            default -> null;
        };
    }
}
