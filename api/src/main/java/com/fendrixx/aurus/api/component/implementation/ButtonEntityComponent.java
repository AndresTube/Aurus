package com.fendrixx.aurus.api.component.implementation;

import com.fendrixx.aurus.api.component.ActionType;
import com.fendrixx.aurus.api.component.EntityComponent;
import com.fendrixx.aurus.api.component.DisplayData;
import com.fendrixx.aurus.api.component.builder.ButtonComponentBuilder;
import com.fendrixx.aurus.api.packet.PacketDispatcher;
import com.fendrixx.aurus.api.packet.PacketType;
import com.fendrixx.aurus.api.packet.PacketWrapper;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class ButtonEntityComponent<B extends ButtonComponentBuilder<B, T>,T extends ButtonEntityComponent<B,T>> extends EntityComponent<B, T> {
  protected final DisplayData displayData;

  protected ButtonEntityComponent(B builder) {
    super(builder);
    this.displayData = builder.displayData();
  }

  public abstract void rendering(Player player, Location location);

  public abstract void button(Player player, ActionType type);

  @Override
  public void execute(Player player, ActionType type, Location location) {
    if (type == ActionType.DECONSTRUCT) PacketDispatcher.send(player, PacketType.DESTROY_ENTITY, new PacketWrapper(entityId(), null));
    if (type == ActionType.RENDER) rendering(player, location);
    if (type == ActionType.CLICK || type == ActionType.CLICK_LEFT) button(player, type);
    this.subComponents.forEach(subComponent -> subComponent.execute(player, type, location));
  }
}