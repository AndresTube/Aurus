package com.fendrixx.aurus.menu;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCamera;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;

public class MenuCamera {
    private final Player player;
    private Pig tripod;

    public MenuCamera(Player player) {
        this.player = player;
    }

    public void spawn() {
        Location location = player.getLocation();

        tripod = (Pig) location.getWorld().spawnEntity(location, EntityType.PIG);
        tripod.setInvisible(true);
        tripod.setAI(false);
        tripod.setGravity(false);
        tripod.setSilent(true);
        tripod.setInvulnerable(true);
        tripod.setCollidable(false);

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerCamera(tripod.getEntityId()));

        PacketEvents.getAPI().getPlayerManager().sendPacket(player,
                new WrapperPlayServerSetPassengers(tripod.getEntityId(), new int[] { player.getEntityId() }));
    }

    public void remove() {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerCamera(player.getEntityId()));

        if (tripod != null) {
            tripod.remove();
        }
    }

    public Location getLocation() {
        return tripod != null ? tripod.getLocation() : player.getLocation();
    }

    public Location getEyeLocation() {
        return getLocation().clone().add(0, 0.6, 0);
    }

    public Pig getTripod() {
        return tripod;
    }
}
