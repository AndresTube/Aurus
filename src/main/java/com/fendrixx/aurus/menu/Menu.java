package com.fendrixx.aurus.menu;

import com.fendrixx.aurus.Aurus;
import com.fendrixx.aurus.processors.ActionProcessor;
import com.fendrixx.aurus.util.ColorUtils;
import com.fendrixx.aurus.util.MathUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

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
    private Location cameraLocation;
    private double menuDistance;
    private long lastClickTime = 0;
    public Menu(Aurus plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.camera = new MenuCamera();
        this.renderer = new MenuRenderer(new ActionProcessor(plugin));
    }
    public void open(String menuId) {
        ConfigurationSection section = plugin.getConfigHandler().getMenuSection(menuId);
        if (section == null) return;
        this.oldLocation = player.getLocation().clone();
        this.menuDistance = 2.5;
        this.cameraLocation = player.getLocation();
        this.cameraLocation.setPitch(0);

        camera.spawn(player, cameraLocation);

        spawnCursor();
        ConfigurationSection comps = section.getConfigurationSection("components");
        if (comps != null) {
            for (String key : comps.getKeys(false)) {
                ConfigurationSection c = comps.getConfigurationSection(key);
                Location loc = calculateComponentLocation(c.getDouble("x"), c.getDouble("y"));
                MenuButton btn = renderer.createComponent(player, c.getString("type", "BUTTON").toUpperCase(), c, loc, this::close);
                if (btn != null) buttons.add(btn);
            }
        }
        int delay = section.getInt("update-in-ticks", 20);
        this.animator = new MenuAnimator(this, player, buttons, menuDistance, delay);
        this.animator.runTaskTimerAsynchronously(plugin, 0L, 1L);
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY, 999999, 1, false, false));
    }
    private void spawnCursor() {
        ConfigurationSection c = plugin.getConfigHandler().getCursorSection();
        Location loc = player.getEyeLocation().add(player.getLocation().getDirection().multiply(menuDistance));
        TextDisplay td = (TextDisplay) player.getWorld().spawnEntity(loc, EntityType.TEXT_DISPLAY);
        td.setText(ColorUtils.format(c != null ? c.getString("value", "!") : "!"));
        td.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        renderer.setupDisplay(td, (float) (c != null ? c.getDouble("size", 1.5) : 1.5), c);
        this.cursorEntity = td;
    }
    public Location calculateComponentLocation(double x, double y) {
        return MathUtil.getComponentLocation(
                cameraLocation.clone().add(0, 1.62, 0),
                cameraLocation.getYaw(),
                menuDistance,
                x,
                y
        );
    }
    public void close() {
        if (oldLocation != null && player.isOnline()) player.teleport(oldLocation);
        if (animator != null) animator.cancel();
        if (camera != null) {
            camera.despawn(player);
        }
        if (cursorEntity != null) cursorEntity.remove();
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
    public void handleInteraction() {
        long now = System.currentTimeMillis();
        if (now - lastClickTime < 50) return;
        lastClickTime = now;
        if (cursorEntity == null) return;
        for (MenuButton btn : buttons) {
            if (!btn.getDisplay().isValid()) continue;
            double dist = cursorEntity.getLocation().distance(btn.getDisplay().getLocation());
            if (dist < 0.8) {
                btn.onClick();
                player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 0.5f, 1f);
                break;
            }
        }
    }
    public MenuCamera getCamera() { return camera; }
    public Display getCursor() { return cursorEntity; }
    public List<MenuButton> getButtons() { return buttons; }
    public Aurus getPlugin() { return plugin; }
    public Location getCameraLocation() { return cameraLocation; }
}