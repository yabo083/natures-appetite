package com.naturesappetite.natures_appetite.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FoodQualityEntry(
        int tier,
        int loveTimeBonusTicks,
        float healAmount,
        int babyGrowthBonusTicks,
        double extraBabyChance,
        double signalRange,
        int signalDurationTicks,
        double specialDropChance,
        float specialDropMultiplier,
        int specialDropDurationTicks) {
    public static final FoodQualityEntry NONE = new FoodQualityEntry(0, 0, 0.0F, 0, 0.0D, 0.0D, 0, 0.0D, 1.0F, 0);

    public static final FoodQualityEntry GOLDEN_FALLBACK = new FoodQualityEntry(
            2, 40, 2.0F, 1200, 0.08D, 12.0D, 120, 0.3D, 1.5F, 12000);

    public static final Codec<FoodQualityEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("tier", 0).forGetter(FoodQualityEntry::tier),
            Codec.INT.optionalFieldOf("loveTimeBonusTicks", 0).forGetter(FoodQualityEntry::loveTimeBonusTicks),
            Codec.FLOAT.optionalFieldOf("healAmount", 0.0F).forGetter(FoodQualityEntry::healAmount),
            Codec.INT.optionalFieldOf("babyGrowthBonusTicks", 0).forGetter(FoodQualityEntry::babyGrowthBonusTicks),
            Codec.DOUBLE.optionalFieldOf("extraBabyChance", 0.0D).forGetter(FoodQualityEntry::extraBabyChance),
            Codec.DOUBLE.optionalFieldOf("signalRange", 0.0D).forGetter(FoodQualityEntry::signalRange),
            Codec.INT.optionalFieldOf("signalDurationTicks", 0).forGetter(FoodQualityEntry::signalDurationTicks),
            Codec.DOUBLE.optionalFieldOf("specialDropChance", 0.0D).forGetter(FoodQualityEntry::specialDropChance),
            Codec.FLOAT.optionalFieldOf("specialDropMultiplier", 1.0F).forGetter(FoodQualityEntry::specialDropMultiplier),
            Codec.INT.optionalFieldOf("specialDropDurationTicks", 0).forGetter(FoodQualityEntry::specialDropDurationTicks)).apply(instance,
                    FoodQualityEntry::new));

    public boolean hasAnyEffect() {
        return this.tier > 0
                || this.loveTimeBonusTicks > 0
                || this.healAmount > 0.0F
                || this.babyGrowthBonusTicks > 0
                || this.extraBabyChance > 0.0D
                || this.signalRange > 0.0D
                || this.signalDurationTicks > 0
                || this.specialDropChance > 0.0D
                || this.specialDropMultiplier > 1.0F
                || this.specialDropDurationTicks > 0;
    }
}
