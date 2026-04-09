package com.naturesappetite.natures_appetite;

import com.mojang.logging.LogUtils;
import com.naturesappetite.natures_appetite.attachment.ModAttachments;
import com.naturesappetite.natures_appetite.config.NaturesAppetiteServerConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(NaturesAppetiteMod.MODID)
public final class NaturesAppetiteMod {
    public static final String MODID = "natures_appetite";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NaturesAppetiteMod(IEventBus modEventBus, ModContainer modContainer) {
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.SERVER, NaturesAppetiteServerConfig.SPEC);
    }
}
