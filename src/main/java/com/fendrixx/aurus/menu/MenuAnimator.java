package com.fendrixx.aurus.menu;

import com.fendrixx.aurus.util.MathUtil;
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
        if (menu.getCamera().getTripod() == null || !player.isOnline()) {
            menu.close();
            return;
        }

        ticks += 0.05;

        for (MenuButton btn : buttons) {
            ConfigurationSection conf = btn.getConfig();
            ConfigurationSection anim = conf.getConfigurationSection("animations");
            if (anim == null) continue;

            Display display = btn.getDisplay();
            Transformation trans = display.getTransformation();
            boolean changed = false;

            if (anim.contains("x-formula") || anim.contains("y-formula")) {
                float offX = (float) MathUtil.evaluate(anim.getString("x-formula", "0"), ticks);
                float offY = (float) MathUtil.evaluate(anim.getString("y-formula", "0"), ticks);
                trans.getTranslation().set(offX, offY, 0);
                changed = true;
            }

            if (anim.contains("scale-formula")) {
                float s = (float) MathUtil.evaluate(anim.getString("scale-formula"), ticks);
                trans.getScale().set(s, s, s);
                changed = true;
            }

            if (anim.contains("rotation-formula")) {
                float r = (float) Math.toRadians(MathUtil.evaluate(anim.getString("rotation-formula"), ticks));
                trans.getLeftRotation().identity().rotateZ(r);
                changed = true;
            }

            if (changed) {
                display.setInterpolationDuration(1);
                display.setInterpolationDelay(0);
                display.setTransformation(trans);
            }
        }


        Location newPos = MathUtil.getCursorLocation(menu.getCamera().getEyeLocation(), menu.getCamera().getLocation().getYaw(),
                player.getLocation().getYaw(), player.getLocation().getPitch(), distance);
        menu.getCursor().teleport(newPos);

        updateCounter++;
        if (updateCounter >= updateDelay) {
            buttons.forEach(b -> b.updateText(player));
            updateCounter = 0;
        }
    }
}