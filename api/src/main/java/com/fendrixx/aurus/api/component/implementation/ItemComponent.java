package com.fendrixx.aurus.api.component.implementation;

import com.fendrixx.aurus.api.component.ActionType;
import com.fendrixx.aurus.api.component.builder.ItemDisplayComponentBuilder;
import com.fendrixx.aurus.api.packet.PacketDispatcher;
import com.fendrixx.aurus.api.packet.PacketType;
import com.fendrixx.aurus.api.packet.PacketWrapper;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ItemComponent extends ButtonEntityComponent<ItemDisplayComponentBuilder,ItemComponent> {
  private final ItemStack itemStack;

  public ItemComponent(ItemDisplayComponentBuilder builder) {
    super(builder);
    this.itemStack = builder.itemStack();
  }

  @Override
  public void rendering(Player player, Location location) {
    PacketDispatcher.send(player, PacketType.DISPLAY_ENTITY, new PacketWrapper(
      entityId(),
      null,
      location,
      polygon3D,
      displayData,
      itemStack));
  }

  @Override
  public void button(Player player, ActionType type) {
    this.actions.forEach(actions1 -> {
      if (actions1.actionType() != type) return;
      actions1.action(player);
    });
  }
}