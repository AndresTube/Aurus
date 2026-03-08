# Commands & Permissions

## Commands

| Command | Description |
|---|---|
| `/au open <menu_id> [player]` | Opens a menu for yourself or a target player |
| `/au close [player]` | Closes the active menu for yourself or a target player |
| `/au reload` | Reloads all menu files and config |
| `/au debug` | Toggles debug mode — logs button clicks, actions, and input events to console |

Alias: `/aurus`

## Permissions

| Permission | Description | Default |
|---|---|---|
| `aurus.admin` | Access to all `/au` subcommands | op |

---

## config.yml — Cursor

```yaml
cursor:
  type: TEXT
  value: "●"
  size: 1.0
```

| Key | Type | Default | Description |
|---|---|---|---|
| `type` | string | `TEXT` | Cursor display type: `TEXT`, `ITEM`, or `BLOCK` |
| `value` | string | `●` | The character (for TEXT), material name (for ITEM/BLOCK). Supports PAPI placeholders |
| `size` | double | `1.0` | Scale of the cursor |

```yaml
cursor:
  type: ITEM
  value: ARROW
  size: 0.5
```
