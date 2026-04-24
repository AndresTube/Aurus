package com.fendrixx.aurus.menu;

import com.fendrixx.aurus.Aurus;
import com.fendrixx.aurus.api.component.AnimationType;
import com.fendrixx.aurus.api.component.AreaType;
import com.fendrixx.aurus.api.event.MenuCloseEvent;
import com.fendrixx.aurus.processors.ActionProcessor;
import com.fendrixx.aurus.util.CameraBasis;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Menu {
    private final Aurus plugin;
    private final Player player;
    private final MenuCamera camera;
    private final MenuRenderer renderer;
    private final List<MenuArea> areas = new ArrayList<>();
    private final Map<String, MenuArea> areaMap = new LinkedHashMap<>();

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
    private List<String> onCloseActions;
    private CameraBasis basis;
    private String menuId;
    private ItemStack[] savedHotbar;
    private boolean hasScrollArea = false;
    private int updateTaskId = -1;

    public Menu(Aurus plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.camera = new MenuCamera(player);
        this.renderer = new MenuRenderer(new ActionProcessor(plugin), plugin);
    }

    public void open(String menuId) {
        this.menuId = menuId;
        ConfigurationSection section = plugin.getConfigHandler().getMenuSection(menuId);
        if (section == null)
            return;

        this.oldLocation = player.getLocation().clone();
        this.couldFlyBefore = player.getAllowFlight();
        this.menuDistance = section.getDouble("distance", 2.5);
        this.updatePlaceholders = section.getBoolean("update-placeholders", true);
        this.onCloseActions = section.getStringList("on-close");

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
            player.setVelocity(new org.bukkit.util.Vector(0, 0, 0));

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (closed || !player.isOnline()) return;

                camera.spawnAt(plugin, fixedLoc, () -> {
                    player.teleport(fixedLoc);
                    player.setVelocity(new org.bukkit.util.Vector(0, 0, 0));
                    finishSetup(section);
                });
            }, 5L);
        });
    }

    private void finishSetup(ConfigurationSection section) {
        this.basis = new CameraBasis(spawnYaw, spawnPitch);
        this.menuOrigin = basis.getMenuOrigin(camera.getEyeLocation(), menuDistance);

        spawnCursor();

        ConfigurationSection areasSection = section.getConfigurationSection("areas");
        if (areasSection != null) {
            for (String areaKey : areasSection.getKeys(false)) {
                ConfigurationSection areaConf = areasSection.getConfigurationSection(areaKey);
                if (areaConf == null) continue;

                String typeStr = areaConf.getString("type", "STATIC").toUpperCase();
                AreaType areaType = AreaType.STATIC;
                try { areaType = AreaType.valueOf(typeStr); } catch (IllegalArgumentException ignored) {}

                double aX = areaConf.getDouble("x", 0);
                double aY = areaConf.getDouble("y", 0);
                double aSizeX = areaConf.getDouble("size-x", 6.0);
                double aSizeY = areaConf.getDouble("size-y", 4.0);
                int aUpdateTicks = areaConf.getInt("update-ticks", 20);

                String openAnimStr = areaConf.getString("open-animation", "NONE").toUpperCase();
                String closeAnimStr = areaConf.getString("close-animation", "NONE").toUpperCase();
                AnimationType openAnim = AnimationType.NONE;
                AnimationType closeAnim = AnimationType.NONE;
                try { openAnim = AnimationType.valueOf(openAnimStr); } catch (IllegalArgumentException ignored) {}
                try { closeAnim = AnimationType.valueOf(closeAnimStr); } catch (IllegalArgumentException ignored) {}
                int animDuration = areaConf.getInt("animation-duration", 10);

                MenuArea menuArea = new MenuArea(areaKey, areaType, aX, aY, aSizeX, aSizeY,
                        aUpdateTicks, openAnim, closeAnim, animDuration);

                if (areaType == AreaType.SCROLL) {
                    hasScrollArea = true;
                }

                ConfigurationSection comps = areaConf.getConfigurationSection("components");
                if (comps != null) {
                    for (String compKey : comps.getKeys(false)) {
                        ConfigurationSection c = comps.getConfigurationSection(compKey);
                        double cx = c.getDouble("x");
                        double cy = c.getDouble("y");
                        double cz = c.getDouble("z", 1.0);
                        double worldX = aX + cx;
                        double worldY = aY + cy;
                        Location loc = calculateComponentLocation(worldX, worldY, cz);
                        MenuButton btn = renderer.createComponent(player, c.getString("type", "BUTTON").toUpperCase(), c,
                                loc, worldX, worldY, this::close);
                        if (btn != null) {
                            btn.setBaseZ(cz);
                            btn.setAreaLocalX(cx);
                            btn.setAreaLocalY(cy);
                            btn.checkViewRequirements();
                            menuArea.addButton(compKey, btn);
                        }
                    }
                }

                areas.add(menuArea);
                areaMap.put(areaKey, menuArea);
            }
        }

        // Save hotbar if any scroll area exists
        if (hasScrollArea) {
            savedHotbar = new ItemStack[9];
            for (int i = 0; i < 9; i++) {
                savedHotbar[i] = player.getInventory().getItem(i);
                player.getInventory().setItem(i, null);
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

        List<String> onOpen = section.getStringList("on-open");
        if (!onOpen.isEmpty())
            renderer.getActionProcessor().processList(player, onOpen, this::close);

        // Play open animations
        for (MenuArea area : areas) {
            area.playOpenAnimation(player, this);
        }

        this.animator = new MenuAnimator(this, player, menuDistance);
        this.animator.runTaskTimer(plugin, 0L, 1L);

        this.updateTaskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (closed || !player.isOnline()) return;
            if (!updatePlaceholders) return;
            for (MenuArea area : areas) {
                area.tickUpdateCounter();
                if (area.shouldUpdate()) {
                    area.updatePlaceholders(player);
                }
            }
        }, 1L, 1L).getTaskId();
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

    public void openFromSection(String menuId, ConfigurationSection section) {
        this.menuId = menuId;
        if (section == null) return;

        this.oldLocation = player.getLocation().clone();
        this.couldFlyBefore = player.getAllowFlight();
        this.menuDistance = section.getDouble("distance", 2.5);
        this.updatePlaceholders = section.getBoolean("update-placeholders", true);
        this.onCloseActions = section.getStringList("on-close");

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

    public void close() {
        close(false);
    }

    public void close(boolean immediate) {
        if (closed)
            return;
        closed = true;

        Bukkit.getPluginManager().callEvent(new MenuCloseEvent(player, menuId));

        if (onCloseActions != null && !onCloseActions.isEmpty() && player.isOnline())
            renderer.getActionProcessor().processList(player, onCloseActions, null);

        // Check if any area has close animations
        boolean hasCloseAnimations = areas.stream()
                .anyMatch(a -> a.getCloseAnimation() != AnimationType.NONE);

        if (!immediate && hasCloseAnimations && player.isOnline()) {
            int maxDuration = 0;
            for (MenuArea area : areas) {
                if (area.getCloseAnimation() != AnimationType.NONE) {
                    area.playCloseAnimation(player, this, null);
                    maxDuration = Math.max(maxDuration, area.getAnimationDuration());
                }
            }
            Bukkit.getScheduler().runTaskLater(plugin, this::finalCleanup, maxDuration + 2L);
        } else {
            finalCleanup();
        }
    }

    private void finalCleanup() {
        if (updateTaskId != -1) {
            Bukkit.getScheduler().cancelTask(updateTaskId);
        }
        if (animator != null)
            animator.cancel();
        camera.remove();
        if (cursor != null)
            cursor.remove();
        for (MenuArea area : areas) {
            area.removeAll();
        }
        areas.clear();
        areaMap.clear();
        plugin.getMenuManager().removeMenu(player.getUniqueId());

        // Restore hotbar
        if (savedHotbar != null && player.isOnline()) {
            for (int i = 0; i < 9; i++) {
                player.getInventory().setItem(i, savedHotbar[i]);
            }
            savedHotbar = null;
        }

        if (oldLocation != null && player.isOnline())
            player.teleport(oldLocation);
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

    public CameraBasis getBasis() { return basis; }
    public MenuCamera getCamera() { return camera; }
    public MenuCursor getCursor() { return cursor; }
    public Location getMenuOrigin() { return menuOrigin; }
    public float getSpawnYaw() { return spawnYaw; }
    public float getSpawnPitch() { return spawnPitch; }
    public double getMenuDistance() { return menuDistance; }
    public boolean shouldUpdatePlaceholders() { return updatePlaceholders; }
    public String getMenuId() { return menuId; }
    public Player getPlayer() { return player; }
    public List<MenuArea> getAreas() { return areas; }
    public Map<String, MenuArea> getAreaMap() { return areaMap; }
    public boolean hasScrollArea() { return hasScrollArea; }

    /** Flattened view of all buttons across all areas (for MenuAnimator formula anims). */
    public List<MenuButton> getButtons() {
        List<MenuButton> all = new ArrayList<>();
        for (MenuArea area : areas) {
            all.addAll(area.getButtons());
        }
        return all;
    }

    /** Flattened button map across all areas. */
    public Map<String, MenuButton> getButtonMap() {
        Map<String, MenuButton> all = new LinkedHashMap<>();
        for (MenuArea area : areas) {
            all.putAll(area.getButtonMap());
        }
        return all;
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
