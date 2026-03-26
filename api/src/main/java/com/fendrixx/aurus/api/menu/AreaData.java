package com.fendrixx.aurus.api.menu;

import com.fendrixx.aurus.api.component.AnimationType;
import com.fendrixx.aurus.api.component.AreaType;
import com.fendrixx.aurus.api.component.ComponentData;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class AreaData {
    private final String id;
    private final AreaType type;
    private final double x;
    private final double y;
    private final double sizeX;
    private final double sizeY;
    private final int updateTicks;
    private final AnimationType openAnimation;
    private final AnimationType closeAnimation;
    private final int animationDuration;
    private final Map<String, ComponentData> components;

    private AreaData(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.x = builder.x;
        this.y = builder.y;
        this.sizeX = builder.sizeX;
        this.sizeY = builder.sizeY;
        this.updateTicks = builder.updateTicks;
        this.openAnimation = builder.openAnimation;
        this.closeAnimation = builder.closeAnimation;
        this.animationDuration = builder.animationDuration;
        this.components = Collections.unmodifiableMap(new LinkedHashMap<>(builder.components));
    }

    public String getId() { return id; }
    public AreaType getType() { return type; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getSizeX() { return sizeX; }
    public double getSizeY() { return sizeY; }
    public int getUpdateTicks() { return updateTicks; }
    public AnimationType getOpenAnimation() { return openAnimation; }
    public AnimationType getCloseAnimation() { return closeAnimation; }
    public int getAnimationDuration() { return animationDuration; }
    public Map<String, ComponentData> getComponents() { return components; }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static class Builder {
        private final String id;
        private AreaType type = AreaType.STATIC;
        private double x = 0;
        private double y = 0;
        private double sizeX = 6.0;
        private double sizeY = 4.0;
        private int updateTicks = 20;
        private AnimationType openAnimation = AnimationType.NONE;
        private AnimationType closeAnimation = AnimationType.NONE;
        private int animationDuration = 10;
        private final Map<String, ComponentData> components = new LinkedHashMap<>();

        private Builder(String id) {
            this.id = id;
        }

        public Builder type(AreaType type) { this.type = type; return this; }
        public Builder x(double x) { this.x = x; return this; }
        public Builder y(double y) { this.y = y; return this; }
        public Builder position(double x, double y) { this.x = x; this.y = y; return this; }
        public Builder sizeX(double sizeX) { this.sizeX = sizeX; return this; }
        public Builder sizeY(double sizeY) { this.sizeY = sizeY; return this; }
        public Builder size(double sizeX, double sizeY) { this.sizeX = sizeX; this.sizeY = sizeY; return this; }
        public Builder updateTicks(int ticks) { this.updateTicks = ticks; return this; }
        public Builder openAnimation(AnimationType anim) { this.openAnimation = anim; return this; }
        public Builder closeAnimation(AnimationType anim) { this.closeAnimation = anim; return this; }
        public Builder animationDuration(int duration) { this.animationDuration = duration; return this; }
        public Builder addComponent(String id, ComponentData data) { this.components.put(id, data); return this; }

        public AreaData build() { return new AreaData(this); }
    }
}
