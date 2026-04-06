package com.fendrixx.aurus.api.packet;

public class PacketInterpreterRegistry {
    private final PacketInterpreter[] packetInterpreters;

    public PacketInterpreterRegistry() {
        packetInterpreters = new PacketInterpreter[PacketType.values().length + 1];
    }

    public void register(PacketType type, PacketInterpreter interpreter) {
        packetInterpreters[type.ordinal() - 1] = interpreter;
    }

    public PacketInterpreter get(PacketType type) {
        return packetInterpreters[type.ordinal() - 1];
    }

}