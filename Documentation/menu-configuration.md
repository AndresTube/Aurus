# Menu Configuration

Each `.yml` file inside `plugins/Aurus/menus/` can hold one or more menus.

## Root Keys

```yaml
my_menu:
  distance: 2.5          # How far the menu spawns from the player's eyes (blocks)
  update-in-ticks: 20    # How often placeholder text refreshes (1 = every tick)
  components:
    ...
```

## Component Keys (shared by all types)

| Key | Type | Default | Description |
|---|---|---|---|
| `type` | string | `BUTTON` | Component type: `TEXT`, `BUTTON`, `INPUT`, `ITEM`, `BLOCK` |
| `x` | double | `0.0` | Horizontal offset on the menu plane (+right / -left) |
| `y` | double | `0.0` | Vertical offset on the menu plane (+up / -down) |
| `size` | double | `1.0` | Scale multiplier (also affects hitbox radius for buttons) |
| `background` | boolean | `true` | Show the dark background behind text displays |
| `rotation.x/y/z` | double | `0` | Visual rotation in degrees (does not affect hitbox) |

## Coordinate System

`x: 0, y: 0` is the **exact center of the player's screen** when the menu opens.

```
        y+
        |
  x-  --+--  x+
        |
        y-
```

---

> **Tip:** Use `size` to control both visual scale and click area — a `size: 2.0` button has twice the click radius of a `size: 1.0` button.
