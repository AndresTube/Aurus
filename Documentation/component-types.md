# Component Types

## TEXT

A non-interactive text label.

```yaml
my_text:
  type: TEXT
  text: "<gray>Server version: <white>1.21.4"
  background: false
  x: 0.0
  y: 1.5
  size: 1.2
```

---

## BUTTON

A clickable text label. Fires `actions` when clicked.

```yaml
my_button:
  type: BUTTON
  text: "<green>[ Play ]"
  x: 0.0
  y: 0.0
  size: 1.5
  actions:
    - "[close]"
    - "[player] spawn"
```

---

## INPUT

Opens a chat input box when clicked. Stores the player's response in a named variable.

```yaml
name_input:
  type: INPUT
  text: "<yellow>Click to set name"
  variable_name: player_custom_name
  x: 0.0
  y: 0.0
  size: 1.2
  actions:
    - "[message] <green>Name saved!"
```

Access the variable in PAPI: `%aurus_variable_player_custom_name%`

---

## ITEM

Displays a Minecraft item.

```yaml
my_item:
  type: ITEM
  material: DIAMOND_SWORD
  x: 1.5
  y: 0.0
  size: 1.0
```

---

## BLOCK

Displays a Minecraft block.

```yaml
my_block:
  type: BLOCK
  material: GRASS_BLOCK
  x: -1.5
  y: 0.0
  size: 0.8
```

---

> **Text Formatting:** All `text` fields support MiniMessage tags (`<red>`, `<gradient:...>`, etc.) and legacy § codes. PlaceholderAPI placeholders (`%placeholder%`) are also supported.
