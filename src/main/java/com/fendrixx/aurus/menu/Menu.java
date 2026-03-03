package com.fendrixx.aurus.menu;

import com.fendrixx.aurus.Aurus;
import com.fendrixx.aurus.processors.ActionProcessor;
import com.fendrixx.aurus.util.ColorUtils;
import com.fendrixx.aurus.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private final Aurus plugin;
    private final Player player;
    private final MenuCamera camera;
    private final MenuRenderer renderer;
    private final List<MenuButton> buttons = new ArrayList<>();

    private Display cursorEntity;
    private MenuAnimator animator;
    private Location oldLocation;
    private double menuDistance;
    private boolean closed = false;

    public Menu(Aurus plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.camera = new MenuCamera(player);
        this.renderer = new MenuRenderer(new ActionProcessor(plugin));
    }

    public void open(String menuId) {
        ConfigurationSection section = plugin.getConfigHandler().getMenuSection(menuId);
        if (section == null)
            return;

        this.oldLocation = player.getLocation().clone();

        this.menuDistance = section.getDouble("distance", 2.5);

        Location savedLocation = player.getLocation().clone();
        savedLocation.setPitch(0);

        camera.spawn();
        player.teleport(savedLocation);

        spawnCursor();

        ConfigurationSection comps = section.getConfigurationSection("components");
        if (comps != null) {
            for (String key : comps.getKeys(false)) {
                ConfigurationSection c = comps.getConfigurationSection(key);
                Location loc = calculateComponentLocation(c.getDouble("x"), c.getDouble("y"));
                MenuButton btn = renderer.createComponent(player, c.getString("type", "BUTTON").toUpperCase(), c, loc,
                        this::close);
                if (btn != null) {
                    buttons.add(btn);
                    for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                        if (!otherPlayer.equals(player)) {
                            otherPlayer.hideEntity(plugin, btn.getDisplay());
                        }
                    }
                }
            }
        }

        int delay = section.getInt("update-in-ticks", 20);
        this.animator = new MenuAnimator(this, player, buttons, menuDistance, delay);
        this.animator.runTaskTimer(plugin, 0L, 1L);

        player.hideEntity(plugin, player);
    }

    private void spawnCursor() {
        ConfigurationSection c = plugin.getConfigHandler().getCursorSection();
        Location loc = player.getEyeLocation().add(player.getLocation().getDirection().multiply(menuDistance));
        TextDisplay td = (TextDisplay) player.getWorld().spawnEntity(loc, EntityType.TEXT_DISPLAY);
        td.setBillboard(org.bukkit.entity.Display.Billboard.FIXED);
        td.setText(ColorUtils.format(c != null ? c.getString("value", "!") : "!"));
        td.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        renderer.setupDisplay(td, (float) (c != null ? c.getDouble("size", 1.5) : 1.5), c);
        this.cursorEntity = td;
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (!otherPlayer.equals(player)) {
                otherPlayer.hideEntity(plugin, td);
            }
        }
    }

    public Location calculateComponentLocation(double x, double y) {
        Location origin = player.getEyeLocation().clone().add(0, -0.4, 0);

        Vector direction = player.getEyeLocation().getDirection().normalize();

        Vector right = new Vector(-direction.getZ(), 0, direction.getX()).normalize();
        Vector up = direction.clone().crossProduct(right).multiply(-1).normalize();

        Location center = origin.add(direction.multiply(menuDistance));
        center.add(right.multiply(x));
        center.add(up.multiply(y));

        center.setYaw(player.getLocation().getYaw() + 180f);
        center.setPitch(-player.getLocation().getPitch());

        return center;
    }

    public void close() {
        if (closed)
            return;
        closed = true;

        if (oldLocation != null && player.isOnline())
            player.teleport(oldLocation);
        if (animator != null)
            animator.cancel();
        camera.remove();
        if (cursorEntity != null)
            cursorEntity.remove();
        buttons.forEach(b -> b.getDisplay().remove());
        buttons.clear();
        plugin.getMenuManager().removeMenu(player.getUniqueId());
        player.showEntity(plugin, player);
    }

    public void updateVisuals() {
        for (MenuButton btn : buttons) {
            btn.updateText(player);
        }
    }

    public MenuCamera getCamera() {
        return camera;
    }

    public Display getCursor() {
        return cursorEntity;
    }

    public List<MenuButton> getButtons() {
        return buttons;
    }
}
