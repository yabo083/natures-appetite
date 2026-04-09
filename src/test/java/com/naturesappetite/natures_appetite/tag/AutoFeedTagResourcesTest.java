package com.naturesappetite.natures_appetite.tag;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class AutoFeedTagResourcesTest {
    @Test
    void autoFeedAnimalTagsUseEntityTypesDirectory() {
        assertNotNull(
                AutoFeedTagResourcesTest.class.getClassLoader().getResource("data/natures_appetite/tags/entity_types/auto_feed_animals.json"),
                "auto_feed_animals tag must exist under tags/entity_types/");
        assertNotNull(
                AutoFeedTagResourcesTest.class.getClassLoader().getResource("data/natures_appetite/tags/entity_types/auto_feed_blacklist.json"),
                "auto_feed_blacklist tag must exist under tags/entity_types/");
    }
}
