package com.fendrixx.aurus.api.packet;

import org.bukkit.entity.Player;

public abstract class PacketAdapter {
  protected final PacketInterpreterRegistry registry;

  protected PacketAdapter(PacketInterpreterRegistry registry) {
    this.registry = registry;
  }

  public abstract void send(Player player, PacketType type, PacketWrapper wrapper);
}