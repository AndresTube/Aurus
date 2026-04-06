package com.fendrixx.aurus.api.packet;

import org.bukkit.entity.Player;

public interface PacketSender {

  void send(Player player, Object packet);
}
