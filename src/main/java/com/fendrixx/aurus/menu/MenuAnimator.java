package com.fendrixx.aurus.menu;

import com.fendrixx.aurus.packets.FakeEntityFactory;
import com.fendrixx.aurus.util.MathUtil;

import net.objecthunter.exp4j.Expression;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class MenuAnimator extends BukkitRunnable {
    private final Menu menu;
    private final Player player;
    private final double distance;
    private final int updateDelay;
    private final List<MenuButton> allButtons;
    private final List<MenuButton> hoverableButtons;
    private final List<AnimatedEntry> animatedEntries;
    private final boolean hasAnimations;
    private double ticks = 0;
    private int updateCounter = 0;

    private record AnimatedEntry(
            MenuButton button,
            float baseSize,
            double baseX, double baseY, double baseZ,
            Expression scaleExpr,
            Expression rotExpr,
            Expression xExpr, Expression yExpr, Expression zExpr,
            boolean hasTransform, boolean hasPosition
    ) {}

    public MenuAnimator(Menu menu, Player player, List<MenuButton> buttons, double distance, int updateDelay) {
        this.menu = menu;
        this.player = player;
        this.allButtons = buttons;
        this.distance = distance;
        this.updateDelay = updateDelay;

        List<MenuButton> hoverable = new ArrayList<>();
        for (MenuButton btn : buttons) {
            String type = btn.getType();
            if (("BUTTON".equals(type) || "INPUT".equals(type)) && btn.getConfig().getConfigurationSection("hover") != null) {
                hoverable.add(btn);
            }
        }
        this.hoverableButtons = hoverable;

        List<AnimatedEntry> entries = new ArrayList<>();
        for (MenuButton btn : buttons) {
            ConfigurationSection conf = btn.getConfig();
            ConfigurationSection anim = conf.getConfigurationSection("animations");
            if (anim == null) continue;

            Expression scaleExpr = anim.contains("scale-formula") ? MathUtil.compile(anim.getString("scale-formula")) : null;
            Expression rotExpr = anim.contains("rotation-formula") ? MathUtil.compile(anim.getString("rotation-formula")) : null;
            Expression xExpr = anim.contains("x-formula") ? MathUtil.compile(anim.getString("x-formula")) : null;
            Expression yExpr = anim.contains("y-formula") ? MathUtil.compile(anim.getString("y-formula")) : null;
            Expression zExpr = anim.contains("z-formula") ? MathUtil.compile(anim.getString("z-formula")) : null;

            boolean hasTransform = scaleExpr != null || rotExpr != null;
            boolean hasPosition = xExpr != null || yExpr != null || zExpr != null;

            if (hasTransform || hasPosition) {
                entries.add(new AnimatedEntry(btn,
                        (float) conf.getDouble("size", 1.0),
                        conf.getDouble("x", 0.0), conf.getDouble("y", 0.0), btn.getBaseZ(),
                        scaleExpr, rotExpr, xExpr, yExpr, zExpr,
                        hasTransform, hasPosition));
            }
        }
        this.animatedEntries = entries;
        this.hasAnimations = !entries.isEmpty();
    }

    @Override
    public void run() {
        if (menu.getCamera().getTripod() == null || !player.isOnline()) {
            menu.close();
            return;
        }

        if (hasAnimations) {
            ticks += 0.05;
            for (AnimatedEntry e : animatedEntries) {
                if (e.hasPosition) {
                    double rx = e.xExpr != null ? e.xExpr.setVariable("t", ticks).evaluate() : 0;
                    double ry = e.yExpr != null ? e.yExpr.setVariable("t", ticks).evaluate() : 0;
                    double rz = e.zExpr != null ? e.zExpr.setVariable("t", ticks).evaluate() : 0;
                    Location loc = menu.calculateComponentLocation(e.baseX + rx, e.baseY + ry, e.baseZ + rz);
                    FakeEntityFactory.teleportEntity(player, e.button.getEntityId(), loc);
                }
                if (e.hasTransform) {
                    float scale = e.scaleExpr != null ? (float) e.scaleExpr.setVariable("t", ticks).evaluate() : e.baseSize;
                    float rotZ = e.rotExpr != null ? (float) e.rotExpr.setVariable("t", ticks).evaluate() : 0;
                    FakeEntityFactory.setDisplayTransform(player, e.button.getEntityId(), scale, 0, 0, rotZ);
                }
            }
        }

        Location playerLoc = player.getLocation();
        Location newCursorPos = menu.getBasis().getCursorLocation(
                menu.getMenuOrigin(),
                playerLoc.getYaw(),
                playerLoc.getPitch(),
                distance);
        menu.getCursor().teleport(newCursorPos);

        if (!hoverableButtons.isEmpty()) {
            double[] local = menu.getBasis().projectToPlane(playerLoc.getYaw(), playerLoc.getPitch());
            double cursorX = local[0] * distance;
            double cursorY = local[1] * distance;

            for (MenuButton btn : hoverableButtons) {
                double dx = cursorX - btn.getBaseX();
                double dy = cursorY - btn.getBaseY();
                btn.setHovered(Math.abs(dx) < btn.getHitboxHalfW() && Math.abs(dy) < btn.getHitboxHalfH());
            }
        }

        updateCounter++;
        if (updateCounter >= updateDelay) {
            if (menu.shouldUpdatePlaceholders()) {
                allButtons.forEach(b -> b.updateText(player));
            }
            updateCounter = 0;
        }
    }
}
