package com.fendrixx.aurus.menu;

import com.fendrixx.aurus.processors.ActionProcessor;
import com.fendrixx.aurus.util.ColorUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

public class MenuRenderer {
    private final ActionProcessor actionProcessor;

    public MenuRenderer(ActionProcessor actionProcessor) {
        this.actionProcessor = actionProcessor;
    }

    public ActionProcessor getActionProcessor() {
        return actionProcessor;
    }

    public MenuButton createComponent(Player player, String type, ConfigurationSection conf, Location loc,
            double baseX, double baseY, Runnable closeAction) {
        float size = (float) conf.getDouble("size", 1.0);
        String rawText = conf.getString("text", "");

        return switch (type) {
            case "TEXT" -> {
                TextDisplay td = spawnTextDisplay(loc, player, rawText, conf, size);
                yield new MenuButton(td, rawText, null, "TEXT", null, conf, actionProcessor, baseX, baseY);
            }
            case "BUTTON" -> {
                TextDisplay td = spawnTextDisplay(loc, player, rawText, conf, size);
                yield new MenuButton(td, rawText,
                        () -> actionProcessor.processList(player, conf.getStringList("actions"), closeAction),
                        "BUTTON", null, conf, actionProcessor, baseX, baseY);
            }
            case "INPUT" -> {
                TextDisplay td = spawnTextDisplay(loc, player, rawText, conf, size);
                yield new MenuButton(td, rawText,
                        () -> actionProcessor.processList(player, conf.getStringList("actions"), closeAction),
                        "INPUT", conf.getString("variable_name"), conf, actionProcessor, baseX, baseY);
            }
            case "ITEM" -> {
                ItemDisplay idisp = (ItemDisplay) loc.getWorld().spawnEntity(loc, EntityType.ITEM_DISPLAY);
                String mat = actionProcessor.parse(player, conf.getString("material", "STONE"));
                idisp.setItemStack(new ItemStack(org.bukkit.Material.matchMaterial(mat)));
                setupDisplay(idisp, size, conf);
                yield new MenuButton(idisp, null, null, "ITEM", null, conf, actionProcessor, baseX, baseY);
            }
            case "BLOCK" -> {
                BlockDisplay bd = (BlockDisplay) loc.getWorld().spawnEntity(loc, EntityType.BLOCK_DISPLAY);
                String mat = actionProcessor.parse(player, conf.getString("material", "STONE"));
                bd.setBlock(org.bukkit.Material.matchMaterial(mat).createBlockData());
                setupDisplay(bd, size, conf);
                yield new MenuButton(bd, null, null, "BLOCK", null, conf, actionProcessor, baseX, baseY);
            }
            default -> null;
        };
    }

    private TextDisplay spawnTextDisplay(Location loc, Player p, String raw, ConfigurationSection conf, float size) {
        TextDisplay td = (TextDisplay) loc.getWorld().spawnEntity(loc, EntityType.TEXT_DISPLAY);
        td.setBillboard(Display.Billboard.FIXED);
        td.setText(ColorUtils.format(actionProcessor.parse(p, raw)));
        if (!conf.getBoolean("background", true))
            td.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        setupDisplay(td, size, conf);
        return td;
    }

    public void setupDisplay(Display display, float scale, ConfigurationSection conf) {
        Transformation trans = display.getTransformation();
        trans.getScale().set(new Vector3f(scale, scale, scale));
        if (conf != null && conf.contains("rotation")) {
            trans.getLeftRotation().rotationXYZ(
                    (float) Math.toRadians(conf.getDouble("rotation.x")),
                    (float) Math.toRadians(conf.getDouble("rotation.y")),
                    (float) Math.toRadians(conf.getDouble("rotation.z")));
        }
        display.setTransformation(trans);
    }
}