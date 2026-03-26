package com.fendrixx.aurus.api.menu;

import com.fendrixx.aurus.api.component.AnimationType;
import com.fendrixx.aurus.api.component.AreaType;
import com.fendrixx.aurus.api.component.ComponentType;

import java.util.function.Consumer;

public class AreaBuilder {
    private final AreaData.Builder dataBuilder;

    AreaBuilder(String id) {
        this.dataBuilder = AreaData.builder(id);
    }

    public AreaBuilder type(AreaType type) { dataBuilder.type(type); return this; }
    public AreaBuilder position(double x, double y) { dataBuilder.position(x, y); return this; }
    public AreaBuilder x(double x) { dataBuilder.x(x); return this; }
    public AreaBuilder y(double y) { dataBuilder.y(y); return this; }
    public AreaBuilder size(double sizeX, double sizeY) { dataBuilder.size(sizeX, sizeY); return this; }
    public AreaBuilder sizeX(double sizeX) { dataBuilder.sizeX(sizeX); return this; }
    public AreaBuilder sizeY(double sizeY) { dataBuilder.sizeY(sizeY); return this; }
    public AreaBuilder updateTicks(int ticks) { dataBuilder.updateTicks(ticks); return this; }
    public AreaBuilder openAnimation(AnimationType anim) { dataBuilder.openAnimation(anim); return this; }
    public AreaBuilder closeAnimation(AnimationType anim) { dataBuilder.closeAnimation(anim); return this; }
    public AreaBuilder animationDuration(int duration) { dataBuilder.animationDuration(duration); return this; }

    public AreaBuilder addComponent(String id, Consumer<ComponentBuilder> config) {
        ComponentBuilder builder = new ComponentBuilder(id);
        config.accept(builder);
        dataBuilder.addComponent(id, builder.build());
        return this;
    }

    public AreaBuilder addComponent(String id, ComponentType type, Consumer<ComponentBuilder> config) {
        ComponentBuilder builder = new ComponentBuilder(id, type);
        config.accept(builder);
        dataBuilder.addComponent(id, builder.build());
        return this;
    }

    AreaData build() {
        return dataBuilder.build();
    }
}
