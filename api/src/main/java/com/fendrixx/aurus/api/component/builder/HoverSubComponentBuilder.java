package com.fendrixx.aurus.api.component.builder;

import com.fendrixx.aurus.api.component.DisplayData;
import com.fendrixx.aurus.api.component.implementation.subcomponent.HoverSubEntityComponent;

import java.util.UUID;

public final class HoverSubComponentBuilder extends SubComponentBuilder<HoverSubEntityComponent> {
  private UUID fakeUuid = UUID.randomUUID();
  private DisplayData displayData = new DisplayData();

  public HoverSubComponentBuilder displayData(DisplayData displayData) {
    this.displayData = displayData;
    return this;
  }

  public HoverSubComponentBuilder fakeUuid(UUID fakeUuid) {
    this.fakeUuid = fakeUuid;
    return this;
  }

  public DisplayData displayData() {
    return displayData;
  }

  public UUID fakeUuid() {
    return fakeUuid;
  }

  @Override
  public HoverSubEntityComponent build() {
    return new HoverSubEntityComponent(this);
  }
}
