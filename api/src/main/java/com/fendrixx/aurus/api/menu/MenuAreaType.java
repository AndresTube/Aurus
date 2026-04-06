package com.fendrixx.aurus.api.menu;

import com.fendrixx.aurus.api.menu.implementation.StaticMenuArea;

import java.util.function.Function;

public final class MenuAreaType {
  public static final Function<MenuArea.Builder, StaticMenuArea> STATIC_MENU_AREA = builder ->  {
    return new StaticMenuArea();
  };
}
