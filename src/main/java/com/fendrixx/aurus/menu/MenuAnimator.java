package com.fendrixx.aurus.menu;
import com.fendrixx.aurus.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import java.util.List;
public class MenuAnimator extends BukkitRunnable {
    private final Menu menu;
    private final Player player;
    private final List<MenuButton> buttons;
    private final double distance;
    private final int updateDelay;
    private double ticks = 0;
    private int updateCounter = 0;
    public MenuAnimator(Menu menu, Player player, List<MenuButton> buttons, double distance, int updateDelay) {
        this.menu = menu;
        this.player = player;
        this.buttons = buttons;
        this.distance = distance;
        this.updateDelay = updateDelay;
    }
    @Override
    public void run() {
        if (!player.isOnline()) {
            Bukkit.getScheduler().runTask(menu.getPlugin(), menu::close);
            this.cancel();
            return;
        }
        ticks += 0.05;
        Location baseLoc = menu.getCameraLocation();
        float yaw = baseLoc.getYaw();
        for (MenuButton btn : buttons) {
            ConfigurationSection conf = btn.getConfig();
            Display display = btn.getDisplay();
            if (!display.isValid()) continue;
            double x = conf.getDouble("x");
            double y = conf.getDouble("y");
            if (conf.contains("animations")) {
                ConfigurationSection anim = conf.getConfigurationSection("animations");
                x += anim.contains("x-formula") ? MathUtil.evaluate(anim.getString("x-formula"), ticks) : 0;
                y += anim.contains("y-formula") ? MathUtil.evaluate(anim.getString("y-formula"), ticks) : 0;
            }
            Location finalTarget = MathUtil.getComponentLocation(baseLoc, yaw, distance, x, y);
            Location eyeLoc = player.getEyeLocation();
            Location newCursorPos = MathUtil.getCursorLocation(eyeLoc, yaw,
                    player.getLocation().getYaw(), player.getLocation().getPitch(), distance);
            Bukkit.getScheduler().runTask(menu.getPlugin(), () -> {
                if (!display.isValid()) return;
                display.teleport(finalTarget);
                if (conf.contains("animations")) {
                    ConfigurationSection anim = conf.getConfigurationSection("animations");
                    Transformation trans = display.getTransformation();
                    boolean changed = false;
                    if (anim.contains("scale-formula")) {
                        float s = (float) MathUtil.evaluate(anim.getString("scale-formula"), ticks);
                        trans.getScale().set(s, s, s);
                        changed = true;
                    }
                    if (anim.contains("rotation-formula")) {
                        float r = (float) MathUtil.evaluate(anim.getString("rotation-formula"), ticks);
                        trans.getLeftRotation().rotationXYZ(0, 0, (float) Math.toRadians(r));
                        changed = true;
                    }
                    if (changed) display.setTransformation(trans);
                }
                if (menu.getCursor() != null && menu.getCursor().isValid()) {
                    menu.getCursor().teleport(newCursorPos);
                }
            });
            Location cam = menu.getCameraLocation();
            if (player.getLocation().distanceSquared(cam) > 0.04) {
                player.teleport(cam);
            }
        }
        updateCounter++;
        if (updateCounter >= updateDelay) {
            Bukkit.getScheduler().runTask(menu.getPlugin(), () -> {
                buttons.forEach(b -> b.updateText(player));
            });
            updateCounter = 0;
        }
    }
}