package com.fendrixx.aurus.menu;

import com.fendrixx.aurus.api.component.AnimationType;
import com.fendrixx.aurus.api.component.AreaType;
import com.fendrixx.aurus.packets.FakeEntityFactory;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MenuArea {
    private final String id;
    private final AreaType type;
    private final double areaX;
    private final double areaY;
    private final double sizeX;
    private final double sizeY;
    private final int updateTicks;
    private final AnimationType openAnimation;
    private final AnimationType closeAnimation;
    private final int animationDuration;
    private final List<MenuButton> buttons = new ArrayList<>();
    private final Map<String, MenuButton> buttonMap = new LinkedHashMap<>();
    private final AreaAnimator animator = new AreaAnimator();
    private double scrollOffset = 0;
    private int updateCounter = 0;

    public MenuArea(String id, AreaType type, double areaX, double areaY,
                    double sizeX, double sizeY, int updateTicks,
                    AnimationType openAnimation, AnimationType closeAnimation,
                    int animationDuration) {
        this.id = id;
        this.type = type;
        this.areaX = areaX;
        this.areaY = areaY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.updateTicks = updateTicks;
        this.openAnimation = openAnimation;
        this.closeAnimation = closeAnimation;
        this.animationDuration = animationDuration;
    }

    public void addButton(String key, MenuButton btn) {
        buttons.add(btn);
        buttonMap.put(key, btn);
    }

    public boolean containsCursor(double cx, double cy) {
        return Math.abs(cx - areaX) < sizeX / 2.0 && Math.abs(cy - areaY) < sizeY / 2.0;
    }

    public boolean isComponentVisible(MenuButton btn) {
        if (type == AreaType.SCROLL) {
            double localY = btn.getAreaLocalY() + scrollOffset;
            return localY >= -sizeY / 2.0 && localY <= sizeY / 2.0;
        } else if (type == AreaType.SCROLL_HORIZONTAL) {
            double localX = btn.getAreaLocalX() + scrollOffset;
            return localX >= -sizeX / 2.0 && localX <= sizeX / 2.0;
        }
        return true;
    }

    public void scroll(double delta, Menu menu, Player player) {
        if (type != AreaType.SCROLL && type != AreaType.SCROLL_HORIZONTAL) return;
        scrollOffset += delta * 0.5;

        for (MenuButton btn : buttons) {
            double worldX = areaX + btn.getAreaLocalX() + (type == AreaType.SCROLL_HORIZONTAL ? scrollOffset : 0);
            double worldY = areaY + btn.getAreaLocalY() + (type == AreaType.SCROLL ? scrollOffset : 0);
            Location loc = menu.calculateComponentLocation(worldX, worldY, btn.getBaseZ());
            FakeEntityFactory.teleportEntity(player, btn.getEntityId(), loc);

            boolean visible = isComponentVisible(btn);
            btn.setVisible(visible, player);
        }
    }

    public void updatePlaceholders(Player player) {
        for (MenuButton btn : buttons) {
            btn.checkViewRequirements();
            if (isComponentVisible(btn)) {
                btn.updateText(player);
            }
        }
    }

    public void tickUpdateCounter() {
        updateCounter++;
    }

    public boolean shouldUpdate() {
        if (updateCounter >= updateTicks) {
            updateCounter = 0;
            return true;
        }
        return false;
    }

    public List<MenuButton> getVisibleButtons() {
        return buttons.stream().filter(this::isComponentVisible).collect(Collectors.toList());
    }

    public List<MenuButton> getHoverableButtons() {
        List<MenuButton> hoverable = new ArrayList<>();
        for (MenuButton btn : buttons) {
            if (!isComponentVisible(btn)) continue;
            String t = btn.getType();
            if (("BUTTON".equals(t) || "INPUT".equals(t)) && btn.getConfig().getConfigurationSection("hover") != null) {
                hoverable.add(btn);
            }
        }
        return hoverable;
    }

    public void removeAll() {
        for (MenuButton btn : buttons) {
            btn.remove();
        }
        buttons.clear();
        buttonMap.clear();
    }

    public void playOpenAnimation(Player player, Menu menu) {
        animator.startOpen(openAnimation, animationDuration, player, this, menu);
    }

    public void playCloseAnimation(Player player, Menu menu, Runnable onComplete) {
        animator.startClose(closeAnimation, animationDuration, player, this, menu, onComplete);
    }

    public String getId() { return id; }
    public AreaType getType() { return type; }
    public double getAreaX() { return areaX; }
    public double getAreaY() { return areaY; }
    public double getSizeX() { return sizeX; }
    public double getSizeY() { return sizeY; }
    public int getUpdateTicks() { return updateTicks; }
    public AnimationType getOpenAnimation() { return openAnimation; }
    public AnimationType getCloseAnimation() { return closeAnimation; }
    public int getAnimationDuration() { return animationDuration; }
    public List<MenuButton> getButtons() { return buttons; }
    public Map<String, MenuButton> getButtonMap() { return buttonMap; }
    public AreaAnimator getAnimator() { return animator; }
    public double getScrollOffset() { return scrollOffset; }
    public boolean hasScrollArea() { return type == AreaType.SCROLL || type == AreaType.SCROLL_HORIZONTAL; }
}
