# BarMaster

BarMaster is a display-only RuneLite external plugin that adds configurable status bars for player HP, prayer, special attack energy, and target HP. It does not send any input to the game or read scene state more than necessary.

## Features

- **Player status bars**: HP, prayer, and special attack energy.
- **Target HP bar**: Shows the current or last interacted target. NPCs use estimated real HP when RuneLite has max HP data, otherwise BarMaster falls back to ratio/scale.
- **Grouped or independent layout**: Group the three player bars into a single frame, or use independent overlays that can be positioned separately.
- **Style configuration**: bar width/height, fill colors, background color, border color/thickness, font size, and optional percentage text.
- **Pure Java helpers**: `BarRenderer` handles bar geometry/labels and `TargetHpEstimator` handles NPC HP estimation; both are covered by unit tests.

## Configuration

All options are available in the RuneLite configuration panel under **BarMaster**.

| Section | Option | Description |
| --- | --- | --- |
| Layout | Group player bars | Merge HP/prayer/special into one frame |
| Style | Bar width / height | Size of each bar in pixels |
| Style | Show text labels | Toggle current/max value text |
| Style | Show percentage | Append a percentage to labels |
| Style | Hide full player bars | Hide HP/prayer/special when full |
| Style | Font size / border thickness | Tune WoW-style bar text and border appearance |
| Style | Text / background / border color | Global text, empty-bar, and border colors |
| Player Hitpoints | Show player HP bar / HP color | Toggle and color |
| Player Prayer | Show player prayer bar / Prayer color | Toggle and color |
| Special Attack | Show special attack bar / Special color | Toggle and color |
| Target Hitpoints | Show target HP bar / Target HP color | Toggle and color |
| Target Hitpoints | Target display style | Ratio/scale or estimated real NPC HP when available |
| Target Hitpoints | Target source mode | Current interaction only or remember last interaction |
| Target Hitpoints | Target bar width / height | Optional target-only size override |

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
