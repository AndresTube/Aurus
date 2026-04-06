package com.fendrixx.aurus.api.component.builder;

import com.fendrixx.aurus.api.component.SubComponent;
import com.fendrixx.aurus.api.packet.Polygon3D;
import com.fendrixx.aurus.api.packet.Vector3D;
import com.fendrixx.aurus.api.utilitary.Builder;

public abstract class SubComponentBuilder<T extends SubComponent> implements Builder<T> {
  private Vector3D vector3D = new Vector3D(0, 0);
  private Polygon3D polygon3D = new Polygon3D(0, 0, 0);

  public SubComponentBuilder<T> vector3D(Vector3D vector3D) {
    this.vector3D = vector3D;
    return this;
  }

  public SubComponentBuilder<T> polygon3D(Polygon3D polygon3D) {
    this.polygon3D = polygon3D;
    return this;
  }

  public Vector3D vector3D() {
    return vector3D;
  }

  public Polygon3D polygon3D() {
    return polygon3D;
  }
}
