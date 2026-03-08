# Menu Configuration

Each `.yml` file inside `plugins/Aurus/menus/` can hold one or more menus. Menus can also be defined under `menus:` in `config.yml`.

## Root Keys

```yaml
my_menu:
  distance: 2.5          # How far the menu spawns from the player's eyes (blocks)
  update-in-ticks: 20    # How often placeholder text refreshes (1 = every tick)
  location: "world,100,64,200,0,0"  # Optional fixed location (world,x,y,z,yaw,pitch)
  components:
    ...
```

| Key | Type | Default | Description |
|---|---|---|---|
| `distance` | double | `2.5` | Distance from the player's eyes to the menu plane |
| `update-in-ticks` | int | `20` | Ticks between placeholder text refreshes |
| `location` | string | *(none)* | Optional. Teleports the player to a fixed location before opening the menu. Format: `world,x,y,z,yaw,pitch` |

## Component Keys (shared by all types)

| Key | Type | Default | Description |
|---|---|---|---|
| `type` | string | `BUTTON` | Component type: `TEXT`, `BUTTON`, `INPUT`, `ITEM`, `BLOCK`, `ENTITY`, `PLAYER` |
| `x` | double | `0.0` | Horizontal offset on the menu plane (+right / -left) |
| `y` | double | `0.0` | Vertical offset on the menu plane (+up / -down) |
| `z` | double | `1.0` | Depth offset on the menu plane (+forward / -backward from the menu origin) |
| `size` | double | `1.0` | Scale multiplier (also affects hitbox radius for buttons) |
| `background` | boolean | `true` | Show the dark background behind text displays (TEXT, BUTTON, INPUT only) |
| `shadow` | boolean | `false` | Enable text shadow rendering (TEXT, BUTTON, INPUT only) |
| `rotation.x/y/z` | double | `0` | Visual rotation in degrees (does not affect hitbox) |

## Type-Specific Keys

| Key | Applies to | Type | Default | Description |
|---|---|---|---|---|
| `text` | TEXT, BUTTON, INPUT | string | `""` | The displayed text. Supports MiniMessage and PAPI |
| `actions` | BUTTON, INPUT | string list | `[]` | List of actions to run on click |
| `variable_name` | INPUT | string | *(required)* | Name of the variable to store input in |
| `fallback-message` | INPUT | string | *(default prompt)* | Custom message shown when the input prompt opens |
| `material` | ITEM, BLOCK | string | `STONE` | Material name. Supports PAPI |
| `model-id` | ITEM | int | *(none)* | Custom model data for the item |
| `entity` | ENTITY | string | `ZOMBIE` | Entity type name (e.g. `ZOMBIE`, `VILLAGER`, `ARMOR_STAND`) |
| `skin` | PLAYER | string | *(viewer's name)* | Username to fetch skin from. Supports PAPI (e.g. `%player%`) |
| `nametag` | PLAYER | string | `""` | Text above the NPC. If empty, nametag is hidden |
| `sound` | BUTTON, INPUT | string | `minecraft:ui.button.click` | Custom click sound. `minecraft:` prefix is added automatically if omitted |

## Coordinate System

`x: 0, y: 0` is the **exact center of the player's screen** when the menu opens.

```
        y+
        |
  x-  --+--  x+
        |
        y-
```

`z` controls depth: `z: 1.0` (default) is the menu plane, higher values push forward, lower values push backward.

---

> **Tip:** Use `size` to control both visual scale and click area — a `size: 2.0` button has twice the click radius of a `size: 1.0` button.
