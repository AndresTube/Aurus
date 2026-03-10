package com.fendrixx.aurus.menu;

import com.fendrixx.aurus.Aurus;
import com.fendrixx.aurus.processors.ActionProcessor;
import com.fendrixx.aurus.util.CameraBasis;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
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

    private MenuCursor cursor;
    private MenuAnimator animator;
    private Location oldLocation;
    private double menuDistance;
    private Location menuOrigin;
    private float spawnYaw;
    private float spawnPitch;
    private boolean closed = false;
    private boolean couldFlyBefore;
    private boolean updatePlaceholders = true;
    private CameraBasis basis;

    public Menu(Aurus plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.camera = new MenuCamera(player);
        this.renderer = new MenuRenderer(new ActionProcessor(plugin), plugin);
    }

    public void open(String menuId) {
        ConfigurationSection section = plugin.getConfigHandler().getMenuSection(menuId);
        if (section == null)
            return;

        this.oldLocation = player.getLocation().clone();
        this.couldFlyBefore = player.getAllowFlight();
        this.menuDistance = section.getDouble("distance", 2.5);
        this.updatePlaceholders = section.getBoolean("update-placeholders", true);

        String locationStr = section.getString("location");
        Location fixedLoc = locationStr != null ? parseLocation(locationStr) : null;

        Runnable setupMenu = (fixedLoc != null) ? () -> setupAtLocation(section, fixedLoc) : () -> setupAtPlayer(section);

        if (fixedLoc != null) {
            fixedLoc.getWorld().getChunkAtAsync(fixedLoc).thenAccept(chunk -> {
                if (!player.isOnline()) return;
                Bukkit.getScheduler().runTask(plugin, setupMenu);
            });
        } else {
            setupMenu.run();
        }
    }

    private void setupAtPlayer(ConfigurationSection section) {
        Location savedLocation = player.getLocation().clone();
        this.spawnYaw = savedLocation.getYaw();
        this.spawnPitch = savedLocation.getPitch();

        player.setAllowFlight(true);

        camera.spawn(plugin, () -> {
            player.teleport(savedLocation);
            finishSetup(section);
        });
    }

    private void setupAtLocation(ConfigurationSection section, Location fixedLoc) {
        this.spawnYaw = fixedLoc.getYaw();
        this.spawnPitch = fixedLoc.getPitch();

        player.setAllowFlight(true);

        fixedLoc.getChunk().addPluginChunkTicket(plugin);

        player.teleportAsync(fixedLoc).thenAccept(success -> {
            if (!success || !player.isOnline() || closed) return;

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (closed || !player.isOnline()) return;

                camera.spawnAt(plugin, fixedLoc, () -> {
                    player.teleport(fixedLoc);
                    finishSetup(section);
                });
            }, 5L);
        });
    }

    private void finishSetup(ConfigurationSection section) {
        this.basis = new CameraBasis(spawnYaw, spawnPitch);
        this.menuOrigin = basis.getMenuOrigin(camera.getEyeLocation(), menuDistance);

        spawnCursor();

        ConfigurationSection comps = section.getConfigurationSection("components");
        if (comps != null) {
            for (String key : comps.getKeys(false)) {
                ConfigurationSection c = comps.getConfigurationSection(key);
                double bx = c.getDouble("x");
                double by = c.getDouble("y");
                double bz = c.getDouble("z", 1.0);
                Location loc = calculateComponentLocation(bx, by, bz);
                MenuButton btn = renderer.createComponent(player, c.getString("type", "BUTTON").toUpperCase(), c,
                        loc, bx, by, this::close);
                if (btn != null) {
                    btn.setBaseZ(bz);
                    buttons.add(btn);
                }
            }
        }

        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (!otherPlayer.equals(player)) {
                otherPlayer.hideEntity(plugin, player);
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
        this.cursor = new MenuCursor();

        ConfigurationSection cursorConf = plugin.getConfigHandler().getCursorSection();

        Location loc = menuOrigin.clone();
        loc.setYaw(spawnYaw + 180f);
        loc.setPitch(-spawnPitch);

        cursor.spawn(
                player,
                loc,
                cursorConf,
                renderer.getActionProcessor()
        );
    }

    public Location calculateComponentLocation(double x, double y, double z) {
        return basis.calculateComponentLocation(menuOrigin, x, y, z);
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
        if (cursor != null)
            cursor.remove();
        for (MenuButton btn : buttons) {
            btn.remove();
        }
        buttons.clear();
        plugin.getMenuManager().removeMenu(player.getUniqueId());

        player.setAllowFlight(couldFlyBefore);
        player.setFlying(false);
        player.showEntity(plugin, player);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (!otherPlayer.equals(player)) {
                otherPlayer.showEntity(plugin, player);
            }
        }
    }

    public CameraBasis getBasis() {
        return basis;
    }

    public MenuCamera getCamera() {
        return camera;
    }

    public MenuCursor getCursor() {
        return cursor;
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

    public boolean shouldUpdatePlaceholders() {
        return updatePlaceholders;
    }

    private Location parseLocation(String str) {
        try {
            String[] parts = str.split(",");
            org.bukkit.World world = Bukkit.getWorld(parts[0].trim());
            if (world == null) return null;
            double x = Double.parseDouble(parts[1].trim());
            double y = Double.parseDouble(parts[2].trim());
            double z = Double.parseDouble(parts[3].trim());
            float yaw = parts.length > 4 ? Float.parseFloat(parts[4].trim()) : 0f;
            float pitch = parts.length > 5 ? Float.parseFloat(parts[5].trim()) : 0f;
            return new Location(world, x, y, z, yaw, pitch);
        } catch (Exception e) {
            return null;
        }
    }
}
