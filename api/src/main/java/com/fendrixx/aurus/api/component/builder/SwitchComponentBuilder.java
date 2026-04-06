package com.fendrixx.aurus.api.component.builder;

import com.fendrixx.aurus.api.component.DisplayData;
import com.fendrixx.aurus.api.component.implementation.SwitchComponent;

import java.util.function.Supplier;

public final class SwitchComponentBuilder extends ButtonComponentBuilder<SwitchComponentBuilder,SwitchComponent> {
  private DisplayData inactiveData;
  private Supplier<Boolean> stateBoolean = () -> true;

  public Supplier<Boolean> stateBoolean() {
    return stateBoolean;
  }

  public DisplayData inactiveData() {
    return inactiveData;
  }

  public SwitchComponentBuilder inactiveData(DisplayData inactiveData) {
    this.inactiveData = inactiveData;
    return this;
  }

  public SwitchComponentBuilder stateBoolean(Supplier<Boolean> stateBoolean) {
    this.stateBoolean = stateBoolean;
    return this;
  }

  @Override
  public SwitchComponent build() {
    return new SwitchComponent(this);
  }
}
