package com.cs4321.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuilderConfigTest {

    @Test
    void testInit() {
        BuilderConfig config1 = new BuilderConfig("src/test/resources/samplePlanBuilderConfigs/config1.txt");
        assertEquals(BuilderConfig.Join.TNLJ, config1.getJoinType());
        assertEquals(BuilderConfig.Sort.MEMORY, config1.getSortType());
        assertFalse(config1.shouldUseIndexForSelection());

        BuilderConfig config2 = new BuilderConfig("src/test/resources/samplePlanBuilderConfigs/config2.txt");
        assertEquals(BuilderConfig.Join.BNLJ, config2.getJoinType());
        assertEquals(5, config2.getJoinBufferSize());
        assertEquals(BuilderConfig.Sort.EXTERNAL, config2.getSortType());
        assertEquals(4, config2.getSortBufferSize());
        assertFalse(config2.shouldUseIndexForSelection());

        BuilderConfig config3 = new BuilderConfig("src/test/resources/samplePlanBuilderConfigs/config3.txt");
        assertEquals(BuilderConfig.Join.SMJ, config3.getJoinType());
        assertEquals(BuilderConfig.Sort.EXTERNAL, config3.getSortType());
        assertEquals(9, config3.getSortBufferSize());
        assertTrue(config3.shouldUseIndexForSelection());
    }
}