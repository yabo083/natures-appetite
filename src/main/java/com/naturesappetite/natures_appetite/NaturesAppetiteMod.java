package com.naturesappetite.natures_appetite;

import com.mojang.logging.LogUtils;
import com.naturesappetite.natures_appetite.config.NaturesAppetiteServerConfig;
import com.naturesappetite.natures_appetite.datamap.ModDataMaps;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(NaturesAppetiteMod.MODID)
public final class NaturesAppetiteMod {
    public static final String MODID = "natures_appetite";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NaturesAppetiteMod() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, NaturesAppetiteServerConfig.SPEC);
        MinecraftForge.EVENT_BUS.addListener(ModDataMaps::onAddReloadListener);
    }
}
