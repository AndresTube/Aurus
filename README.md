# Aurus

![Version](https://img.shields.io/badge/Version-1.1.4_BETA-yellow?style=flat-square)
![Software](https://img.shields.io/badge/Software-Paper-blue?style=flat-square)
![Author](https://img.shields.io/badge/Author-Fendrixx-red?style=flat-square)

---

Aurus is a **packet-based 3D menu system** for modern Paper servers.
It renders interactive menus directly in front of the player using fake entities and a custom camera, instead of classic chest inventories.

Built for **Paper 1.20+**, designed to be flexible, animated, and fully configurable through YAML.

## How it works

When a menu is opened:

- The player is mounted on an invisible camera entity
- Fake entities (text, items, blocks, players, mobs) are spawned via packets — **only the viewer can see them**
- A floating cursor follows the player's head movement
- Clicks are detected through packet listening

No inventory windows. No chest GUI. Just world-space menus.

## Requirements

| Requirement | Version |
|---|---|
| Paper Server | 1.20.1+ |
| Java | 17+ |
| [PacketEvents](https://github.com/retrooper/packetevents) | 2.x (separate plugin) |
| PlaceholderAPI *(optional)* | Any |

## Installation

1. Install **PacketEvents 2.x** on your Paper server.
2. Drop `Aurus-1.1.4-BETA.jar` into your `plugins/` folder.
3. Start the server — default menus are created in `plugins/Aurus/menus/`.
4. Open a menu in-game: `/au open welcome_server`

## Component Types

| Type | Description |
|---|---|
| `TEXT` | Non-interactive text label |
| `BUTTON` | Clickable text with actions |
| `INPUT` | Chat input prompt, stores variables |
| `ITEM` | Displays a Minecraft item |
| `BLOCK` | Displays a Minecraft block |
| `ENTITY` | Displays a fake mob (zombie, villager, etc.) |
| `PLAYER` | Displays a fake player NPC with skin and nametag |

## Quick Example

```yaml
welcome_menu:
  distance: 2.5
  update-in-ticks: 20
  on-open:
    - "[sound] entity.player.levelup, 1.0, 1.5"
    - "[message] <green>Welcome to the server!"
  on-close:
    - "[message] <red>See you later!"
  components:
    title:
      type: TEXT
      text: "<gold>Welcome, <aqua>%player_name%!"
      x: 0.0
      y: 2.0
      size: 2.0
      background: false
    play_btn:
      type: BUTTON
      text: "<green>[ Play ]"
      x: 0.0
      y: 0.0
      size: 1.5
      sound: "ui.button.click"
      actions:
        - "[close]"
        - "[player] spawn"
        - "[sound] entity.player.levelup, 1.0, 1.2"
        - "[message] <green>Teleported!"
    npc:
      type: PLAYER
      skin: "Notch"
      nametag: "<gold>Notch"
      x: 2.0
      y: -0.5
      rotation:
        x: -30
        x-head: -45
        y-head: 10
```

## Actions

Actions run top-to-bottom when a BUTTON or INPUT is clicked.

| Action | Example |
|---|---|
| `[close]` | Closes the menu |
| `[player] <cmd>` | `[player] spawn` |
| `[console] <cmd>` | `[console] give %player% diamond 1` |
| `[message] <text>` | `[message] <green>Hello!` |
| `[broadcast] <text>` | `[broadcast] <gold>%player% joined!` |
| `[openmenu] <id>` | `[openmenu] settings_menu` |
| `[sound] <key>, <vol>, <pitch>` | `[sound] entity.player.levelup, 1.0, 1.2` |

## Commands

| Command | Description |
|---|---|
| `/au open <menu_id> [player]` | Opens a menu for yourself or a target player |
| `/au close [player]` | Closes the active menu |
| `/au reload` | Reloads config and menus |
| `/au debug` | Toggles debug mode |

Alias: `/aurus`

## Permissions

| Permission | Description | Default |
|---|---|---|
| `aurus.admin` | Access to all `/au` subcommands | op |

## Features

- **Packet-based rendering** — all entities are fake, no server-side entity overhead
- **7 component types** — TEXT, BUTTON, INPUT, ITEM, BLOCK, ENTITY, PLAYER
- **Animations** — math formula-driven scale, rotation, and position animations
- **On-open / on-close actions** — run actions when a menu opens or closes
- **Fixed locations** — optionally teleport players to a set location for the menu
- **MiniMessage + PAPI** — full text formatting and placeholder support
- **Custom click sounds** — per-button sound customization
- **Independent head rotation** — PLAYER and ENTITY support separate body/head angles
- **Z-depth axis** — layer components at different depths
- **Debug mode** — logs clicks, actions, and input events to console

## Documentation

See the full documentation in the [Documentation](./Documentation/index.md) folder.

## WARNING

This plugin is currently in **beta**. If you find bugs, please open an [issue on GitHub](https://github.com/AndresTube/Aurus/issues).

#### Author
- Fendrixx
