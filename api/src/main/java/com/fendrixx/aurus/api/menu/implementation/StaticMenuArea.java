package com.fendrixx.aurus.api.menu.implementation;

import com.fendrixx.aurus.api.component.ActionType;
import com.fendrixx.aurus.api.component.AreaType;
import com.fendrixx.aurus.api.component.EntityComponent;
import com.fendrixx.aurus.api.menu.MenuArea;
import com.fendrixx.aurus.api.packet.Vector3D;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

public record StaticMenuArea(Collection<EntityComponent<?,?>> components,
                             int updateTicks,
                             AreaType areaType,
                             Vector3D vector3D) implements MenuArea {

  @Override
  public void update(Player player, Location location) {
    for (EntityComponent<?,?> component : this.components()) {
      component.execute(player, ActionType.UPDATE, location);
    }
  }

  @Override
  public void quit(Player player) {

  }
}
