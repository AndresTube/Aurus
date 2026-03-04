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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
    private Location menuOrigin;
    private float spawnYaw;
    private float spawnPitch;
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
        camera.spawn();
        player.teleport(savedLocation);

        this.spawnYaw = player.getLocation().getYaw();
        this.spawnPitch = player.getLocation().getPitch();
        this.menuOrigin = MathUtil.getMenuOrigin(camera.getEyeLocation(), spawnYaw, spawnPitch, menuDistance);

        spawnCursor();

        ConfigurationSection comps = section.getConfigurationSection("components");
        if (comps != null) {
            for (String key : comps.getKeys(false)) {
                ConfigurationSection c = comps.getConfigurationSection(key);
                double bx = c.getDouble("x");
                double by = c.getDouble("y");
                Location loc = calculateComponentLocation(bx, by);
                MenuButton btn = renderer.createComponent(player, c.getString("type", "BUTTON").toUpperCase(), c, loc,
                        bx, by, this::close);
                if (btn != null) {
                    buttons.add(btn);
                    for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                        if (!otherPlayer.equals(player)) {
                            otherPlayer.hideEntity(plugin, btn.getDisplay());
                            otherPlayer.hideEntity(plugin, player);
                        }
                    }
                }
            }
        }

        player.hideEntity(plugin, player);
        player.addPotionEffect(
                new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false, false));

        int delay = section.getInt("update-in-ticks", 20);
        this.animator = new MenuAnimator(this, player, buttons, menuDistance, delay);
        this.animator.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnCursor() {
        ConfigurationSection c = plugin.getConfigHandler().getCursorSection();
        Location loc = menuOrigin.clone();
        loc.setYaw(spawnYaw + 180f);
        loc.setPitch(-spawnPitch);

        TextDisplay td = (TextDisplay) player.getWorld().spawnEntity(loc, EntityType.TEXT_DISPLAY);
        td.setBillboard(Display.Billboard.FIXED);

        String val = c != null ? c.getString("value", "●") : "●";
        val = renderer.getActionProcessor().parse(player, val);
        td.setText(ColorUtils.format(val));
        td.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        renderer.setupDisplay(td, (float) (c != null ? c.getDouble("size", 1.0) : 1.0), c);
        this.cursorEntity = td;

        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (!otherPlayer.equals(player)) {
                otherPlayer.hideEntity(plugin, td);
            }
        }
    }

    public Location calculateComponentLocation(double x, double y) {
        return MathUtil.calculateComponentLocation(menuOrigin, spawnYaw, spawnPitch, x, y);
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
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (!otherPlayer.equals(player)) {
                otherPlayer.showEntity(plugin, player);
            }
        }
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

    public Location getMenuOrigin() {
        return menuOrigin;
    }

    public float getSpawnYaw() {
        return spawnYaw;
    }

    public float getSpawnPitch() {
        return spawnPitch;
    }

    public double getMenuDistance() {
        return menuDistance;
    }
}
