package com.fendrixx.aurus.packets;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FakeEntityFactory {

    private static final AtomicInteger ID_COUNTER = new AtomicInteger(-1);
    private static final boolean SUPPORTS_SCALE;

    static {
        String version = org.bukkit.Bukkit.getBukkitVersion();
        String[] parts = version.split("-")[0].split("\\.");
        int major = Integer.parseInt(parts[1]);
        int minor = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
        SUPPORTS_SCALE = major > 20 || (major == 20 && minor >= 5);
    }

    public static boolean supportsScale() {
        return SUPPORTS_SCALE;
    }

    public static int nextEntityId() {
        return ID_COUNTER.getAndDecrement();
    }

    private static void send(Player viewer, Object packet) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, (com.github.retrooper.packetevents.wrapper.PacketWrapper<?>) packet);
    }

    public static void spawnFakePlayer(Player viewer, int entityId, UUID fakeUUID, org.bukkit.Location loc,
                                       SkinFetcher.SkinData skin, String nametag) {
        String fakeName = "NPC_" + Integer.toHexString(entityId & 0xFFFF);
        UserProfile profile = new UserProfile(fakeUUID, fakeName);
        if (skin != null) {
            profile.setTextureProperties(List.of(new TextureProperty("textures", skin.textureValue(), skin.textureSignature())));
        }

        WrapperPlayServerPlayerInfoUpdate.PlayerInfo info = new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                profile, false, 0, GameMode.CREATIVE, null, null);

        WrapperPlayServerPlayerInfoUpdate infoPacket = new WrapperPlayServerPlayerInfoUpdate(
                EnumSet.of(
                        WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER,
                        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LISTED),
                info);
        send(viewer, infoPacket);

        WrapperPlayServerSpawnEntity spawnPacket = new WrapperPlayServerSpawnEntity(
                entityId, fakeUUID, EntityTypes.PLAYER,
                new Location(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch()),
                loc.getYaw(), 0, null);
        send(viewer, spawnPacket);

        List<EntityData<?>> metadata = new ArrayList<>();
        metadata.add(new EntityData<>(17, EntityDataTypes.BYTE, (byte) 0x7F));
        send(viewer, new WrapperPlayServerEntityMetadata(entityId, metadata));

        WrapperPlayServerTeams.NameTagVisibility nametagVisibility =
                (nametag != null && !nametag.isEmpty())
                        ? WrapperPlayServerTeams.NameTagVisibility.ALWAYS
                        : WrapperPlayServerTeams.NameTagVisibility.NEVER;

        WrapperPlayServerTeams.ScoreBoardTeamInfo teamInfo = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                Component.empty(), Component.empty(), Component.empty(),
                nametagVisibility,
                WrapperPlayServerTeams.CollisionRule.NEVER,
                NamedTextColor.WHITE,
                WrapperPlayServerTeams.OptionData.NONE);

        send(viewer, new WrapperPlayServerTeams(
                "aurus_" + (entityId & 0xFFFF),
                WrapperPlayServerTeams.TeamMode.CREATE, teamInfo, fakeName));

        if (nametag != null && !nametag.isEmpty()) {
            Component parsed = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(nametag);
            List<EntityData<?>> nameMeta = new ArrayList<>();
            nameMeta.add(new EntityData<>(2, EntityDataTypes.OPTIONAL_ADV_COMPONENT, Optional.of(parsed)));
            nameMeta.add(new EntityData<>(3, EntityDataTypes.BOOLEAN, true));
            send(viewer, new WrapperPlayServerEntityMetadata(entityId, nameMeta));
        }
    }

    public static void spawnFakeEntity(Player viewer, int entityId, org.bukkit.Location loc, String entityTypeName) {
        EntityType type = EntityTypes.getByName("minecraft:" + entityTypeName.toLowerCase());
        if (type == null) type = EntityTypes.ZOMBIE;

        UUID uuid = UUID.randomUUID();
        WrapperPlayServerSpawnEntity spawnPacket = new WrapperPlayServerSpawnEntity(
                entityId, uuid, type,
                new Location(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch()),
                loc.getYaw(), 0, null);
        send(viewer, spawnPacket);

        List<EntityData<?>> metadata = new ArrayList<>();
        metadata.add(new EntityData<>(5, EntityDataTypes.BOOLEAN, true));
        metadata.add(new EntityData<>(4, EntityDataTypes.BOOLEAN, true));
        send(viewer, new WrapperPlayServerEntityMetadata(entityId, metadata));
    }

    public static void setScale(Player viewer, int entityId, double scale) {
        if (!SUPPORTS_SCALE || scale == 1.0) return;
        WrapperPlayServerUpdateAttributes.Property prop = new WrapperPlayServerUpdateAttributes.Property(
                com.github.retrooper.packetevents.protocol.attribute.Attributes.GENERIC_SCALE,
                scale, Collections.emptyList());
        send(viewer, new WrapperPlayServerUpdateAttributes(entityId, List.of(prop)));
    }

    public static void destroyEntities(Player viewer, int... entityIds) {
        send(viewer, new WrapperPlayServerDestroyEntities(entityIds));
    }

    public static void removePlayerInfo(Player viewer, UUID... fakeUUIDs) {
        send(viewer, new WrapperPlayServerPlayerInfoRemove(fakeUUIDs));
    }

    public static int spawnFakeTextDisplay(Player viewer, org.bukkit.Location loc, String text,
                                            int bgColor, boolean shadow, byte billboard, float scale,
                                            float rotX, float rotY, float rotZ) {
        int entityId = nextEntityId();
        UUID uuid = UUID.randomUUID();

        send(viewer, new WrapperPlayServerSpawnEntity(
                entityId, uuid, EntityTypes.TEXT_DISPLAY,
                new Location(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch()),
                loc.getYaw(), 0, null));

        List<EntityData<?>> meta = new ArrayList<>();
        meta.add(new EntityData<>(15, EntityDataTypes.BYTE, billboard));
        meta.add(new EntityData<>(8, EntityDataTypes.INT, 0));

        Component textComponent = LegacyComponentSerializer.legacySection().deserialize(text);
        meta.add(new EntityData<>(23, EntityDataTypes.ADV_COMPONENT, textComponent));
        meta.add(new EntityData<>(25, EntityDataTypes.INT, bgColor));
        meta.add(new EntityData<>(26, EntityDataTypes.BYTE, (byte) -1));
        meta.add(new EntityData<>(27, EntityDataTypes.BYTE, (byte) (shadow ? 0x01 : 0)));

        send(viewer, new WrapperPlayServerEntityMetadata(entityId, meta));
        setDisplayTransform(viewer, entityId, scale, rotX, rotY, rotZ);

        return entityId;
    }

    public static int spawnFakeItemDisplay(Player viewer, org.bukkit.Location loc, ItemStack itemStack,
                                            float scale, float rotX, float rotY, float rotZ) {
        int entityId = nextEntityId();
        UUID uuid = UUID.randomUUID();

        send(viewer, new WrapperPlayServerSpawnEntity(
                entityId, uuid, EntityTypes.ITEM_DISPLAY,
                new Location(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch()),
                loc.getYaw(), 0, null));

        List<EntityData<?>> meta = new ArrayList<>();
        meta.add(new EntityData<>(15, EntityDataTypes.BYTE, (byte) 0));
        meta.add(new EntityData<>(23, EntityDataTypes.ITEMSTACK,
                SpigotConversionUtil.fromBukkitItemStack(itemStack)));

        send(viewer, new WrapperPlayServerEntityMetadata(entityId, meta));
        setDisplayTransform(viewer, entityId, scale, rotX, rotY, rotZ);

        return entityId;
    }

    public static int spawnFakeBlockDisplay(Player viewer, org.bukkit.Location loc, int blockStateId,
                                             float scale, float rotX, float rotY, float rotZ) {
        int entityId = nextEntityId();
        UUID uuid = UUID.randomUUID();

        send(viewer, new WrapperPlayServerSpawnEntity(
                entityId, uuid, EntityTypes.BLOCK_DISPLAY,
                new Location(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch()),
                loc.getYaw(), 0, null));

        List<EntityData<?>> meta = new ArrayList<>();
        meta.add(new EntityData<>(15, EntityDataTypes.BYTE, (byte) 0));
        meta.add(new EntityData<>(23, EntityDataTypes.INT, blockStateId));

        send(viewer, new WrapperPlayServerEntityMetadata(entityId, meta));
        setDisplayTransform(viewer, entityId, scale, rotX, rotY, rotZ);

        return entityId;
    }

    public static void setDisplayTransform(Player viewer, int entityId, float scale,
                                            float rotX, float rotY, float rotZ) {
        List<EntityData<?>> meta = new ArrayList<>();
        meta.add(new EntityData<>(12, EntityDataTypes.VECTOR3F,
                new com.github.retrooper.packetevents.util.Vector3f(scale, scale, scale)));
        if (rotX != 0 || rotY != 0 || rotZ != 0) {
            org.joml.Quaternionf q = new org.joml.Quaternionf().rotationXYZ(
                    (float) Math.toRadians(rotX),
                    (float) Math.toRadians(rotY),
                    (float) Math.toRadians(rotZ));
            meta.add(new EntityData<>(13, EntityDataTypes.QUATERNION,
                    new com.github.retrooper.packetevents.util.Quaternion4f(q.x, q.y, q.z, q.w)));
        }
        meta.add(new EntityData<>(8, EntityDataTypes.INT, 0));
        meta.add(new EntityData<>(9, EntityDataTypes.INT, 0));

        send(viewer, new WrapperPlayServerEntityMetadata(entityId, meta));
    }

    public static void rotateEntity(Player viewer, int entityId, float yaw, float pitch) {
        send(viewer, new WrapperPlayServerEntityRotation(entityId, yaw, pitch, false));
        send(viewer, new WrapperPlayServerEntityHeadLook(entityId, yaw));
    }

    public static void rotateEntityFull(Player viewer, int entityId, float bodyYaw, float headPitch, float headYaw) {
        send(viewer, new WrapperPlayServerEntityRotation(entityId, bodyYaw, headPitch, false));
        send(viewer, new WrapperPlayServerEntityHeadLook(entityId, headYaw));
    }

    public static void teleportEntity(Player viewer, int entityId, org.bukkit.Location loc) {
        send(viewer, new WrapperPlayServerEntityTeleport(entityId,
                new Location(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch()),
                false));
    }

    public static void updateEntityMetadata(Player viewer, int entityId, List<EntityData<?>> metadata) {
        send(viewer, new WrapperPlayServerEntityMetadata(entityId, metadata));
    }

    public static void updateTextDisplayText(Player viewer, int entityId, String text) {
        Component textComponent = LegacyComponentSerializer.legacySection().deserialize(text);
        List<EntityData<?>> meta = new ArrayList<>();
        meta.add(new EntityData<>(23, EntityDataTypes.ADV_COMPONENT, textComponent));
        send(viewer, new WrapperPlayServerEntityMetadata(entityId, meta));
    }
}
