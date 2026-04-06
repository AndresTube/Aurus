package com.fendrixx.aurus.api.component.implementation;

import com.fendrixx.aurus.api.action.Actions;
import com.fendrixx.aurus.api.component.ActionType;
import com.fendrixx.aurus.api.component.DisplayData;
import com.fendrixx.aurus.api.component.builder.SwitchComponentBuilder;
import com.fendrixx.aurus.api.packet.PacketDispatcher;
import com.fendrixx.aurus.api.packet.PacketType;
import com.fendrixx.aurus.api.packet.PacketWrapper;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

public final class SwitchComponent extends ButtonEntityComponent<SwitchComponentBuilder,SwitchComponent> {
  private final DisplayData inactiveData;
  private final Supplier<Boolean> stateBoolean;

  public SwitchComponent(SwitchComponentBuilder builder) {
    super(builder);
    this.inactiveData = builder.inactiveData();
    this.stateBoolean = builder.stateBoolean();
  }

  @Override
  public void rendering(Player player, Location location) {
    if (stateBoolean.get()) {
      PacketDispatcher.send(player, PacketType.DISPLAY_ENTITY, new PacketWrapper(
        entityId(),
        null,
        location,
        polygon3D,
        displayData
      ));
    }
    else {
      PacketDispatcher.send(player, PacketType.DISPLAY_ENTITY, new PacketWrapper(
        entityId(),
        null,
        location,
        polygon3D,
        inactiveData
      ));
    }
  }

  @Override
  public void button(Player player, ActionType type) {

  }

  public record SwitchState(String text, Actions actions) {
  }
}
