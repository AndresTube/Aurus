package com.fendrixx.aurus.packetevent.interpreter;

import com.fendrixx.aurus.api.packet.PacketInterpreter;
import com.fendrixx.aurus.api.packet.PacketSender;
import com.fendrixx.aurus.api.packet.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class DestroyEntityInterpreter implements PacketInterpreter {
  @Override
  public void interpret(Player player, PacketWrapper wrapper, PacketSender sender) {
    final int entityId = wrapper.get(0, int.class);
    final UUID uuid = wrapper.getOrDefault(1, null);

    sender.send(player, new WrapperPlayServerDestroyEntities(entityId));

    if (uuid == null) return;

    sender.send(player, new WrapperPlayServerPlayerInfoRemove(uuid));
  }
}
