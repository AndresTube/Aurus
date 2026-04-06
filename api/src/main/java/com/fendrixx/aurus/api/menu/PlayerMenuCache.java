package com.fendrixx.aurus.api.menu;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Objects;
import java.util.UUID;

public final class PlayerMenuCache {
  public static final PlayerMenuCache INSTANCE = new PlayerMenuCache();
  private final Object2ObjectMap<UUID, Menu> menuMap = new Object2ObjectOpenHashMap<>();

  public Menu menu(UUID uuid) {
    Objects.requireNonNull(uuid, "uuid");
    return menuMap.get(uuid);
  }

  public void remove(UUID uuid) {
    this.menuMap.remove(uuid);
  }

  public void save(UUID uuid, Menu menu) {
    Objects.requireNonNull(uuid, "uuid");
    this.menuMap.put(uuid, menu);
  }
}
