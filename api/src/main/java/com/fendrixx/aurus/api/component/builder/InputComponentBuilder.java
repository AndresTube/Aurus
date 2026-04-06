package com.fendrixx.aurus.api.component.builder;

import com.fendrixx.aurus.api.component.implementation.InputButtonComponent;

public final class InputComponentBuilder extends ButtonComponentBuilder<InputComponentBuilder, InputButtonComponent> {
  private String variableName;
  private Runnable submitAction;

  public String variableName() {
    return variableName;
  }

  public Runnable submitAction() {
    return submitAction;
  }

  public InputComponentBuilder variableName(String variableName) {
    this.variableName = variableName;
    return this;
  }

  public InputComponentBuilder submitAction(Runnable submitAction) {
    this.submitAction = submitAction;
    return this;
  }

  @Override
  public InputButtonComponent build() {
    return new InputButtonComponent(this);
  }
}
