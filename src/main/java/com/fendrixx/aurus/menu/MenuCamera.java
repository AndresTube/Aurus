package com.fendrixx.aurus.menu;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class MenuCamera {
    private final int entityId;
    private final UUID uuid;

    public MenuCamera() {
        this.entityId = ThreadLocalRandom.current().nextInt(10000, 20000);
        this.uuid = UUID.randomUUID();
    }

    public void spawn(Player player, Location loc) {
        WrapperPlayServerSpawnEntity spawn = new WrapperPlayServerSpawnEntity(
                entityId, java.util.Optional.of(uuid), EntityTypes.PIG,
                new com.github.retrooper.packetevents.util.Vector3d(loc.getX(), loc.getY(), loc.getZ()),
                0, 0, 0, 0, java.util.Optional.empty()
        );

        var metadata = new WrapperPlayServerEntityMetadata(entityId, List.of(
                new EntityData(0, EntityDataTypes.BYTE, (byte) 0x20)
        ));

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, spawn);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, metadata);
    }

    public void despawn(Player player) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerDestroyEntities(entityId));
    }

    public int getEntityId() { return entityId; }
}