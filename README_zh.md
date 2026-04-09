简介

Nature's Appetite 是一个 NeoForge 1.21.1 模组，用于自动化畜牧投喂与繁殖：

- 动物可自动寻找并食用地面掉落的可繁殖食物。
- 成年动物受繁殖冷却约束（`age == 0` 才会进食），避免持续吞粮浪费。
- 幼崽默认可持续进食（可配置），用于加速成长。
- 食物品质系统数据驱动，可附带回血、爱心时长、成长加速、额外幼崽概率、群体信号和限时额外掉落效果。

### 已实现功能

- 白名单动物自动注入自动进食 AI。
- 白名单/黑名单标签控制生效范围。
- 投喂归属玩家追踪（繁殖归因）。
- Data Map 品质系统。
- Level 级掉落物候选追踪，降低农场扫描开销。
- 已接入 GameTest 与单元测试基础。

### 服务器配置项

`natures_appetite-server.toml` 主要键：

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

### 数据包标签

只有在白名单中的动物会生效；黑名单最终否决（优先级更高）。

`data/natures_appetite/tags/entity_type/auto_feed_animals.json`

```json
{
  "replace": true,
  "values": [
    "minecraft:cow",
    "minecraft:sheep",
    "minecraft:pig",
    "minecraft:chicken"
  ]
}
```

`data/natures_appetite/tags/entity_type/auto_feed_blacklist.json`

```json
{
  "replace": true,
  "values": [
    "minecraft:pig"
  ]
}
```

重载数据包：

```mcfunction
/reload
/datapack list
```
