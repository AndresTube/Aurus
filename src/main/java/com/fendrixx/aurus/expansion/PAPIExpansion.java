package com.fendrixx.aurus.expansion;

import com.fendrixx.aurus.processors.InputProcessor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PAPIExpansion extends PlaceholderExpansion {

    private final InputProcessor processor;

    public PAPIExpansion(InputProcessor processor, Plugin plugin) {
        this.processor = processor;
    }

    @Override
    public String getIdentifier() {
        return "aurus";
    }

    @Override
    public String getAuthor() {
        return "Fendrixx";
    }

    @Override
    public String getVersion() {
        return "1.1.2-BETA";
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (params.startsWith("variable_")) {
            String varName = params.replace("variable_", "");

            return processor.getValue(varName);
        }
        return null;
    }
}