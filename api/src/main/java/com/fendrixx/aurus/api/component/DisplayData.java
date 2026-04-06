package com.fendrixx.aurus.api.component;

public record DisplayData(String text,
                          int bgColor,
                          boolean shadow,
                          Alignment alignment,
                          byte billboard) {
  // private String text = "";
  //  private int bgColor = 0x40000000;
  //  private boolean shadow = false;
  //  private String alignment = "CENTER";
  //  private byte billboard = 0;
  public DisplayData() {
    this("", 0x40000000, false, Alignment.CENTER, (byte) 0);
  }

  public DisplayData(String text) {
    this(text, 0x40000000, false, Alignment.CENTER, (byte) 0);
  }

  public DisplayData(String text, int bgColor) {
    this(text, bgColor, false, Alignment.CENTER, (byte) 0);
  }

  public DisplayData(String text, int bgColor, boolean shadow) {
    this(text, bgColor, shadow, Alignment.CENTER, (byte) 0);
  }

  public DisplayData(String text, int bgColor, boolean shadow, Alignment alignment) {
    this(text, bgColor, shadow, alignment, (byte) 0);
  }

  public enum Alignment {
    CENTER,
    LEFT,
    RIGHT;
  }
}
