package com.fendrixx.aurus;
import com.fendrixx.aurus.commands.AurusCommand;
import com.fendrixx.aurus.config.ConfigHandler;
import com.fendrixx.aurus.expansion.PAPIExpansion;
import com.fendrixx.aurus.listeners.InteractionListener;
import com.fendrixx.aurus.listeners.MoveListener;
import com.fendrixx.aurus.menu.Menu;
import com.fendrixx.aurus.menu.MenuManager;
import com.fendrixx.aurus.processors.ActionProcessor;
import com.fendrixx.aurus.processors.InputProcessor;
import com.fendrixx.aurus.util.ColorUtils;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
public class Aurus extends JavaPlugin {
    private MenuManager menuManager;
    private BukkitAudiences adventure;
    private ConfigHandler configHandler;
    private ActionProcessor actionProcessor;
    private InputProcessor inputProcessor;
    private PAPIExpansion aurusExpansion;
    private String prefix = "<dark_gray>[<gradient:dark_purple:yellow> Aurus </gradient><dark_gray>] ";
    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
        Bukkit.getConsoleSender().sendMessage(ColorUtils.format(prefix + "[<yellow>↺<dark_gray>] <yellow>Plugin loading..."));
    }
    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();
        Bukkit.getConsoleSender().sendMessage(ColorUtils.format(prefix + "[<yellow>↺<dark_gray>] <yellow>Loading packetevents..."));
        this.adventure = BukkitAudiences.create(this);
        Bukkit.getConsoleSender().sendMessage(ColorUtils.format(prefix + "[<yellow>↺<dark_gray>] <yellow>Loading Adventure (MiniMessage)..."));
        this.configHandler = new ConfigHandler(this);
        this.menuManager = new MenuManager(this);
        this.inputProcessor = new InputProcessor(this);
        this.actionProcessor = new ActionProcessor(this);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getConsoleSender().sendMessage(ColorUtils.format(prefix + "[<yellow>↺<dark_gray>] <aqua>PlaceholderAPI <yellow>Founded, loading placeholders..."));
            new PAPIExpansion(this.inputProcessor, this).register();
        }
        registerCommands();
        InteractionListener interactionListener = new InteractionListener(this);
        getServer().getPluginManager().registerEvents(interactionListener, this);
        getServer().getPluginManager().registerEvents(new MoveListener(this), this);
        getServer().getPluginManager().registerEvents(this.inputProcessor, this);

        // menu interaction with packetevents
        // (if I use bukkit normal one, it gives me a "Cannot interact with your" error"
        PacketEvents.getAPI().getEventManager().registerListener(
                new com.github.retrooper.packetevents.event.PacketListener() {
                    @Override
                    public void onPacketReceive(com.github.retrooper.packetevents.event.PacketReceiveEvent event) {
                        if (event.getPacketType() == com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client.INTERACT_ENTITY) {
                            com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity packet =
                                    new com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity(event);
                            if (packet.getAction() == com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                                org.bukkit.entity.Player p = (org.bukkit.entity.Player) event.getPlayer();
                                Menu menu = menuManager.getActiveMenu(p.getUniqueId());
                                if (menu != null) {
                                    org.bukkit.Bukkit.getScheduler().runTask(Aurus.this, menu::handleInteraction);
                                }
                            }
                        }
                    }
                },
                com.github.retrooper.packetevents.event.PacketListenerPriority.NORMAL
        );
        sendStartupMessage();
    }
    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ColorUtils.format(prefix + "[<yellow>↺<dark_gray>] <yellow>Disabling menus..."));
        if (this.menuManager != null) {
            this.menuManager.closeAll();
        }
        Bukkit.getConsoleSender().sendMessage(ColorUtils.format(prefix + "[<yellow>↺<dark_gray>] <yellow>Disabling adventure..."));
        if (this.adventure != null) {
            this.adventure.close();
        }
        Bukkit.getConsoleSender().sendMessage(ColorUtils.format(prefix + "[<yellow>↺<dark_gray>] <yellow>Disabling packetevents..."));
        PacketEvents.getAPI().terminate();
        Bukkit.getConsoleSender().sendMessage(ColorUtils.format(prefix + "[<green>✔<dark_gray>] <green>Plugin disabled!"));
    }
    private void registerCommands() {
        if (getCommand("aurus") != null) {
            AurusCommand cmd = new AurusCommand(this);
            getCommand("aurus").setExecutor(cmd);
            getCommand("aurus").setTabCompleter(cmd);
        }
    }
    // beautiful startup message (kinda useless, but idc)
    private void sendStartupMessage() {
        Bukkit.getConsoleSender().sendMessage(ColorUtils.format(prefix + "[<green>✔<dark_gray>] <green>Plugin enabled!"));
        Bukkit.getConsoleSender().sendMessage(ColorUtils.format(prefix + "[<yellow>!<dark_gray>] <gold>ty for using my plugin! <dark_gray>~ Fendrixx"));
        Bukkit.getConsoleSender().sendMessage(ColorUtils.format("""
                \s
                <gradient:dark_purple:yellow>| ░█▀▀█⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀</gradient>
                <gradient:dark_purple:yellow>| ░█▄▄█░█ ░█ █▀▀█░█ ░█░██▀▀</gradient>
                <gradient:dark_purple:yellow>| ░█ ░█░█▄▄█ █▀█▄░█▄▄█ ▄▄█▀</gradient>
                <dark_purple>|
                <dark_purple>|<gradient:dark_purple:yellow> - Aurus v1.0.0-<yellow>BETA
                <dark_purple>|<gradient:dark_gray:white> - Author: Fendrixx
                \s
                """));
    }
    public BukkitAudiences adventure() { return this.adventure; }
    public MenuManager getMenuManager() { return this.menuManager; }
    public ConfigHandler getConfigHandler() { return this.configHandler; }
    public ActionProcessor getActionProcessor() {return this.actionProcessor; }
    public InputProcessor getInputProcessor() {return this.inputProcessor; }
}