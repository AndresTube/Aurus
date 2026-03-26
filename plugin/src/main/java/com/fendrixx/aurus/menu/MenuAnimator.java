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
    private final List<AnimatedEntry> animatedEntries;
    private final boolean hasAnimations;
    private double ticks = 0;

    private record AnimatedEntry(
            MenuButton button,
            float baseSize,
            double baseX, double baseY, double baseZ,
            Expression scaleExpr,
            Expression rotExpr,
            Expression xExpr, Expression yExpr, Expression zExpr,
            boolean hasTransform, boolean hasPosition
    ) {}

    public MenuAnimator(Menu menu, Player player, double distance) {
        this.menu = menu;
        this.player = player;
        this.distance = distance;

        List<AnimatedEntry> entries = new ArrayList<>();
        for (MenuArea area : menu.getAreas()) {
            for (MenuButton btn : area.getButtons()) {
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
                            btn.getBaseX(), btn.getBaseY(), btn.getBaseZ(),
                            scaleExpr, rotExpr, xExpr, yExpr, zExpr,
                            hasTransform, hasPosition));
                }
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

        // Tick area animators
        for (MenuArea area : menu.getAreas()) {
            area.getAnimator().tick();
        }

        // Formula animations
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

        // Cursor position
        Location playerLoc = player.getLocation();
        Location newCursorPos = menu.getBasis().getCursorLocation(
                menu.getMenuOrigin(),
                playerLoc.getYaw(),
                playerLoc.getPitch(),
                distance);
        menu.getCursor().teleport(newCursorPos);

        // Hover detection per area
        double[] local = menu.getBasis().getCursorXY(playerLoc.getYaw(), playerLoc.getPitch(), distance);
        double cursorX = local[0];
        double cursorY = local[1];

        for (MenuArea area : menu.getAreas()) {
            List<MenuButton> hoverable = area.getHoverableButtons();
            if (hoverable.isEmpty()) continue;

            boolean cursorInArea = area.containsCursor(cursorX, cursorY);
            for (MenuButton btn : hoverable) {
                if (!cursorInArea) {
                    btn.setHovered(false);
                    continue;
                }
                double effectiveX = area.getAreaX() + btn.getAreaLocalX();
                double effectiveY = area.getAreaY() + btn.getAreaLocalY() + area.getScrollOffset();
                double dx = cursorX - effectiveX;
                double dy = cursorY - effectiveY;
                btn.setHovered(Math.abs(dx) < btn.getHitboxHalfW() && Math.abs(dy) < btn.getHitboxHalfH());
            }
        }

        // Per-area placeholder updates
        if (menu.shouldUpdatePlaceholders()) {
            for (MenuArea area : menu.getAreas()) {
                area.tickUpdateCounter();
                if (area.shouldUpdate()) {
                    area.updatePlaceholders(player);
                }
            }
        }
    }
}
