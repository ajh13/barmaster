# BarMaster

BarMaster is a display-only RuneLite external plugin that adds configurable status bars for player HP, prayer, special attack energy, and target HP. It does not send any input to the game or read scene state more than necessary.

## Features

- **Player status bars**: HP, prayer, and special attack energy.
- **Target HP bar**: Shows the health ratio of the entity you are currently interacting with.
- **Grouped or independent layout**: Group the three player bars into a single frame, or use independent overlays that can be positioned separately.
- **Style configuration**: bar width/height, fill colors, background color, text color, and optional percentage text.
- **Pure Java helpers**: `BarRenderer` handles bar geometry, labels, and color interpolation and is covered by unit tests.

## Configuration

All options are available in the RuneLite configuration panel under **BarMaster**.

| Section | Option | Description |
| --- | --- | --- |
| Layout | Group player bars | Merge HP/prayer/special into one frame |
| Style | Bar width / height | Size of each bar in pixels |
| Style | Show text labels | Toggle current/max value text |
| Style | Show percentage | Append a percentage to labels |
| Style | Text / background color | Global text and empty-bar colors |
| Player Hitpoints | Show player HP bar / HP color | Toggle and color |
| Player Prayer | Show player prayer bar / Prayer color | Toggle and color |
| Special Attack | Show special attack bar / Special color | Toggle and color |
| Target Hitpoints | Show target HP bar / Target HP color | Toggle and color |

## Building

```bash
./gradlew build
```

## Testing

```bash
./gradlew test
```

## Running in development

```bash
./gradlew run
```

Then follow the [Using Jagex Accounts](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts) guide to log in to the development client.

## Plugin Hub safety

BarMaster is intentionally read-only and display-only:

- No reflection, JNI, JNA, native memory access, external processes, dynamic code loading, or serialization.
- no input injection, autotyping, or automated game actions.
- No network requests.
- No next-attack prediction, prayer switching, tile indicators, or other restricted PvM/PvP helpers.

## License

BSD-2-Clause. See [LICENSE](LICENSE).
