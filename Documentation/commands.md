# Commands & Permissions

## Commands

| Command | Description |
|---|---|
| `/au open <menu_id>` | Opens a menu for the sender |
| `/au reload` | Reloads all menu files and config |
| `/au list` | Lists all loaded menu IDs |

Alias: `/aurus`

## Permissions

| Permission | Description | Default |
|---|---|---|
| `aurus.admin` | Access to all `/au` subcommands | op |

---

## config.yml — Cursor

```yaml
cursor:
  value: "●"         # The character shown as the cursor
  size: 1.0          # Size of the cursor entity
```

You can use any unicode character or MiniMessage-formatted string as the cursor value.

```yaml
cursor:
  value: "<red>✦"
  size: 1.2
```
