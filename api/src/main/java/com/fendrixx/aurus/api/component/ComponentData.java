package com.fendrixx.aurus.api.component;

import com.fendrixx.aurus.api.action.MenuAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComponentData {
    private final String id;
    private final ComponentType type;
    private final String text;
    private final double x;
    private final double y;
    private final double z;
    private final double size;
    private final boolean background;
    private final boolean shadow;
    private final String alignment;
    private final String material;
    private final int modelId;
    private final String entity;
    private final String skin;
    private final String nametag;
    private final String variableName;
    private final String fallbackMessage;
    private final String sound;
    private final float rotationX;
    private final float rotationY;
    private final float rotationZ;
    private final float headYaw;
    private final float headPitch;
    private final double hitboxWidth;
    private final double hitboxHeight;
    private final List<MenuAction> actions;
    private final HoverData hoverData;
    private final AnimationData animationData;

    private ComponentData(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.text = builder.text;
        this.x = builder.x;
        this.y = builder.y;
        this.z = builder.z;
        this.size = builder.size;
        this.background = builder.background;
        this.shadow = builder.shadow;
        this.alignment = builder.alignment;
        this.material = builder.material;
        this.modelId = builder.modelId;
        this.entity = builder.entity;
        this.skin = builder.skin;
        this.nametag = builder.nametag;
        this.variableName = builder.variableName;
        this.fallbackMessage = builder.fallbackMessage;
        this.sound = builder.sound;
        this.rotationX = builder.rotationX;
        this.rotationY = builder.rotationY;
        this.rotationZ = builder.rotationZ;
        this.headYaw = builder.headYaw;
        this.headPitch = builder.headPitch;
        this.hitboxWidth = builder.hitboxWidth;
        this.hitboxHeight = builder.hitboxHeight;
        this.actions = Collections.unmodifiableList(new ArrayList<>(builder.actions));
        this.hoverData = builder.hoverData;
        this.animationData = builder.animationData;
    }

    public String getId() { return id; }
    public ComponentType getType() { return type; }
    public String getText() { return text; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public double getSize() { return size; }
    public boolean hasBackground() { return background; }
    public boolean hasShadow() { return shadow; }
    public String getAlignment() { return alignment; }
    public String getMaterial() { return material; }
    public int getModelId() { return modelId; }
    public String getEntity() { return entity; }
    public String getSkin() { return skin; }
    public String getNametag() { return nametag; }
    public String getVariableName() { return variableName; }
    public String getFallbackMessage() { return fallbackMessage; }
    public String getSound() { return sound; }
    public float getRotationX() { return rotationX; }
    public float getRotationY() { return rotationY; }
    public float getRotationZ() { return rotationZ; }
    public float getHeadYaw() { return headYaw; }
    public float getHeadPitch() { return headPitch; }
    public double getHitboxWidth() { return hitboxWidth; }
    public double getHitboxHeight() { return hitboxHeight; }
    public List<MenuAction> getActions() { return actions; }
    public HoverData getHoverData() { return hoverData; }
    public AnimationData getAnimationData() { return animationData; }

    public static Builder builder(String id, ComponentType type) {
        return new Builder(id, type);
    }

    public static class Builder {
        private final String id;
        private ComponentType type;
        private String text = "";
        private double x = 0;
        private double y = 0;
        private double z = 1.0;
        private double size = 1.0;
        private boolean background = true;
        private boolean shadow = false;
        private String alignment = "CENTER";
        private String material = "STONE";
        private int modelId = -1;
        private String entity = "ZOMBIE";
        private String skin;
        private String nametag = "";
        private String variableName;
        private String fallbackMessage;
        private String sound = "minecraft:ui.button.click";
        private float rotationX = 0;
        private float rotationY = 0;
        private float rotationZ = 0;
        private float headYaw = 0;
        private float headPitch = 0;
        private double hitboxWidth = -1;
        private double hitboxHeight = -1;
        private final List<MenuAction> actions = new ArrayList<>();
        private HoverData hoverData;
        private AnimationData animationData;

        private Builder(String id, ComponentType type) {
            this.id = id;
            this.type = type;
        }

        public String getId() { return id; }
        public Builder type(ComponentType type) { this.type = type; return this; }
        public Builder text(String text) { this.text = text; return this; }
        public Builder position(double x, double y, double z) { this.x = x; this.y = y; this.z = z; return this; }
        public Builder x(double x) { this.x = x; return this; }
        public Builder y(double y) { this.y = y; return this; }
        public Builder z(double z) { this.z = z; return this; }
        public Builder size(double size) { this.size = size; return this; }
        public Builder background(boolean bg) { this.background = bg; return this; }
        public Builder shadow(boolean shadow) { this.shadow = shadow; return this; }
        public Builder alignment(String alignment) { this.alignment = alignment; return this; }
        public Builder material(String material) { this.material = material; return this; }
        public Builder modelId(int modelId) { this.modelId = modelId; return this; }
        public Builder entity(String entity) { this.entity = entity; return this; }
        public Builder skin(String skin) { this.skin = skin; return this; }
        public Builder nametag(String nametag) { this.nametag = nametag; return this; }
        public Builder variableName(String name) { this.variableName = name; return this; }
        public Builder fallbackMessage(String msg) { this.fallbackMessage = msg; return this; }
        public Builder sound(String sound) { this.sound = sound; return this; }
        public Builder rotation(float x, float y, float z) { this.rotationX = x; this.rotationY = y; this.rotationZ = z; return this; }
        public Builder headRotation(float yaw, float pitch) { this.headYaw = yaw; this.headPitch = pitch; return this; }
        public Builder hitbox(double width, double height) { this.hitboxWidth = width; this.hitboxHeight = height; return this; }
        public Builder action(MenuAction action) { this.actions.add(action); return this; }
        public Builder action(String actionStr) { this.actions.add(new MenuAction(actionStr)); return this; }
        public Builder hover(HoverData hover) { this.hoverData = hover; return this; }
        public Builder animation(AnimationData animation) { this.animationData = animation; return this; }

        public ComponentData build() { return new ComponentData(this); }
    }
}
