package com.fendrixx.aurus.api.component;

import org.bukkit.configuration.ConfigurationSection;

public interface ComponentSerializer<T extends EntityComponent> {

  public T deserialize(ConfigurationSection section);
}
