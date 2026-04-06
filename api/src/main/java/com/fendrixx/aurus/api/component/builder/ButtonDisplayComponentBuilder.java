package com.fendrixx.aurus.api.component.builder;

import com.fendrixx.aurus.api.component.implementation.ButtonComponent;

public final class ButtonDisplayComponentBuilder extends ButtonComponentBuilder<ButtonDisplayComponentBuilder, ButtonComponent> {

  @Override
  public ButtonComponent build() {
    return new ButtonComponent(this);
  }
}
