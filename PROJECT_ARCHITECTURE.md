# Nature's Appetite Architecture

## 1. Project Intent and Source Materials

Nature's Appetite is a NeoForge 1.21.1 gameplay automation mod focused on husbandry QoL:

- Animals automatically seek and consume dropped breeding food.
- Successful auto-feeding triggers love mode and can complete breeding without player right-click.
- A quality system extends breeding outcomes, herd behavior, and post-feed drop buffs.

Primary requirement source:

- `C:\Users\yabo\OneDrive\未来-终极愿景\编程\项目\MCMod开发\MCMod：Nature's Appetite\生灵食性开发日记.md`

External knowledge sources used during implementation:

- NeoForge Getting Started 1.21.1
- NeoForge Event system (1.21.1 line)
- NeoForge Data Maps (server resources)
- NeoForge Data Attachments (datastorage)
- NeoForge Tags (server resources)
- NeoForge 21.1.x Javadocs for `EntityJoinLevelEvent`, `Animal`, `GoalSelector`, `ItemEntity`, `BabyEntitySpawnEvent`
- NeoForge `Tags.java`, `EventHooks.java`, and Maven metadata (`21.1.x` latest patchline)

## 2. Version and Build Baseline

- Minecraft: `1.21.1`
- NeoForge: `21.1.224`
- Java toolchain: `21`
- Mod ID: `natures_appetite`
- Package root: `com.naturesappetite.natures_appetite`

Build system and runtime notes:

- Access Transformer enabled for `Mob.goalSelector` goal injection.
- JUnit 5 enabled for JVM test source set.
- GameTest server run is wired and verified.

## 3. Runtime Architecture

### 3.1 Core Flow

1. `EntityJoinLevelEvent` (server-side) detects `Animal` and injects `AutoFeedDroppedFoodGoal`.
2. Goal periodically scans tracked `ItemEntity` candidates around each animal.
3. Candidate filter uses `animal.isFood(stack)` for compatibility-first food recognition.
4. Animal navigates to target dropped food and consumes 1 item.
5. Feed trigger policy is age-aware:
   - Adult: must be enabled in config, must have `age == 0` (no breeding cooldown), and must pass `canFallInLove()`.
   - Baby: controlled by a dedicated continuous-feed switch; consumption accelerates growth via `ageUp`.
6. Quality data (DataMap) optionally applies extra effects:
   - healing
   - extra love duration
   - baby growth bonus
   - extra baby chance
   - group signal
   - special drop buff

### 3.2 Module Boundaries

- `config`: server config keys and accessors (`NaturesAppetiteServerConfig`)
- `ai`: feeding state machine goal (`AutoFeedDroppedFoodGoal`)
- `event`: runtime wiring, injection, breeding bonus, drop bonus, gametest registration (`ModGameplayEvents`)
- `attachment`: per-animal runtime state (`AnimalFeedState`, `ModAttachments`)
- `datamap`: item quality schema and type registration (`FoodQualityEntry`, `ModDataMaps`)
- `tag`: supported/blacklist entity tags (`ModTags`)
- `util`: candidate cache, player attribution, quality resolving
- `gametest`: minimal integration test class

### 3.3 Performance Strategy

- No full-world item scans in goals.
- Level-scoped dropped-item index maintained through `EntityJoinLevelEvent`/`EntityLeaveLevelEvent`.
- Per-animal randomized scan interval (`scanIntervalMin`~`scanIntervalMax`).
- Candidate cap per scan (`maxCandidatesPerScan`).
- Path timeout and temporary per-item backoff to avoid retry storms.
- Signal state can shorten scan interval for nearby herd wakeup.

### 3.4 Compatibility Strategy

- Food compatibility is based on `Animal#isFood` (no hardcoded breeding food list).
- Entity scope is data-driven by tags:
  - `natures_appetite:auto_feed_animals` (default farm whitelist)
  - `natures_appetite:auto_feed_blacklist` (hard deny)
- Quality lookup uses DataMap with fallback for `#c:foods/golden` semantic items.

## 4. Public Contracts

### 4.1 Config Keys (SERVER)

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
- `goalPriority`

### 4.2 DataMap

- ID: `natures_appetite:food_quality`
- Registry: `item`
- Codec fields:
  - `tier`
  - `loveTimeBonusTicks`
  - `healAmount`
  - `babyGrowthBonusTicks`
  - `extraBabyChance`
  - `signalRange`
  - `signalDurationTicks`
  - `specialDropChance`
  - `specialDropMultiplier`
  - `specialDropDurationTicks`

### 4.3 Attachment

- ID: `natures_appetite:animal_feed_state`
- Holder: `Animal`
- Stored state:
  - next scan scheduling
  - blocked target backoff
  - pathing start tick
  - recent attribution player
  - pending breeding bonuses
  - herd signal window
  - special drop buff window

### 4.4 Data/Tag Resources

- `data/natures_appetite/tags/entity_type/auto_feed_animals.json`
- `data/natures_appetite/tags/entity_type/auto_feed_blacklist.json`
- `data/natures_appetite/data_maps/item/food_quality.json`
- GameTest structure:
  - `data/natures_appetite/structure/framework_loads.nbt`
  - `data/natures_appetite/structures/framework_loads.nbt`

## 5. Event Topology

- `EntityJoinLevelEvent`
  - track `ItemEntity`
  - inject goal into supported animals
- `EntityLeaveLevelEvent`
  - untrack removed `ItemEntity`
- `LevelEvent.Unload`
  - clear level tracker state
- `BabyEntitySpawnEvent`
  - consume pending quality bonuses for growth and extra child chance
- `LivingDropsEvent`
  - apply temporary quality drop multipliers
- `RegisterGameTestsEvent`
  - register `AutoFeedGameTests`

## 6. Testing and Verification Status

Executed and passing:

- `.\gradlew.bat test`
- `.\gradlew.bat runGameTestServer`

Current test coverage:

- Unit test: `FoodQualityEntry` codec/default behavior
- Unit test: `FeedEligibilityRules` adult/baby feeding gate logic
- GameTest: framework wiring and template loading smoke test

## 7. Planned Growth (Aligned with Initial Roadmap)

Already implemented from M0/M1/M2 scope:

- baseline mod setup and config
- automatic feed + breeding trigger
- item-tracker performance cache
- quality effects and breeding modifiers
- herd signal behavior
- special drop buff behavior
- data-driven entity and quality controls

Future optional hardening:

- richer GameTests for full in-world breeding assertions
- stricter attribution model beyond owner/nearby fallback
- optional telemetry hooks for large-farm profiling
