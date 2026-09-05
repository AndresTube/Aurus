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

                Expression scaleExpr = compile(anim, "scale-formula", btn);
                Expression rotExpr = compile(anim, "rotation-formula", btn);
                Expression xExpr = compile(anim, "x-formula", btn);
                Expression yExpr = compile(anim, "y-formula", btn);
                Expression zExpr = compile(anim, "z-formula", btn);

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

    private Expression compile(ConfigurationSection animation, String key, MenuButton button) {
        if (!animation.contains(key)) return null;
        String formula = animation.getString(key);
        Expression expression = MathUtil.compile(formula);
        if (expression == null && formula != null && !formula.isBlank()) {
            menu.getPlayer().sendMessage("<red>Aurus: invalid animation formula <yellow>" + key + "</yellow> on <white>" + button.getType() + "</white>. Animation disabled.");
        }
        return expression;
    }

    private double evaluate(Expression expression, double t) {
        if (expression == null) return 0;
        try {
            return expression.setVariable("t", t).evaluate();
        } catch (RuntimeException ignored) {
            return 0;
        }
    }

    @Override
    public void run() {
        if (menu.getCamera().getTripod() == null || !player.isOnline()) {
            menu.close();
            return;
        }

        for (MenuArea area : menu.getAreas()) {
            area.getAnimator().tick();
        }

        if (hasAnimations) {
            ticks += 0.05;
            for (AnimatedEntry e : animatedEntries) {
                if (e.hasPosition) {
                    double rx = evaluate(e.xExpr, ticks);
                    double ry = evaluate(e.yExpr, ticks);
                    double rz = evaluate(e.zExpr, ticks);
                    Location loc = menu.calculateComponentLocation(e.baseX + rx, e.baseY + ry, e.baseZ + rz);
                    FakeEntityFactory.teleportEntity(player, e.button.getEntityId(), loc);
                }
                if (e.hasTransform) {
                    float scale = e.scaleExpr != null ? (float) evaluate(e.scaleExpr, ticks) : e.baseSize;
                    if (!Float.isFinite(scale)) scale = e.baseSize;
                    scale = Math.max(0f, scale);
                    float rotZ = e.rotExpr != null ? (float) evaluate(e.rotExpr, ticks) : 0;
                    if (!Float.isFinite(rotZ)) rotZ = 0;
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

        // Placeholder updates are handled by Menu's dedicated update task.
    }
}
