# Animations

Any component with a Display entity (TEXT, BUTTON, INPUT, ITEM, BLOCK) can have an `animations` block to make it move or pulse over time.

The variable `t` increases by `0.05` every tick (reaches `~3.0` per second).

## Available Formulas

| Key | Description |
|---|---|
| `scale-formula` | Sets the scale of the component each tick |
| `rotation-formula` | Sets the Z-rotation in degrees each tick |
| `x-formula` | Adds an offset to the component's base `x` position |
| `y-formula` | Adds an offset to the component's base `y` position |
| `z-formula` | Adds an offset to the component's base `z` position |

## Examples

### Breathing pulse
```yaml
my_button:
  type: BUTTON
  text: "<gold>[ Play ]"
  x: 0.0
  y: 0.0
  size: 1.5
  animations:
    scale-formula: "1.5 + 0.1 * sin(t * 3)"
```

### Spinning item
```yaml
my_item:
  type: ITEM
  material: NETHER_STAR
  x: 0.0
  y: 1.0
  size: 1.2
  animations:
    rotation-formula: "t * 60"
```

### Floating up/down
```yaml
my_text:
  type: TEXT
  text: "<aqua>★ Welcome ★"
  x: 0.0
  y: 2.0
  background: false
  size: 2.0
  animations:
    y-formula: "0.15 * sin(t * 2)"
```

### Orbiting
```yaml
my_item:
  type: ITEM
  material: BLAZE_ROD
  x: 0.0
  y: 0.0
  size: 0.8
  animations:
    x-formula: "1.5 * cos(t)"
    y-formula: "1.5 * sin(t)"
```

### Moving forward/backward
```yaml
my_block:
  type: BLOCK
  material: DIAMOND_BLOCK
  x: 0.0
  y: 0.0
  z: 1.0
  size: 0.5
  animations:
    z-formula: "0.3 * sin(t * 2)"
```

---

> **Note:** Animations only work on display components (TEXT, BUTTON, INPUT, ITEM, BLOCK). ENTITY and PLAYER types do not support animations.

> **Tip:** Formula evaluation uses [exp4j](https://www.objecthunter.net/exp4j/). Supported functions: `sin`, `cos`, `tan`, `abs`, `sqrt`, `log`, `pow`, etc.
