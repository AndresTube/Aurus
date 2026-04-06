package com.fendrixx.aurus.api.packet;

import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public final class PacketDispatcher {
  private static final PacketAdapter ADAPTER;

  static {
    try {
      ADAPTER = (PacketAdapter) Class.forName("com.fendrixx.aurus.packetevent.PacketEventsAdapter").getConstructor(PacketInterpreterRegistry.class).newInstance(new PacketInterpreterRegistry());
    } catch (InstantiationException |
             IllegalAccessException |
             InvocationTargetException |
             NoSuchMethodException |
             ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public static void send(Player player, PacketType type, PacketWrapper wrapper) {
    ADAPTER.send(player, type, wrapper);
  }
}
