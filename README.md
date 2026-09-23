# BarMaster

BarMaster is a display-only RuneLite external plugin that adds configurable status bars for player HP, prayer, special attack energy, run energy, and target HP. It does not send any input to the game or read scene state more than necessary.

## Features

- **Player status bars**: HP, prayer, special attack energy, and run energy.
- **Target HP bar**: Shows the current or last interacted target. NPCs use estimated real HP when RuneLite has max HP data, otherwise BarMaster falls back to ratio/scale.
- **Nearby HP bars**: Optionally show overhead health-only bars for nearby NPCs and players with visible health.
- **Fixed, overhead, or both**: Show bars as movable RuneLite overlays, above character heads, or both at once. Player and NPC overhead stacks can independently sit left, right, above, or below an estimated native-UI exclusion region; left remains the default. Dimensions can scale with the character or remain constant on screen.
- **Optional native bar hiding**: Hide the game's built-in health-bar sprites while preserving native overhead UI like hitsplats, prayers, skulls, and chat.
- **Grouped or independent layout**: Stack player bars into one movable overlay, or use independent overlays that can be positioned separately.
- **Sleeker rendering**: Rounded borderless bars by default, horizontal or vertical orientation, stacked upright text on vertical bars, antialiased drawing, darker empty-bar background, and labels that auto-hide instead of becoming unreadable on skinny bars.
- **Text modes**: Show just numbers, just percent, or both numbers and percent; optional bar names can be enabled separately.
- **Pure Java helpers**: `BarRenderer` handles bar geometry/labels and `TargetHpEstimator` handles NPC HP estimation; both are covered by unit tests.
- **Combat-ready defaults**: Defaults to overhead horizontal bars, scale-with-character sizing, no extra overhead gap, and hidden native combat bars.

## Configuration

All options are available in the RuneLite configuration panel under **BarMaster**.

| Section | Option | Description |
| --- | --- | --- |
| Layout | Group player bars | Stack HP/prayer/special/run into one overlay or split them into independent overlays |
| Layout | Bar placement | Fixed overlays, overhead character bars, or both |
| Layout | Bar orientation | Horizontal or vertical bars |
| Layout | Overhead player X/Y offset | Move your overhead stack left/right and up/down |
| Layout | Overhead sizing | Keep overhead bars constant on-screen or scale them with the character |
| Layout | Overhead gap | Vertical gap from the estimated native overhead region; scales with the character in scale mode |
| Layout | Native overhead clearance | Horizontal gap beyond the estimated native overhead region for left/right placement |
| Layout | Player / NPC overhead side | Choose left, right, above, or below independently for player stacks and target/nearby bars |
| Layout | Native overhead half-width / above / below | Tune the estimated region reserved for prayers, hitsplats, and chat |
| Layout | Hide game combat bars | Hide native in-game health-bar sprites without suppressing hitsplats, overhead prayers, skulls, or chat |
| Style | Bar width / height | Size of each bar in pixels |
| Style | Show text labels | Toggle value text |
| Style | Text mode | Numbers, percent, or numbers and percent |
| Style | Show bar names | Prefix labels with HP, Prayer, Special, Run, or target names |
| Style | Hide full player bars | Hide HP/prayer/special/run when full |
| Style | Font size | Text size; labels auto-hide when bars are too skinny |
| Style | Text / bar background / border color | Global text, empty-bar/background, and optional border colors |
| Style | Border thickness | Defaults to 0 for cleaner bars |
| Player Hitpoints | Show player HP bar / HP color | Toggle and color |
| Player Prayer | Show player prayer bar / Prayer color | Toggle and color |
| Special Attack | Show special attack bar / Special color | Toggle and color |
| Run Energy | Show run energy bar / Run energy color | Toggle and color |
| Target Hitpoints | Show target HP bar / Target HP color | Toggle and color |
| Target Hitpoints | Target display style | Ratio/scale or estimated real NPC HP when available |
| Target Hitpoints | Target source mode | Current interaction only or remember last interaction |
| Target Hitpoints | Show nearby NPC/player HP bars | Add health-only overhead bars for nearby NPCs and players with visible health |
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

Overhead prayer, hitsplat, and chat bounds are estimated from the actor anchor, not read from native RuneLite sprite rectangles. In the game, test prayer/hitsplats with horizontal and vertical bars on the player and target/nearby actors at low, middle, and high zoom; tune the exclusion dimensions and offsets if anything overlaps. Below places bars beneath the reserved region and may cover the actor; bars near a screen edge can clip. Native health-bar hiding is unchanged. A passing Java build cannot establish visual clearance.

## Plugin Hub safety

BarMaster is intentionally read-only and display-only:

- No reflection, JNI, JNA, native memory access, external processes, dynamic code loading, or serialization.
- No input injection, autotyping, or automated game actions.
- No network requests.
- No next-attack prediction, prayer switching, tile indicators, or other restricted PvM/PvP helpers.

## License

MIT. See [LICENSE](LICENSE).
