package com.naturesappetite.natures_appetite.gametest;

import com.naturesappetite.natures_appetite.NaturesAppetiteMod;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(NaturesAppetiteMod.MODID)
@PrefixGameTestTemplate(false)
public final class AutoFeedGameTests {
    private AutoFeedGameTests() {
    }

    @GameTest(templateNamespace = NaturesAppetiteMod.MODID, template = "framework_loads")
    public static void frameworkLoads(GameTestHelper helper) {
        helper.succeed();
    }
}
