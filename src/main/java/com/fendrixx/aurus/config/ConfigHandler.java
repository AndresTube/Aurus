package com.fendrixx.aurus.config;
import com.fendrixx.aurus.Aurus;
import com.fendrixx.aurus.util.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
public class ConfigHandler {
    private final Aurus plugin;
    private FileConfiguration config;
    private final Map<String, FileConfiguration> menus = new HashMap<>();
    private final String prefix = "<dark_gray>[<gradient:dark_purple:yellow> Aurus </gradient><dark_gray>] ";
    public ConfigHandler(Aurus plugin) {
        this.plugin = plugin;
        loadConfig();
        loadMenus();
    }
    public void loadConfig() {
        try {
            plugin.saveDefaultConfig();
            plugin.reloadConfig();
            this.config = plugin.getConfig();
            Bukkit.getConsoleSender().sendMessage(ColorUtils.format(prefix + "<dark_gray>[<green>✔<dark_gray>] <green>Main config loaded successfully!"));
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ColorUtils.format(prefix + "<dark_gray>[<red>✘<dark_gray>] <red>Failed to load config: " + e.getMessage()));
            this.config = new YamlConfiguration();
        }
    }
    public void loadMenus() {
        menus.clear();
        File folder = new File(plugin.getDataFolder(), "menus");
        if (!folder.exists()) {
            folder.mkdirs();
            try {
                plugin.saveResource("menus/welcome_server.yml", false);
                plugin.saveResource("menus/user_profile.yml", false);
                plugin.saveResource("menus/name_menu.yml", false);
                plugin.saveResource("menus/animated_menu.yml", false);
                plugin.saveResource("menus/pixelart.yml", false);
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(ColorUtils.format(prefix + "<dark_gray>[<yellow>!<dark_gray>] <gray>Could not save default menus."));
            }
        }
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;
        for (File file : files) {
            try {
                String name = file.getName().replace(".yml", "");
                YamlConfiguration menuConfig = new YamlConfiguration();
                menuConfig.load(file);
                menus.put(name, menuConfig);
                Bukkit.getConsoleSender().sendMessage(ColorUtils.format(prefix + "<dark_gray>[<green>✔<dark_gray>] <gray>Loaded menu: <white>" + name));
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(ColorUtils.format(prefix + "<dark_gray>[<red>✘<dark_gray>] <red>Error loading <yellow>" + file.getName() + "<red>: " + e.getMessage()));
            }
        }
    }
    public ConfigurationSection getMenuSection(String menuId) {
        if (menuId == null || menuId.isEmpty()) return null;
        for (FileConfiguration menuFile : menus.values()) {
            if (menuFile.isConfigurationSection(menuId)) {
                return menuFile.getConfigurationSection(menuId);
            }
        }
        if (config != null && config.isConfigurationSection("menus." + menuId)) {
            return config.getConfigurationSection("menus." + menuId);
        }
        return null;
    }
    public Set<String> getMenuKeys() {
        Set<String> keys = new HashSet<>();
        for (FileConfiguration menuFile : menus.values()) {
            keys.addAll(menuFile.getKeys(false));
        }
        if (config != null && config.isConfigurationSection("menus")) {
            keys.addAll(config.getConfigurationSection("menus").getKeys(false));
        }
        return keys;
    }
    public String getMenuType(String menuId) {
        ConfigurationSection section = getMenuSection(menuId);
        return section != null ? section.getString("type", "FULL") : "FULL";
    }
    public String getGlobalCursor() {
        return config != null ? config.getString("cursor", "<red><bold>!") : "<red><bold>!";
        // if you got a "!" red bold cursor, take in count that your cursor doesn't was found in the config.yml
    }
    public Location getLocation(String menuId) {
        ConfigurationSection section = getMenuSection(menuId);
        if (section == null || !section.isConfigurationSection("location")) return null;
        ConfigurationSection loc = section.getConfigurationSection("location");
        try {
            return new Location(
                    Bukkit.getWorld(loc.getString("world", "world")),
                    loc.getDouble("x"),
                    loc.getDouble("y"),
                    loc.getDouble("z"),
                    (float) loc.getDouble("yaw"),
                    (float) loc.getDouble("pitch")
            );
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ColorUtils.format(prefix + "<dark_gray>[<red>✘<dark_gray>] <red>Invalid location for menu: " + menuId));
            return null;
        }
    }
    public ConfigurationSection getCursorSection() {
        return config != null ? config.getConfigurationSection("cursor") : null;
    }
    public void reload() {
        loadConfig();
        loadMenus();
    }
}