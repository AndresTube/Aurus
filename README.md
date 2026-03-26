# Aurus

![Version](https://img.shields.io/badge/Version-1.0.0-green?style=flat-square)
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
2. Drop `Aurus-1.0.0.jar` into your `plugins/` folder.
3. Start the server — default menus are created in `plugins/Aurus/menus/`.
4. Open a menu in-game: `/au open welcome_server`

## Areas

Components are organized into **areas** — bounded containers that group components together. Each area has its own position, size, update interval, and optional open/close animations.

| Area Type | Description |
|---|---|
| `STATIC` | Fixed area. Components stay in place. |
| `SCROLL` | Scrollable area. Scroll vertically using the mouse wheel. Components outside bounds are hidden. |

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
  on-open:
    - "[sound] entity.player.levelup, 1.0, 1.5"
    - "[message] <green>Welcome to the server!"
  on-close:
    - "[message] <red>See you later!"
  areas:
    main:
      type: STATIC
      x: 0
      y: 0
      size-x: 8.0
      size-y: 6.0
      update-ticks: 20
      open-animation: scale
      close-animation: scale
      animation-duration: 10
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

## Area Animations

Areas can play transition animations when the menu opens or closes.

| Animation | Effect |
|---|---|
| `NONE` | No animation |
| `SCALE` | Scale in/out from zero |
| `UP` | Slide from above |
| `DOWN` | Slide from below |
| `LEFT` | Slide from the left |
| `RIGHT` | Slide from the right |

```yaml
areas:
  sidebar:
    type: SCROLL
    x: -3.0
    y: 0
    size-x: 2.0
    size-y: 3.0
    open-animation: left
    animation-duration: 8
    components:
      ...
```

## Formula Animations

Components support math formula-driven animations using [exp4j](https://www.objecthunter.net/exp4j/) syntax. The variable `t` increases by `0.05` every tick.

```yaml
components:
  logo:
    type: ITEM
    material: NETHER_STAR
    x: 0.0
    y: 1.0
    size: 1.0
    animations:
      x-formula: "cos(t) * 0.5"
      y-formula: "sin(t) * 0.5"
      scale-formula: "1 + (abs(sin(t * 2)) * 0.2)"
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

## API

Aurus provides a developer API for creating and managing menus programmatically.

```java
AurusAPI api = AurusAPI.get();

MenuData menu = api.createMenu("shop")
    .distance(2.5)
    .addArea("main", area -> area
        .type(AreaType.STATIC)
        .position(0, 0)
        .size(6.0, 4.0)
        .openAnimation(AnimationType.SCALE)
        .animationDuration(10)
        .addComponent("title", comp -> comp
            .type(ComponentType.TEXT)
            .text("<gold>Shop")
            .position(0, 1.5, 1)
            .size(2.0))
        .addComponent("buy", comp -> comp
            .type(ComponentType.BUTTON)
            .text("<green>Buy Sword")
            .position(0, 0, 1)
            .action("[console] give %player% diamond_sword")
            .action("[close]")))
    .build();

api.openMenu(player, menu);
```

## Features

- **Packet-based rendering** — all entities are fake, no server-side entity overhead
- **Areas system** — group components into bounded containers with independent settings
- **SCROLL areas** — vertically scrollable component lists with visibility clipping
- **5 area animations** — SCALE, UP, DOWN, LEFT, RIGHT transitions on open/close
- **7 component types** — TEXT, BUTTON, INPUT, ITEM, BLOCK, ENTITY, PLAYER
- **Formula animations** — math formula-driven scale, rotation, and position animations
- **On-open / on-close actions** — run actions when a menu opens or closes
- **Fixed locations** — optionally teleport players to a set location for the menu
- **MiniMessage + PAPI** — full text formatting and placeholder support
- **Per-area update ticks** — each area refreshes placeholders independently
- **Custom click sounds** — per-button sound customization
- **Independent head rotation** — PLAYER and ENTITY support separate body/head angles
- **Z-depth axis** — layer components at different depths
- **Hover states** — swap component appearance on cursor hover
- **Debug mode** — logs clicks, actions, and input events to console
- **Developer API** — create menus programmatically with `MenuBuilder` and `AreaBuilder`

## Documentation

Full documentation available at [aurus.fendrixx.com](https://aurus.fendrixx.com)

## Support

Found a bug? Open an [issue on GitHub](https://github.com/AndresTube/Aurus/issues) or report it in the [Discord server](https://discord.gg/w7ef4DCqvK).

#### Author
- Fendrixx
