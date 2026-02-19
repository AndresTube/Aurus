package com.fendrixx.aurus.menu;
import com.fendrixx.aurus.util.ColorUtils;
import me.clip.placeholderapi.PlaceholderAPI;
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
    public MenuButton(Display display, String rawText, Runnable onClick, String type, String variableName, ConfigurationSection config) {
        this.display = display;
        this.rawText = rawText;
        this.onClick = onClick;
        this.type = type;
        this.variableName = variableName;
        this.config = config;
    }
    public void updateText(Player player) {
        if (display instanceof TextDisplay td && rawText != null) {
            String parsed = PlaceholderAPI.setPlaceholders(player, rawText);
            td.setText(ColorUtils.format(parsed));
        }
    }
    public Display getDisplay() { return display; }
    public String getRawText() { return rawText; }
    public String getType() { return type; }
    public String getVariableName() { return variableName; }
    public ConfigurationSection getConfig() { return config; }
    public void onClick() { if (onClick != null) onClick.run(); }
}