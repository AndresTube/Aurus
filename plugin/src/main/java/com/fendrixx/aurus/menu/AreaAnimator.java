package com.fendrixx.aurus.menu;

import com.fendrixx.aurus.api.component.AnimationType;
import com.fendrixx.aurus.packets.FakeEntityFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AreaAnimator {
    private int duration;
    private int elapsed;
    private boolean active;
    private boolean closing;
    private Runnable onComplete;

    public void startOpen(AnimationType animType, int duration, Player player, MenuArea area, Menu menu) {
        if (animType == AnimationType.NONE) return;
        this.duration = duration;
        this.elapsed = 0;
        this.active = true;
        this.closing = false;

        // Set initial state this tick
        for (MenuButton btn : area.getButtons()) {
            int entityId = btn.getEntityId();
            if (animType == AnimationType.SCALE) {
                FakeEntityFactory.setDisplayScale(player, entityId, 0f, 0f, 0f);
            } else {
                float offsetX = 0, offsetY = 0;
                switch (animType) {
                    case LEFT -> offsetX = -2f;
                    case RIGHT -> offsetX = 2f;
                    case UP -> offsetY = 2f;
                    case DOWN -> offsetY = -2f;
                    default -> {}
                }
                FakeEntityFactory.setDisplayTranslationInterpolated(player, entityId,
                        offsetX, offsetY, 0, 0);
            }
        }

        // Schedule interpolation target 1 tick later so the client processes the initial state first
        Bukkit.getScheduler().runTaskLater(
                Bukkit.getPluginManager().getPlugin("Aurus"), () -> {
                    if (!player.isOnline()) return;
                    for (MenuButton btn : area.getButtons()) {
                        int entityId = btn.getEntityId();
                        if (animType == AnimationType.SCALE) {
                            FakeEntityFactory.setDisplayScaleInterpolated(player, entityId,
                                    (float) btn.getConfig().getDouble("size", 1.0), duration);
                        } else {
                            FakeEntityFactory.setDisplayTranslationInterpolated(player, entityId,
                                    0, 0, 0, duration);
                        }
                    }
                }, 1L);
    }

    public void startClose(AnimationType animType, int duration, Player player, MenuArea area, Menu menu, Runnable onComplete) {
        if (animType == AnimationType.NONE) {
            if (onComplete != null) onComplete.run();
            return;
        }
        this.duration = duration;
        this.elapsed = 0;
        this.active = true;
        this.closing = true;
        this.onComplete = onComplete;

        for (MenuButton btn : area.getButtons()) {
            int entityId = btn.getEntityId();
            if (animType == AnimationType.SCALE) {
                FakeEntityFactory.setDisplayScaleInterpolated(player, entityId, 0f, duration);
            } else {
                float offsetX = 0, offsetY = 0;
                switch (animType) {
                    case LEFT -> offsetX = -2f;
                    case RIGHT -> offsetX = 2f;
                    case UP -> offsetY = 2f;
                    case DOWN -> offsetY = -2f;
                    default -> {}
                }
                FakeEntityFactory.setDisplayTranslationInterpolated(player, entityId,
                        offsetX, offsetY, 0, duration);
            }
        }
    }

    public void tick() {
        if (!active) return;
        elapsed++;
        if (elapsed >= duration) {
            active = false;
            if (closing && onComplete != null) {
                onComplete.run();
            }
        }
    }

    public boolean isActive() { return active; }
    public boolean isClosing() { return closing; }
}
