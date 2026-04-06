package com.fendrixx.aurus.api.component.builder;

import com.fendrixx.aurus.api.component.DisplayData;
import com.fendrixx.aurus.api.component.EntityComponent;
import com.fendrixx.aurus.api.component.implementation.ButtonEntityComponent;

public abstract class ButtonComponentBuilder<B extends ButtonComponentBuilder<B, T>,T extends ButtonEntityComponent<B,T>> extends EntityComponent.Builder<T,B> {
  protected DisplayData displayData = new DisplayData();

  public DisplayData displayData() {
    return displayData;
  }

  public B displayData(DisplayData displayData) {
    this.displayData = displayData;
    return (B) this;
  }
}
