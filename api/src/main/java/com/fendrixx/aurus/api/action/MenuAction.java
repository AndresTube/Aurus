package com.fendrixx.aurus.api.action;

public class MenuAction {
    private final String action;

    public MenuAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public static MenuAction close() {
        return new MenuAction("[close]");
    }

    public static MenuAction playerCommand(String command) {
        return new MenuAction("[player] " + command);
    }

    public static MenuAction consoleCommand(String command) {
        return new MenuAction("[console] " + command);
    }

    public static MenuAction message(String miniMessage) {
        return new MenuAction("[message] " + miniMessage);
    }

    public static MenuAction broadcast(String miniMessage) {
        return new MenuAction("[broadcast] " + miniMessage);
    }

    public static MenuAction openMenu(String menuId) {
        return new MenuAction("[openmenu] " + menuId);
    }

    public static MenuAction sound(String key, float volume, float pitch) {
        return new MenuAction("[sound] " + key + ", " + volume + ", " + pitch);
    }

    @Override
    public String toString() {
        return action;
    }
}
