package com.fendrixx.aurus.api.menu;

import com.fendrixx.aurus.api.action.MenuAction;
import com.fendrixx.aurus.api.component.AnimationData;
import com.fendrixx.aurus.api.component.ComponentData;
import com.fendrixx.aurus.api.component.ComponentType;
import com.fendrixx.aurus.api.component.HoverData;

import java.util.function.Consumer;

public class ComponentBuilder {
    private final ComponentData.Builder dataBuilder;

    ComponentBuilder(String id) {
        this.dataBuilder = ComponentData.builder(id, ComponentType.TEXT);
    }

    ComponentBuilder(String id, ComponentType type) {
        this.dataBuilder = ComponentData.builder(id, type);
    }

    public ComponentBuilder type(ComponentType type) { dataBuilder.type(type); return this; }
    public ComponentBuilder text(String text) { dataBuilder.text(text); return this; }
    public ComponentBuilder position(double x, double y, double z) { dataBuilder.position(x, y, z); return this; }
    public ComponentBuilder x(double x) { dataBuilder.x(x); return this; }
    public ComponentBuilder y(double y) { dataBuilder.y(y); return this; }
    public ComponentBuilder z(double z) { dataBuilder.z(z); return this; }
    public ComponentBuilder size(double size) { dataBuilder.size(size); return this; }
    public ComponentBuilder background(boolean bg) { dataBuilder.background(bg); return this; }
    public ComponentBuilder shadow(boolean shadow) { dataBuilder.shadow(shadow); return this; }
    public ComponentBuilder alignment(String alignment) { dataBuilder.alignment(alignment); return this; }
    public ComponentBuilder material(String material) { dataBuilder.material(material); return this; }
    public ComponentBuilder modelId(int modelId) { dataBuilder.modelId(modelId); return this; }
    public ComponentBuilder entity(String entity) { dataBuilder.entity(entity); return this; }
    public ComponentBuilder skin(String skin) { dataBuilder.skin(skin); return this; }
    public ComponentBuilder nametag(String nametag) { dataBuilder.nametag(nametag); return this; }
    public ComponentBuilder variableName(String name) { dataBuilder.variableName(name); return this; }
    public ComponentBuilder fallbackMessage(String msg) { dataBuilder.fallbackMessage(msg); return this; }
    public ComponentBuilder sound(String sound) { dataBuilder.sound(sound); return this; }
    public ComponentBuilder rotation(float x, float y, float z) { dataBuilder.rotation(x, y, z); return this; }
    public ComponentBuilder headRotation(float yaw, float pitch) { dataBuilder.headRotation(yaw, pitch); return this; }
    public ComponentBuilder hitbox(double width, double height) { dataBuilder.hitbox(width, height); return this; }

    public ComponentBuilder action(String actionStr) { dataBuilder.action(actionStr); return this; }
    public ComponentBuilder action(MenuAction action) { dataBuilder.action(action); return this; }

    public ComponentBuilder hover(Consumer<HoverData.Builder> config) {
        HoverData.Builder hoverBuilder = HoverData.builder();
        config.accept(hoverBuilder);
        dataBuilder.hover(hoverBuilder.build());
        return this;
    }

    public ComponentBuilder hover(HoverData hoverData) { dataBuilder.hover(hoverData); return this; }

    public ComponentBuilder animation(Consumer<AnimationData.Builder> config) {
        AnimationData.Builder animBuilder = AnimationData.builder();
        config.accept(animBuilder);
        dataBuilder.animation(animBuilder.build());
        return this;
    }

    public ComponentBuilder animation(AnimationData animationData) { dataBuilder.animation(animationData); return this; }

    ComponentData build() {
        return dataBuilder.build();
    }
}
