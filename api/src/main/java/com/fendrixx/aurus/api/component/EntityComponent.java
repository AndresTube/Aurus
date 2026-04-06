package com.fendrixx.aurus.api.component;

import com.fendrixx.aurus.api.action.Actions;
import com.fendrixx.aurus.api.menu.MenuArea;
import com.fendrixx.aurus.api.packet.Polygon3D;
import com.fendrixx.aurus.api.packet.Vector3D;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class EntityComponent<B extends EntityComponent.Builder<C, B>, C extends EntityComponent<B, C>> {
  static final AtomicInteger IDS = new AtomicInteger();
  private final int entityId;
  protected final Polygon3D polygon3D;
  protected final Vector3D vector3D;
  protected final Set<SubComponent> subComponents;
  protected final List<Actions> actions;

  protected EntityComponent(B builder) {
    this(builder, IDS.getAndIncrement());
  }

  protected EntityComponent(B builder, int id) {
    this.entityId = id;
    this.polygon3D = builder.polygon3D();
    this.subComponents = builder.subComponents();
    this.actions = builder.actionsList();
    this.vector3D = builder.vector3D();
  }

  public abstract void execute(Player player, ActionType type, Location location);

  public int entityId() {
    return this.entityId;
  }

  public Polygon3D polygon3D() {
    return polygon3D;
  }

  public Vector3D vector3D() {
    return vector3D;
  }

  public void onAttach(MenuArea.Builder menuBuilder) {}

  public static abstract class Builder<C extends EntityComponent<T, C>, T extends Builder<C, T>> {
    private Polygon3D polygon3D = new Polygon3D(0F, 0F, 0F);
    private Set<SubComponent> subComponents;
    private List<Actions> actionsList;
    private Vector3D vector3D = new Vector3D(0, 0);

    public T vector3D(Vector3D vector3D) {
      this.vector3D = vector3D;
      return (T) this;
    }

    public T polygon3D(Polygon3D polygon3D) {
      this.polygon3D = polygon3D;
      return (T) this;
    }

    public T subComponents(SubComponent... subComponents) {
      if (this.subComponents == null) this.subComponents = new ObjectOpenHashSet<>();
      this.subComponents.addAll(Arrays.asList(subComponents));
      return (T) this;
    }

    public T actions(Actions... actions) {
      if (this.actionsList == null) this.actionsList = new ArrayList<>();
      this.actionsList.addAll(Arrays.asList(actions));
      return (T) this;
    }

    public Vector3D vector3D() {
      return vector3D;
    }

    public Set<SubComponent> subComponents() {
      return Set.copyOf(subComponents);
    }

    public List<Actions> actionsList() {
      return List.copyOf(actionsList);
    }

    public Polygon3D polygon3D() {
      return polygon3D;
    }

    public abstract C build();
  }
}
