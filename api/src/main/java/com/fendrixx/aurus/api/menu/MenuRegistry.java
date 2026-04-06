package com.fendrixx.aurus.api.menu;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Objects;

public final class MenuRegistry {
  public static final MenuRegistry INSTANCE = new MenuRegistry();
  private final Object2ObjectMap<String, Menu> menuObject2ObjectMap = new Object2ObjectOpenHashMap<>();

  public void register(String string, Menu menu) {
    Objects.requireNonNull(string, "id");
    this.menuObject2ObjectMap.put(string, menu);
  }

  public Menu get(String string) {
    Objects.requireNonNull(string, "id");
    return this.menuObject2ObjectMap.get(string);
  }
}
