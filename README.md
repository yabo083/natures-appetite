# Nature's Appetite

# Overview

Nature's Appetite is a Forge 1.20.1 mod that automates animal feeding and breeding:

- Animals can seek and eat dropped breeding food automatically.
- Adult feeding is gated by breeding cooldown (`age == 0`) to prevent continuous waste.
- Baby animals can continuously eat (configurable) to speed up growth.
- Food quality is data-driven and can add healing, love-time bonus, growth bonus, extra baby chance, herd signals, and temporary special drops.

### Current Features

- Auto-feed goal injection for whitelist animals.
- Tag-driven whitelist/blacklist control.
- Owner attribution for breeding credit.
- Reloadable JSON-based quality system (`data_maps/item/food_quality.json`).
- Per-level dropped-item candidate tracker for better farm performance.
- GameTest + unit test scaffolding.

### Server Config Keys

Main keys in `natures_appetite-server.toml`:

- `enableAutoFeed`
- `enableAdultContinuousFeeding`
- `enableBabyContinuousFeeding`
- `scanRadius`
- `scanIntervalMin`
- `scanIntervalMax`
- `maxCandidatesPerScan`
- `pathTimeoutTicks`
- `ownerAttributionRange`
- `enableQualitySystem`
- `enablePackBehavior`
- `enableSpecialDrops`

### Datapack Tags

Only entities in whitelist are enabled; blacklist always overrides whitelist.

Correct tag folder is `entity_types` (plural):

- `data/natures_appetite/tags/entity_types/auto_feed_animals.json`
- `data/natures_appetite/tags/entity_types/auto_feed_blacklist.json`

Datapacks should be placed in your world save folder:

- `<minecraft>/saves/<world_name>/datapacks/<your_pack>/...`

### Example: Enable Bees

Bees are valid targets when they are in the whitelist and can find valid food (flowers).

Datapack tree:

```text
<minecraft>/saves/<world_name>/datapacks/natures_appetite_bees/
  pack.mcmeta
  data/
    natures_appetite/
      tags/
        entity_types/
          auto_feed_animals.json
```

`pack.mcmeta`

```json
{
  "pack": {
    "pack_format": 15,
    "description": "Enable Nature's Appetite auto-feed for bees"
  }
}
```

`data/natures_appetite/tags/entity_types/auto_feed_animals.json`

```json
{
  "replace": false,
  "values": [
    "minecraft:bee"
  ]
}
```

Apply datapack changes:

```mcfunction
/reload
/datapack list
```
