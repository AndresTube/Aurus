package com.fendrixx.aurus.api.component.implementation;

import com.fendrixx.aurus.api.component.ActionType;
import com.fendrixx.aurus.api.component.builder.InputComponentBuilder;
import com.fendrixx.aurus.api.menu.Menu;
import com.fendrixx.aurus.api.menu.PlayerMenuCache;
import com.fendrixx.aurus.api.packet.PacketDispatcher;
import com.fendrixx.aurus.api.packet.PacketType;
import com.fendrixx.aurus.api.packet.PacketWrapper;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class InputButtonComponent extends ButtonEntityComponent<InputComponentBuilder, InputButtonComponent> {

  private final String variableName;
  private final Runnable onSubmit;

  public InputButtonComponent(InputComponentBuilder builder) {
    super(builder);
    this.variableName = builder.variableName();
    this.onSubmit = builder.submitAction();
  }

  @Override
  public void rendering(Player player, Location location) {
    PacketDispatcher.send(player, PacketType.DISPLAY_ENTITY, new PacketWrapper(
      entityId(),
      null,
      location,
      polygon3D,
      displayData,
      variableName));
  }

  @Override
  public void button(Player player, ActionType type) {
    if(type != ActionType.CLICK) return;
    final Menu menu = PlayerMenuCache.INSTANCE.menu(player.getUniqueId());
    this.onSubmit.run();
    menu.render(player);
  }

  public String variableName()  { return variableName; }
}