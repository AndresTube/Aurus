# Placeholders

Aurus supports PlaceholderAPI in all text fields and action strings.

## Using PAPI Placeholders in Menus

Just write the placeholder directly in `text` or action strings:

```yaml
info:
  type: TEXT
  text: "<gray>Player: <white>%player_name%\n<gray>Ping: <green>%player_ping%ms"
  x: 0.0
  y: 0.8
  size: 1.1
```

They refresh every `update-in-ticks` ticks.

---

## Aurus' Own Placeholders

These are exposed by Aurus to other plugins via PAPI.

| Placeholder | Returns |
|---|---|
| `%aurus_variable_<name>%` | The stored value of an INPUT variable named `<name>` |

### Example

1. Create an INPUT component with `variable_name: my_nickname`:
```yaml
nick_input:
  type: INPUT
  variable_name: my_nickname
  text: "<yellow>Set Nickname"
  x: 0.0
  y: 0.0
  size: 1.2
```

2. Use it anywhere via PAPI:
```
%aurus_variable_my_nickname%
```

---

## Legacy Color Code Support

Both `§` (section sign) and `&` codes are automatically converted before rendering.

```yaml
text: "&aGreen text from a plugin"     # works fine
text: "§cRed text"                     # works fine
text: "<green>MiniMessage also works"  # works fine
```

You can mix them freely.
