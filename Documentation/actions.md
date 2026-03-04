# Actions

Actions are strings inside a button's `actions` list. They run top-to-bottom when a button is clicked.

## Available Actions

| Action | Example | Description |
|---|---|---|
| `[close]` | `[close]` | Closes the menu |
| `[player] <cmd>` | `[player] spawn` | Runs a command as the player |
| `[console] <cmd>` | `[console] give %player% diamond 1` | Runs a command from console |
| `[message] <text>` | `[message] <green>Hello!` | Sends a message to the player |
| `[broadcast] <text>` | `[broadcast] <gold>%player% joined!` | Broadcasts to all players |
| `[openmenu] <id>` | `[openmenu] user_profile` | Closes current menu and opens another |
| `[sound] <sound>, <vol>, <pitch>` | `[sound] ENTITY_PLAYER_LEVELUP, 1.0, 1.2` | Plays a sound to the player |

## Example

```yaml
my_button:
  type: BUTTON
  text: "<aqua>Go to Spawn"
  x: 0.0
  y: 0.0
  size: 1.5
  actions:
    - "[close]"
    - "[player] spawn"
    - "[sound] ENTITY_PLAYER_LEVELUP, 1.0, 1.0"
    - "[message] <green>Teleported!"
```

## Notes

- `%player%` is always replaced with the player's name in any action string.
- All other PlaceholderAPI placeholders are also parsed in action strings.
- `[openmenu]` closes the current menu before opening the new one (1-tick delay).
