package com.fendrixx.aurus.menu;

import com.fendrixx.aurus.processors.ActionProcessor;
import com.fendrixx.aurus.util.ColorUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

public class MenuButton {
    private final Display display;
    private final String rawText;
    private final Runnable onClick;
    private final String type;
    private final String variableName;
    private final ConfigurationSection config;
    private final ActionProcessor actionProcessor;
    private final double baseX;
    private final double baseY;

    public MenuButton(Display display, String rawText, Runnable onClick, String type, String variableName,
            ConfigurationSection config, ActionProcessor actionProcessor, double baseX, double baseY) {
        this.display = display;
        this.rawText = rawText;
        this.onClick = onClick;
        this.type = type;
        this.variableName = variableName;
        this.config = config;
        this.actionProcessor = actionProcessor;
        this.baseX = baseX;
        this.baseY = baseY;
    }

    public void updateText(Player player) {
        if (display instanceof TextDisplay td && rawText != null) {
            td.setText(ColorUtils.format(actionProcessor.parse(player, rawText)));
        }
    }

    public Display getDisplay() {
        return display;
    }

    public String getType() {
        return type;
    }

    public String getVariableName() {
        return variableName;
    }

    public ConfigurationSection getConfig() {
        return config;
    }

    public double getBaseX() {
        return baseX;
    }

    public double getBaseY() {
        return baseY;
    }

    public void onClick() {
        if (onClick != null)
            onClick.run();
    }
}