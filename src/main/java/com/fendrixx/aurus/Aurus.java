package com.fendrixx.aurus;

import com.fendrixx.aurus.commands.AurusCommand;
import com.fendrixx.aurus.config.ConfigHandler;
import com.fendrixx.aurus.expansion.PAPIExpansion;
import com.fendrixx.aurus.listeners.InteractionListener;
import com.fendrixx.aurus.listeners.MoveListener;
import com.fendrixx.aurus.menu.MenuManager;
import com.fendrixx.aurus.processors.ActionProcessor;
import com.fendrixx.aurus.processors.InputProcessor;
import com.fendrixx.aurus.util.ColorUtils;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Aurus extends JavaPlugin {

    private MenuManager menuManager;
    private BukkitAudiences adventure;
    private ConfigHandler configHandler;
    private ActionProcessor actionProcessor;
    private InputProcessor inputProcessor;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();

        Bukkit.getConsoleSender().sendMessage(ColorUtils.format(
                "<dark_gray>[<gradient:dark_purple:yellow> Aurus </gradient><dark_gray>] [<yellow>↺<dark_gray>] <yellow>Plugin loading..."));
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();
        this.adventure = BukkitAudiences.create(this);

        this.configHandler = new ConfigHandler(this);
        this.menuManager = new MenuManager(this);
        this.actionProcessor = new ActionProcessor(this);
        this.inputProcessor = new InputProcessor(this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PAPIExpansion(this.inputProcessor, this).register();
        }

        registerCommands();

        InteractionListener interactionListener = new InteractionListener(this);
        getServer().getPluginManager().registerEvents(interactionListener, this);
        getServer().getPluginManager().registerEvents(new MoveListener(this), this);
        getServer().getPluginManager().registerEvents(this.inputProcessor, this);

        registerPacketListener(interactionListener);

        sendStartupMessage();
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ColorUtils.format(
                "<dark_gray>[<gradient:dark_purple:yellow> Aurus </gradient><dark_gray>] [<yellow>↺<dark_gray>] <yellow>Disabling menus..."));
        if (this.menuManager != null) {
            this.menuManager.closeAll();
        }

        Bukkit.getConsoleSender().sendMessage(ColorUtils.format(
                "<dark_gray>[<gradient:dark_purple:yellow> Aurus </gradient><dark_gray>] [<yellow>↺<dark_gray>] <yellow>Disabling adventure..."));
        if (this.adventure != null) {
            this.adventure.close();
        }

        Bukkit.getConsoleSender().sendMessage(ColorUtils.format(
                "<dark_gray>[<gradient:dark_purple:yellow> Aurus </gradient><dark_gray>] [<yellow>↺<dark_gray>] <yellow>Disabling packetevents..."));
        PacketEvents.getAPI().terminate();

        Bukkit.getConsoleSender().sendMessage(ColorUtils.format(
                "<dark_gray>[<gradient:dark_purple:yellow> Aurus </gradient><dark_gray>] [<green>✔<dark_gray>] <green>Plugin disabled!"));
    }

    private void registerCommands() {
        if (getCommand("aurus") != null) {
            AurusCommand cmd = new AurusCommand(this);
            getCommand("aurus").setExecutor(cmd);
            getCommand("aurus").setTabCompleter(cmd);
        }
    }

    private void registerPacketListener(InteractionListener listener) {
        PacketEvents.getAPI().getEventManager().registerListener(new PacketListenerAbstract() {
            @Override
            public void onPacketReceive(PacketReceiveEvent event) {
                if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
                    Player player = (Player) event.getPlayer();
                    if (menuManager.getActiveMenu(player.getUniqueId()) != null) {
                        event.setCancelled(true);
                        Bukkit.getScheduler().runTask(Aurus.this, () -> listener.handle3DClick(player));
                    }
                }
            }
        });
    }

    private void sendStartupMessage() {
        Bukkit.getConsoleSender().sendMessage(ColorUtils.format(
                "<dark_gray>[<gradient:dark_purple:yellow> Aurus </gradient><dark_gray>] [<green>✔<dark_gray>] <green>Plugin enabled!"));
        Bukkit.getConsoleSender().sendMessage(ColorUtils.format(
                "<dark_gray>[<gradient:dark_purple:yellow> Aurus </gradient><dark_gray>] [<yellow>!<dark_gray>] <gold>ty for using my plugin! <dark_gray>~ Fendrixx"));
        Bukkit.getConsoleSender().sendMessage(ColorUtils.format("""
                \s
                <gradient:dark_purple:yellow>| ░█▀▀█⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀</gradient>
                <gradient:dark_purple:yellow>| ░█▄▄█░█ ░█ █▀▀█░█ ░█░██▀▀</gradient>
                <gradient:dark_purple:yellow>| ░█ ░█░█▄▄█ █▀█▄░█▄▄█ ▄▄█▀</gradient>
                <dark_purple>|<gradient:dark_gray:white> - Aurus 1.0.0-BETA by Fendrixx
                \s
                """));
    }

    public BukkitAudiences adventure() {
        return this.adventure;
    }

    public MenuManager getMenuManager() {
        return this.menuManager;
    }

    public ConfigHandler getConfigHandler() {
        return this.configHandler;
    }

    public ActionProcessor getActionProcessor() {
        return this.actionProcessor;
    }

    public InputProcessor getInputProcessor() {
        return this.inputProcessor;
    }
}
