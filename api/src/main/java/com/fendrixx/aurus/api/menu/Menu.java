package com.fendrixx.aurus.api.menu;

import com.fendrixx.aurus.api.component.ActionType;
import com.fendrixx.aurus.api.component.EntityComponent;
import com.fendrixx.aurus.api.packet.Vector3D;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class Menu {

  private final Int2ObjectMap<List<MenuArea>> tickBuckets;
  private final Object2ObjectMap<UUID, Location> uuidBaseLocationMap;
  private final Set<UUID> viewers;
  private final int maxTick;

  private int currentTick = 0;

  private Menu(Set<MenuArea> areas) {
    this.tickBuckets = new Int2ObjectOpenHashMap<>();
    this.uuidBaseLocationMap = new Object2ObjectOpenHashMap<>();
    this.viewers = new HashSet<>();

    int max = 0;

    for (MenuArea area : areas) {
      int interval = area.updateTicks();

      if (interval <= 0) continue;

      tickBuckets.computeIfAbsent(interval, k -> new ArrayList<>()).add(area);

      if (interval > max) max = interval;
    }

    this.maxTick = max;
  }

  public void render(@NotNull Player player, @NotNull Location location) {
    viewers.add(player.getUniqueId());
    // Calculo del location relativo
    uuidBaseLocationMap.put(player.getUniqueId(), location);
    //
    PlayerMenuCache.INSTANCE.save(player.getUniqueId(), this);

    final Location relative = uuidBaseLocationMap.get(player.getUniqueId()).clone();

    for (List<MenuArea> areas : tickBuckets.values()) {
      for (MenuArea area : areas) {
        final Vector3D vector3D = area.vector3D();
        for (EntityComponent<?,?> component : area.components()) {
          component.execute(player, ActionType.RENDER, relative.add(vector3D.x(), vector3D.y(), vector3D.z()));
        }
      }
    }
  }

  public void render(@NotNull Player player) {
    this.render(player, player.getLocation());
  }

  public void removeViewer(@NotNull Player player) {
    viewers.remove(player.getUniqueId());
    uuidBaseLocationMap.remove(player.getUniqueId());
    PlayerMenuCache.INSTANCE.remove(player.getUniqueId());
  }

  public void tick() {
    currentTick++;

    if (currentTick > maxTick) {
      currentTick = 1;
    }

    for (Int2ObjectMap.Entry<List<MenuArea>> entry : tickBuckets.int2ObjectEntrySet()) {
      int interval = entry.getIntKey();

      if (currentTick % interval != 0) continue;

      for (UUID uuid : viewers) {
        Player player = org.bukkit.Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline()) continue;

        final Location relativeLocation = uuidBaseLocationMap.get(player).clone();

        for (MenuArea area : entry.getValue()) {
          final Vector3D vector3D = area.vector3D();
          area.update(player, relativeLocation.add(vector3D.x(), vector3D.y(), vector3D.z()));
        }
      }
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private final Set<MenuArea> areas = new HashSet<>();

    public Builder appendArea(MenuArea area) {
      areas.add(area);
      return this;
    }

    public Menu build() {
      return new Menu(areas);
    }
  }
}
