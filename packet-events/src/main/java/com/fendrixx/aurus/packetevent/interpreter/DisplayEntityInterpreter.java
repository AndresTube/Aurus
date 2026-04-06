package com.fendrixx.aurus.packetevent.interpreter;

import com.fendrixx.aurus.api.component.DisplayData;
import com.fendrixx.aurus.api.packet.PacketInterpreter;
import com.fendrixx.aurus.api.packet.PacketSender;
import com.fendrixx.aurus.api.packet.PacketWrapper;
import com.fendrixx.aurus.api.packet.Polygon3D;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Quaternion4f;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.fendrixx.aurus.packetevent.interpreter.CursorPacketInterpreter.MINI_MESSAGE;

public final class DisplayEntityInterpreter implements PacketInterpreter {
  @Override
  public void interpret(Player player, PacketWrapper wrapper, PacketSender adapter) {
    int entityId = wrapper.get(0, Integer.class);
    UUID uuid = wrapper.getOrDefault(1, UUID.randomUUID());
    org.bukkit.Location location = wrapper.get(3, org.bukkit.Location.class);;
    Polygon3D polygon3D = wrapper.get(4, Polygon3D.class);
    DisplayData displayData = wrapper.get(5, DisplayData.class);

    Object value = wrapper.getOrDefault(6, null);

    EntityType entityType = mapType(value);

    adapter.send(player, new WrapperPlayServerSpawnEntity(
      entityId,
      uuid,
      entityType,
      new Location(location.x(), location.y(), location.z(), location.getYaw(), location.getPitch()),
      location.getYaw(),
      0,
      null
    ));

    List<EntityData<?>> meta = new ArrayList<>();

    if (value instanceof ItemStack itemStack) {
      meta.add(new EntityData<>(15, EntityDataTypes.BYTE, (byte) 0));
      meta.add(new EntityData<>(23, EntityDataTypes.ITEMSTACK,
        SpigotConversionUtil.fromBukkitItemStack(itemStack)));
    } else {
      meta.add(new EntityData<>(15, EntityDataTypes.BYTE, displayData.billboard()));
      meta.add(new EntityData<>(8, EntityDataTypes.INT, 0));

      Component textComponent = MINI_MESSAGE.deserialize(displayData.text());

      meta.add(new EntityData<>(23, EntityDataTypes.ADV_COMPONENT, textComponent));
      meta.add(new EntityData<>(25, EntityDataTypes.INT, displayData.bgColor()));
      meta.add(new EntityData<>(26, EntityDataTypes.BYTE, (byte) -1));

      byte flags = 0;
      if (displayData.shadow()) flags |= 0x01;
      if (DisplayData.Alignment.LEFT == displayData.alignment()) flags |= 0x08;
      else if (DisplayData.Alignment.RIGHT == displayData.alignment()) flags |= 0x10;

      meta.add(new EntityData<>(27, EntityDataTypes.BYTE, flags));
    }

    adapter.send(player, new WrapperPlayServerEntityMetadata(entityId, meta));
    adapter.send(player, buildTransform(entityId, polygon3D.scale(), polygon3D.rotX(), polygon3D.rotY(), polygon3D.rotZ()));
  }

  private EntityType mapType(Object value) {
    return switch (value) {
      case ItemStack ignored1 -> EntityTypes.ITEM_DISPLAY;
      case String ignored -> EntityTypes.TEXT_DISPLAY;
      default -> EntityTypes.TEXT_DISPLAY;
    };
  }

  private WrapperPlayServerEntityMetadata buildTransform(
    int entityId, float scale, float rotX, float rotY, float rotZ) {

    List<EntityData<?>> meta = new ArrayList<>();

    meta.add(new EntityData<>(12, EntityDataTypes.VECTOR3F,
      new Vector3f(scale, scale, scale)));

    if (rotX != 0 || rotY != 0 || rotZ != 0) {

      Quaternionf q = new Quaternionf().rotationXYZ(
        (float) Math.toRadians(rotX),
        (float) Math.toRadians(rotY),
        (float) Math.toRadians(rotZ)
      );

      meta.add(new EntityData<>(13, EntityDataTypes.QUATERNION,
        new Quaternion4f(q.x, q.y, q.z, q.w)));
    }

    meta.add(new EntityData<>(8, EntityDataTypes.INT, 0));
    meta.add(new EntityData<>(9, EntityDataTypes.INT, 0));

    return new WrapperPlayServerEntityMetadata(entityId, meta);
  }

}
