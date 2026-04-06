package com.fendrixx.aurus.api.packet;

import org.bukkit.entity.Player;

public interface PacketInterpreter {

    void interpret(Player player, PacketWrapper wrapper, PacketSender sender);
}