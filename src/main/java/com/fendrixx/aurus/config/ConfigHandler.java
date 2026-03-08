package com.fendrixx.aurus.config;

import com.fendrixx.aurus.Aurus;
import com.fendrixx.aurus.util.ColorUtils;
import org.bukkit.Bukkit;
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
        checkScaleSupport();
    }

    public void loadConfig() {
        try {
            plugin.saveDefaultConfig();
            plugin.reloadConfig();
            this.config = plugin.getConfig();
            Bukkit.getConsoleSender().sendMessage(ColorUtils
                    .format(prefix + "<dark_gray>[<green>✔<dark_gray>] <green>Main config loaded successfully!"));
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ColorUtils
                    .format(prefix + "<dark_gray>[<red>✘<dark_gray>] <red>Failed to load config: " + e.getMessage()));
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
                Bukkit.getConsoleSender().sendMessage(ColorUtils
                        .format(prefix + "<dark_gray>[<yellow>!<dark_gray>] <gray>Could not save default menus."));
            }
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null)
            return;

        for (File file : files) {
            try {
                String name = file.getName().replace(".yml", "");
                YamlConfiguration menuConfig = new YamlConfiguration();
                menuConfig.load(file);
                menus.put(name, menuConfig);
                Bukkit.getConsoleSender().sendMessage(ColorUtils
                        .format(prefix + "<dark_gray>[<green>✔<dark_gray>] <gray>Loaded menu: <white>" + name));
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(
                        ColorUtils.format(prefix + "<dark_gray>[<red>✘<dark_gray>] <red>Error loading <yellow>"
                                + file.getName() + "<red>: " + e.getMessage()));
            }
        }
    }

    public ConfigurationSection getMenuSection(String menuId) {
        if (menuId == null || menuId.isEmpty())
            return null;

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

    public ConfigurationSection getCursorSection() {
        return config != null ? config.getConfigurationSection("cursor") : null;
    }

    public boolean deleteMenu(String menuId) {
        File folder = new File(plugin.getDataFolder(), "menus");
        File file = new File(folder, menuId + ".yml");
        if (file.exists() && file.delete()) {
            menus.remove(menuId);
            return true;
        }
        return false;
    }

    public void reload() {
        loadConfig();
        loadMenus();
        checkScaleSupport();
    }

    private void checkScaleSupport() {
        if (com.fendrixx.aurus.packets.FakeEntityFactory.supportsScale()) return;
        for (FileConfiguration menuFile : menus.values()) {
            for (String menuKey : menuFile.getKeys(false)) {
                ConfigurationSection comps = menuFile.getConfigurationSection(menuKey + ".components");
                if (comps == null) continue;
                for (String compKey : comps.getKeys(false)) {
                    ConfigurationSection c = comps.getConfigurationSection(compKey);
                    String type = c.getString("type", "BUTTON").toUpperCase();
                    if (("ENTITY".equals(type) || "PLAYER".equals(type)) && c.getDouble("size", 1.0) != 1.0) {
                        Bukkit.getConsoleSender().sendMessage(ColorUtils.format(
                                prefix + "<dark_gray>[<yellow>!<dark_gray>] <yellow>Component <white>" + compKey +
                                "</white> in <white>" + menuKey + "</white> uses size on " + type +
                                " but server is below 1.20.5 — size will be ignored."));
                    }
                }
            }
        }
    }
}