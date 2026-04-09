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

标签目录应为 `entity_types`（复数）：

- `data/natures_appetite/tags/entity_types/auto_feed_animals.json`
- `data/natures_appetite/tags/entity_types/auto_feed_blacklist.json`

数据包应放在存档目录下：

- `<minecraft>/saves/<世界名>/datapacks/<你的数据包>/...`

### 示例：让蜜蜂生效

把 `minecraft:bee` 加入白名单后，蜜蜂即可参与自动觅食（以花为食物）。

目录结构：

```text
<minecraft>/saves/<世界名>/datapacks/natures_appetite_bees/
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
    "description": "让 Nature's Appetite 对蜜蜂启用自动投喂"
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

重载数据包：

```mcfunction
/reload
/datapack list
```
