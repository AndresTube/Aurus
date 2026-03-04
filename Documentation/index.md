# Aurus Documentation

> A 3D holographic menu system for Minecraft servers (1.20.1+)

## Pages

- [Getting Started](./getting-started.md)
- [Menu Configuration](./menu-configuration.md)
- [Component Types](./component-types.md)
- [Actions](./actions.md)
- [Animations](./animations.md)
- [Placeholders](./placeholders.md)
- [Commands & Permissions](./commands.md)

---

## Quick Example

```yaml
my_menu:
  distance: 2.5
  update-in-ticks: 20
  components:
    title:
      type: TEXT
      text: "<gold>Hello, <aqua>%player_name%!"
      x: 0.0
      y: 2.0
      size: 2.0
      background: false
    close_btn:
      type: BUTTON
      text: "<red>[ Close ]"
      x: 0.0
      y: -1.5
      size: 1.2
      actions:
        - "[close]"
```

Open it with: `/au open my_menu`
