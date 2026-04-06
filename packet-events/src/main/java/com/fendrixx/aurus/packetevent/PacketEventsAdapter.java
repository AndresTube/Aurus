package com.fendrixx.aurus.packetevent;

import com.fendrixx.aurus.packetevent.interpreter.CursorPacketInterpreter;
import com.fendrixx.aurus.api.packet.PacketAdapter;
import com.fendrixx.aurus.api.packet.PacketInterpreterRegistry;
import com.fendrixx.aurus.api.packet.PacketType;
import com.fendrixx.aurus.api.packet.PacketWrapper;
import com.fendrixx.aurus.packetevent.interpreter.DestroyEntityInterpreter;
import com.fendrixx.aurus.packetevent.interpreter.DisplayEntityInterpreter;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import org.bukkit.entity.Player;

public final class PacketEventsAdapter extends PacketAdapter {

  public static final PacketEventsAPI<?> API;

  static {
    API = PacketEvents.getAPI();
  }

  public PacketEventsAdapter(PacketInterpreterRegistry registry) {
    super(registry);
    registry.register(PacketType.CURSOR_ENTITY, new CursorPacketInterpreter());
    registry.register(PacketType.DISPLAY_ENTITY, new DisplayEntityInterpreter());
    registry.register(PacketType.DESTROY_ENTITY, new DestroyEntityInterpreter());
  }

  @Override
  public void send(Player player, PacketType type, PacketWrapper wrapper) {
    super.registry.get(type).interpret(player, wrapper, this::sendPacket);
  }

  private void sendPacket(Player player, Object packet) {
    API.getPlayerManager().sendPacket(player, packet);
  }
}
