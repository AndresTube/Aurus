package com.fendrixx.aurus.menu;
import com.fendrixx.aurus.processors.ActionProcessor;
import com.fendrixx.aurus.util.ColorUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
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
    public MenuButton createComponent(Player player, String type, ConfigurationSection conf, Location loc, Runnable closeAction) {
        float scale = (float) conf.getDouble("size", 1.0);
        String rawText = conf.getString("text", "");
        return switch (type) {
            case "TEXT" -> {
                TextDisplay td = spawnTextDisplay(loc, player, rawText, conf, scale);
                yield new MenuButton(td, rawText, null, "TEXT", null, conf);
            }
            case "BUTTON" -> {
                TextDisplay td = spawnTextDisplay(loc, player, rawText, conf, scale);
                yield new MenuButton(td, rawText, () -> actionProcessor.processList(player, conf.getStringList("actions"), closeAction), "BUTTON", null, conf);
            }
            case "INPUT" -> {
                TextDisplay td = spawnTextDisplay(loc, player, rawText, conf, scale);
                yield new MenuButton(td, rawText, () -> actionProcessor.processList(player, conf.getStringList("actions"), closeAction), "INPUT", conf.getString("variable_name"), conf);
            }
            case "ITEM" -> {
                ItemDisplay id = (ItemDisplay) loc.getWorld().spawnEntity(loc, EntityType.ITEM_DISPLAY);
                id.setItemStack(new ItemStack(Material.matchMaterial(conf.getString("material", "STONE"))));
                setupDisplay(id, scale, conf);
                yield new MenuButton(id, null, null, "ITEM", null, conf);
            }
            case "BLOCK" -> {
                BlockDisplay bd = (BlockDisplay) loc.getWorld().spawnEntity(loc, EntityType.BLOCK_DISPLAY);
                bd.setBlock(Material.matchMaterial(conf.getString("block", "STONE")).createBlockData());
                setupDisplay(bd, scale, conf);
                yield new MenuButton(bd, null, null, "BLOCK", null, conf);
            }
            default -> null;
        };
    }
    private TextDisplay spawnTextDisplay(Location loc, Player p, String raw, ConfigurationSection conf, float scale) {
        TextDisplay td = (TextDisplay) loc.getWorld().spawnEntity(loc, EntityType.TEXT_DISPLAY);
        td.setText(ColorUtils.format(actionProcessor.parse(p, raw)));
        if (!conf.getBoolean("background", false)) td.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        setupDisplay(td, scale, conf);
        return td;
    }
    public void setupDisplay(Display display, float scale, ConfigurationSection conf) {
        display.setBillboard(Display.Billboard.CENTER);
        Transformation trans = display.getTransformation();
        trans.getScale().set(new Vector3f(scale, scale, scale));
        if (conf != null && conf.contains("rotation")) {
            trans.getLeftRotation().rotationXYZ(
                    (float) Math.toRadians(conf.getDouble("rotation.x")),
                    (float) Math.toRadians(conf.getDouble("rotation.y")),
                    (float) Math.toRadians(conf.getDouble("rotation.z"))
            );
        }
        display.setTransformation(trans);
    }
}