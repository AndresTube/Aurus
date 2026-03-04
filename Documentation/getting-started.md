# Getting Started

## Requirements

| Requirement | Version |
|---|---|
| Minecraft Spigot / Paper Server | 1.20.1 + |
| Java | 17 + |
| PlaceholderAPI *(optional)* | Any |

## Installation

1. Drop `Aurus-1.0.0-BETA.jar` into your `plugins/` folder.
2. Start the server — default menus are created in `plugins/Aurus/menus/`.
3. Open a menu in-game:

```
/au open welcome_server
```

## File Structure

```
plugins/
  Aurus/
    config.yml         ← cursor settings, global options
    menus/
      welcome_server.yml
      user_profile.yml
      ...
```

---

> **Tip:** You can hot-reload menus with `/au reload` without restarting the server.
