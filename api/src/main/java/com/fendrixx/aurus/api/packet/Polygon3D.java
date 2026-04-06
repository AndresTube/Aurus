package com.fendrixx.aurus.api.packet;

public record Polygon3D(float rotX, float rotY, float rotZ, float scale) {
  public Polygon3D(float rotX, float rotY, float rotZ) {
    this(rotX, rotY, rotZ, 1.0F);
  }
}
