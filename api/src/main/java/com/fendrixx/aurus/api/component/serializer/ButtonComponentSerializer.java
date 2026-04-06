package com.fendrixx.aurus.api.component.serializer;

import com.fendrixx.aurus.api.action.Actions;
import com.fendrixx.aurus.api.component.ActionType;
import com.fendrixx.aurus.api.component.ComponentSerializer;
import com.fendrixx.aurus.api.component.DisplayData;
import com.fendrixx.aurus.api.component.builder.ButtonDisplayComponentBuilder;
import com.fendrixx.aurus.api.component.implementation.ButtonComponent;
import com.fendrixx.aurus.api.packet.Polygon3D;
import com.fendrixx.aurus.api.packet.Vector3D;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;

public final class ButtonComponentSerializer implements ComponentSerializer<ButtonComponent> {
  @Override
  public ButtonComponent deserialize(ConfigurationSection root) {
    final ButtonDisplayComponentBuilder builder = new ButtonDisplayComponentBuilder()
      .vector3D(new Vector3D(root.getDouble("x", 0), root.getDouble("y", 0), root.getDouble("z", 1.0D)))
      .polygon3D(new Polygon3D((float) root.getDouble("rotation.x", 0F), (float) root.getDouble("rotation.y", 0F), (float) root.getDouble("rotation.z", 0F)))
      .displayData(new DisplayData(
          root.getString("text"),
          Color.fromARGB(root.getInt("background.alpha"), root.getInt("background.red"), root.getInt("background.green"), root.getInt("background.blue")).asARGB(),
          root.getBoolean("shadow", false),
          DisplayData.Alignment.valueOf(root.getString("alignment", "CENTER")),
          (Byte) root.get("billboard", 0)));

    root.getList("actions", Collections.emptyList()).forEach(newSection -> {
      final var configSection = (ConfigurationSection) newSection;
      builder.actions(new Actions(ActionType.valueOf(configSection.getString("type", "CLICK")), configSection.getStringList("commands")));
    });

    return builder.build();
  }


}
