package com.fendrixx.aurus.expansion;

import com.fendrixx.aurus.Aurus;
import com.fendrixx.aurus.menu.MenuManager;
import com.fendrixx.aurus.processors.InputProcessor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PAPIExpansion extends PlaceholderExpansion {

    private final InputProcessor processor;
    private final MenuManager menuManager;

    public PAPIExpansion(InputProcessor processor, Aurus plugin) {
        this.processor = processor;
        this.menuManager = plugin.getMenuManager();
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
        return "1.1.4-BETA";
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        UUID uuid = player.getUniqueId();
        if (params.equalsIgnoreCase("active_menu")) return menuManager.getActiveMenuString(uuid);
        if (params.startsWith("variable_")) {
            String varName = params.replace("variable_", "");

            return processor.getValue(uuid, varName);
        }
        return null;
    }
}