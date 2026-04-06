package com.fendrixx.aurus.api.component;

import com.fendrixx.aurus.api.component.builder.SubComponentBuilder;
import com.fendrixx.aurus.api.packet.Polygon3D;
import com.fendrixx.aurus.api.packet.Vector3D;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class SubComponent {
  protected final Polygon3D polygon3D;
  protected final Vector3D vector3D;
  protected final int entityId;

  public SubComponent(SubComponentBuilder<?> builder) {
    this(builder, EntityComponent.IDS.getAndIncrement());
  }

  public SubComponent(SubComponentBuilder<?> builder, int id) {
    this.entityId = id;
    this.vector3D = builder.vector3D();
    this.polygon3D = builder.polygon3D();
  }

  public Polygon3D polygon3D() {
    return polygon3D;
  }

  public Vector3D vector3D() {
    return vector3D;
  }

  public abstract void execute(Player player, ActionType type, Location location);
}