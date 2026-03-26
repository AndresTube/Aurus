package com.fendrixx.aurus.api.component;

public class AnimationData {
    private final String scaleFormula;
    private final String rotationFormula;
    private final String xFormula;
    private final String yFormula;
    private final String zFormula;

    private AnimationData(Builder builder) {
        this.scaleFormula = builder.scaleFormula;
        this.rotationFormula = builder.rotationFormula;
        this.xFormula = builder.xFormula;
        this.yFormula = builder.yFormula;
        this.zFormula = builder.zFormula;
    }

    public String getScaleFormula() { return scaleFormula; }
    public String getRotationFormula() { return rotationFormula; }
    public String getXFormula() { return xFormula; }
    public String getYFormula() { return yFormula; }
    public String getZFormula() { return zFormula; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String scaleFormula;
        private String rotationFormula;
        private String xFormula;
        private String yFormula;
        private String zFormula;

        public Builder scaleFormula(String formula) { this.scaleFormula = formula; return this; }
        public Builder rotationFormula(String formula) { this.rotationFormula = formula; return this; }
        public Builder xFormula(String formula) { this.xFormula = formula; return this; }
        public Builder yFormula(String formula) { this.yFormula = formula; return this; }
        public Builder zFormula(String formula) { this.zFormula = formula; return this; }
        public AnimationData build() { return new AnimationData(this); }
    }
}
