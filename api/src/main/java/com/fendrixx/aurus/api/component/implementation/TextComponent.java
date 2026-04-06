package com.fendrixx.aurus.api.component.implementation;

import com.fendrixx.aurus.api.component.ActionType;
import com.fendrixx.aurus.api.component.builder.ButtonComponentBuilder;
import com.fendrixx.aurus.api.packet.PacketDispatcher;
import com.fendrixx.aurus.api.packet.PacketType;
import com.fendrixx.aurus.api.packet.PacketWrapper;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class TextComponent extends ButtonEntityComponent<TextComponent> {

  public TextComponent(ButtonComponentBuilder<TextComponent> builder) {
    super(builder);
  }

  @Override
  public void rendering(Player player, Location location) {
    PacketDispatcher.send(player, PacketType.DISPLAY_ENTITY, new PacketWrapper(
      entityId(),
      null,
      location,
      polygon3D,
      displayData));
  }

  @Override
  public void button(Player player, ActionType type) {
    this.actions.forEach(actions1 -> {
      if (actions1.actionType() != type) return;
      actions1.action(player);
    });
  }
}