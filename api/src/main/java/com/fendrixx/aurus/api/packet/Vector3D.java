package com.fendrixx.aurus.api.packet;

/**
 *
 * @param x - x coord
 * @param y - y coord
 * @param z - z is the distance from the camera
 */
public record Vector3D(double x, double y, double z) {
  public Vector3D(double x, double y) {
    this(x, y, 1.0);
  }
}
