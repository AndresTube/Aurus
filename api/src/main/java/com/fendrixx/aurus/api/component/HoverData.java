package com.fendrixx.aurus.api.component;

public class HoverData {
    private final ComponentType type;
    private final String text;
    private final double size;
    private final boolean background;
    private final boolean shadow;
    private final String alignment;
    private final String material;
    private final String entity;
    private final String skin;

    private HoverData(Builder builder) {
        this.type = builder.type;
        this.text = builder.text;
        this.size = builder.size;
        this.background = builder.background;
        this.shadow = builder.shadow;
        this.alignment = builder.alignment;
        this.material = builder.material;
        this.entity = builder.entity;
        this.skin = builder.skin;
    }

    public ComponentType getType() { return type; }
    public String getText() { return text; }
    public double getSize() { return size; }
    public boolean hasBackground() { return background; }
    public boolean hasShadow() { return shadow; }
    public String getAlignment() { return alignment; }
    public String getMaterial() { return material; }
    public String getEntity() { return entity; }
    public String getSkin() { return skin; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private ComponentType type;
        private String text;
        private double size = 1.0;
        private boolean background = true;
        private boolean shadow = false;
        private String alignment = "CENTER";
        private String material;
        private String entity;
        private String skin;

        public Builder type(ComponentType type) { this.type = type; return this; }
        public Builder text(String text) { this.text = text; return this; }
        public Builder size(double size) { this.size = size; return this; }
        public Builder background(boolean bg) { this.background = bg; return this; }
        public Builder shadow(boolean shadow) { this.shadow = shadow; return this; }
        public Builder alignment(String alignment) { this.alignment = alignment; return this; }
        public Builder material(String material) { this.material = material; return this; }
        public Builder entity(String entity) { this.entity = entity; return this; }
        public Builder skin(String skin) { this.skin = skin; return this; }
        public HoverData build() { return new HoverData(this); }
    }
}
