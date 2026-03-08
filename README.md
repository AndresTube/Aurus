# Aurus

![Version](https://img.shields.io/badge/Version-1.1.0_BETA-yellow?style=flat-square)
![Software](https://img.shields.io/badge/Software-Paper-blue?style=flat-square)
![Author](https://img.shields.io/badge/Author-Fendrixx-red?style=flat-square)

---

Aurus is a packet-based 3D menu system for modern Spigot servers.
It renders interactive menus directly in front of the player using display entities and a custom camera, instead of classic inventories.

Built for 1.20+, designed to be flexible, animated, and fully configurable through YAML.

## How it works

When a menu is opened:

- The player is spectated through a fake camera entity
- Display entities are spawned in front of them
- A floating cursor follows their head movement
- Interactions are handled through packet listening

No inventory windows. No chest GUI. Just world-space menus.

## Requirements

Spigot or Paper 1.20+

PacketEvents (bundled inside the plugin jar)

PlaceholderAPI (optional, for placeholders)

## Commands

``/aurus open <menu_id> [player]``
Opens a menu by id, optionally for another player.

``/aurus close [player]``
Closes the active menu.

``/aurus reload``
Reloads config and menus.

``/aurus debug``
Toggles debug mode.

Alias: ``/au``

## Permissions

``aurus.admin``
Default: op

Currently only used for administrative control.

## Configuration

Menus are stored in:

``/plugins/Aurus/menus/``

Each .yml file can contain one or more menu sections.

Example structure:

```yaml
welcome_menu:
  components:
    title:
      type: "TEXT"
      text: "<yellow>Welcome"
      x: 0.0
      y: 2.5
      size: 3.0
```

## Documentation

See the full documentation in the [Documentation](./Documentation/index.md) folder.

## WARNING

This plugin is currently in beta, if you find errors, don't get mad and make an issue in GitHub.

#### Author
- Fendrixx