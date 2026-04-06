package com.fendrixx.aurus.api.menu;

import com.fendrixx.aurus.api.component.AreaType;
import com.fendrixx.aurus.api.component.EntityComponent;
import com.fendrixx.aurus.api.packet.Vector3D;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface MenuArea {
  Vector3D vector3D();
  int updateTicks();
  AreaType areaType();
  Collection<EntityComponent<?>> components();
  void update(Player player, Location location);
  void quit(Player player);

  interface Builder {
    <T extends EntityComponent<T>> Builder appendComponent(EntityComponent<T> entityComponent);
    Builder updateTicks(int ticks);
    Builder areaType(AreaType type);
    Builder vector3D(Vector3D vector3D);
  }
}
