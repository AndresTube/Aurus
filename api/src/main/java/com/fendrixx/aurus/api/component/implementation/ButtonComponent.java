package com.fendrixx.aurus.api.component.implementation;

import com.fendrixx.aurus.api.component.ActionType;
import com.fendrixx.aurus.api.component.builder.ButtonDisplayComponentBuilder;
import com.fendrixx.aurus.api.packet.PacketDispatcher;
import com.fendrixx.aurus.api.packet.PacketType;
import com.fendrixx.aurus.api.packet.PacketWrapper;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class ButtonComponent extends ButtonEntityComponent<ButtonDisplayComponentBuilder, ButtonComponent> {

  public ButtonComponent(ButtonDisplayComponentBuilder builder) {
    super(builder);
    //this.isActive = builder.isActive();
  }

  @Override
  public void rendering(Player player, Location location) {
    final PacketWrapper wrapper = new PacketWrapper(
      entityId(),
      null,
      location,
      polygon3D,
      displayData);
    PacketDispatcher.send(player, PacketType.DISPLAY_ENTITY, wrapper);
  }

  @Override
  public void button(Player player, ActionType type) {
    if(type != ActionType.CLICK) return;
    this.actions.forEach(actions1 -> {
      if (actions1.actionType() != type) return;
      actions1.action(player);
    });
  }
}