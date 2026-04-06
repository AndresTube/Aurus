package com.fendrixx.aurus.api.component.builder;

import com.fendrixx.aurus.api.component.implementation.ItemComponent;
import org.bukkit.inventory.ItemStack;

public final class ItemDisplayComponentBuilder extends ButtonComponentBuilder<ItemDisplayComponentBuilder, ItemComponent> {
  private ItemStack itemStack;

  public ItemDisplayComponentBuilder itemStack(ItemStack itemStack) {
    this.itemStack = itemStack;
    return this;
  }

  public ItemStack itemStack() {
    return itemStack;
  }

  @Override
  public ItemComponent build() {
    return new ItemComponent(this);
  }
}
