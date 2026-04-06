package com.fendrixx.aurus.packetevent.interpreter;

import com.fendrixx.aurus.api.packet.*;
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
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class CursorPacketInterpreter implements PacketInterpreter {

  static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
    .tags(StandardTags.color())
    .tags(StandardTags.gradient())
    .tags(StandardTags.font())
    .tags(StandardTags.keybind())
    .tags(StandardTags.rainbow())
    .tags(StandardTags.reset())
    .tags(StandardTags.translatable())
    .tags(StandardTags.translatableFallback())
    .tags(StandardTags.nbt())
    .build();

  @Override
  public void interpret(Player player, PacketWrapper wrapper, PacketSender sender) {

    int entityId = wrapper.get(0, Integer.class);
    UUID uuid = wrapper.getOrDefault(1, UUID.randomUUID());
    String type = wrapper.get(2, String.class);

    org.bukkit.Location vector3D = wrapper.get(3, org.bukkit.Location.class);;

    Object value = wrapper.get(4);

    Polygon3D polygon3D = wrapper.get(5, Polygon3D.class);

    EntityType entityType = mapType(type);

    sender.send(player, new WrapperPlayServerSpawnEntity(
      entityId,
      uuid,
      entityType,
      new Location(vector3D.x(), vector3D.y(), vector3D.z(), vector3D.getYaw(), vector3D.getPitch()),
      vector3D.getYaw(),
      0,
      null
    ));

    List<EntityData<?>> meta = new ArrayList<>();

    switch (type) {

      case "ITEM" -> {
        meta.add(new EntityData<>(15, EntityDataTypes.BYTE, (byte) 0));
        meta.add(new EntityData<>(23, EntityDataTypes.ITEMSTACK,
          SpigotConversionUtil.fromBukkitItemStack((ItemStack) value)));
      }

      case "BLOCK" -> {
        meta.add(new EntityData<>(15, EntityDataTypes.BYTE, (byte) 0));
        meta.add(new EntityData<>(23, EntityDataTypes.INT, (int) value));
      }

      default -> {
        int bgColor = wrapper.getOrDefault(13, 0);
        boolean shadow = wrapper.getOrDefault(14, false);
        String alignment = wrapper.getOrDefault(15, "CENTER");
        byte billboard = wrapper.getOrDefault(16, (byte) 0);

        meta.add(new EntityData<>(15, EntityDataTypes.BYTE, billboard));
        meta.add(new EntityData<>(8, EntityDataTypes.INT, 0));

        Component textComponent = MINI_MESSAGE.deserialize((String) value);

        meta.add(new EntityData<>(23, EntityDataTypes.ADV_COMPONENT, textComponent));
        meta.add(new EntityData<>(25, EntityDataTypes.INT, bgColor));
        meta.add(new EntityData<>(26, EntityDataTypes.BYTE, (byte) -1));

        byte flags = 0;
        if (shadow) flags |= 0x01;
        if ("LEFT".equalsIgnoreCase(alignment)) flags |= 0x08;
        else if ("RIGHT".equalsIgnoreCase(alignment)) flags |= 0x10;

        meta.add(new EntityData<>(27, EntityDataTypes.BYTE, flags));
      }
    }

    sender.send(player, new WrapperPlayServerEntityMetadata(entityId, meta));
    sender.send(player, buildTransform(entityId, polygon3D.scale(), polygon3D.rotX(), polygon3D.rotY(), polygon3D.rotZ()));
  }

  private EntityType mapType(String type) {
    return switch (type) {
      case "ITEM" -> EntityTypes.ITEM_DISPLAY;
      case "BLOCK" -> EntityTypes.BLOCK_DISPLAY;
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
