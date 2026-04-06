package com.fendrixx.aurus.api.component.implementation.subcomponent;

import com.fendrixx.aurus.api.component.ActionType;
import com.fendrixx.aurus.api.component.DisplayData;
import com.fendrixx.aurus.api.component.SubComponent;
import com.fendrixx.aurus.api.component.builder.HoverSubComponentBuilder;
import com.fendrixx.aurus.api.packet.PacketDispatcher;
import com.fendrixx.aurus.api.packet.PacketType;
import com.fendrixx.aurus.api.packet.PacketWrapper;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class HoverSubEntityComponent extends SubComponent {
  private final DisplayData displayData;
  private final UUID uuid;

  public HoverSubEntityComponent(HoverSubComponentBuilder builder) {
    super(builder);
    this.displayData = builder.displayData();
    this.uuid = builder.fakeUuid();
  }

  @Override
  public void execute(Player player, ActionType type, Location location) {
    if (type == ActionType.HOVER_START) {
      final Location newLoc = location.clone().add(vector3D.x(), vector3D.y(), vector3D.z());
      PacketDispatcher.send(player, PacketType.DISPLAY_ENTITY, new PacketWrapper(
        entityId,
        uuid,
        newLoc,
        polygon3D,
        displayData
      ));
    } else if (type == ActionType.HOVER_END) {
      PacketDispatcher.send(player, PacketType.DESTROY_ENTITY, new PacketWrapper(
        entityId,
        uuid));
    } else if (type == ActionType.DECONSTRUCT) {
      PacketDispatcher.send(player, PacketType.DESTROY_ENTITY, new PacketWrapper(
        entityId,
        uuid));
    }
  }
}
