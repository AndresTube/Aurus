package com.fendrixx.aurus.menu;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCamera;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MenuCamera {

    // random id
    private final int fakeEntityId = -696927;
    private final UUID entityUuid = UUID.randomUUID();

    public void startSpectating(Player player, Location loc) {
        WrapperPlayServerSpawnEntity spawnPacket = new WrapperPlayServerSpawnEntity(
                fakeEntityId,
                java.util.Optional.of(entityUuid),
                EntityTypes.ARMOR_STAND,
                new Vector3d(loc.getX(), loc.getY(), loc.getZ()),
                loc.getPitch(),
                loc.getYaw(),
                loc.getYaw(),
                0,
                java.util.Optional.of(new Vector3d(0, 0, 0))
        );

        byte mask = 0x20;
        var metadata = new com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata(
                fakeEntityId,
                java.util.List.of(new com.github.retrooper.packetevents.protocol.entity.data.EntityData(
                        0,
                        com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes.BYTE,
                        mask
                ))
        );

        WrapperPlayServerCamera cameraPacket = new WrapperPlayServerCamera(fakeEntityId);

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, spawnPacket);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, metadata);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, cameraPacket);
    }

    public void stopSpectating(Player player) {
        WrapperPlayServerCamera resetCamera = new WrapperPlayServerCamera(player.getEntityId());
        WrapperPlayServerDestroyEntities destroyPacket = new WrapperPlayServerDestroyEntities(fakeEntityId);

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, resetCamera);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, destroyPacket);
    }
}