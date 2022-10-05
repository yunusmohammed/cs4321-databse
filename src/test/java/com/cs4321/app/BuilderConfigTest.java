package com.cs4321.app.planbuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuilderConfigTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void testInit() {
        BuilderConfig config1 = new BuilderConfig("src/test/resources/sampleConfigs/config1.txt");
        assertEquals(BuilderConfig.Join.TNLJ, config1.getJoinType());
        assertEquals(BuilderConfig.Sort.MEMORY, config1.getSortType());

        BuilderConfig config2 = new BuilderConfig("src/test/resources/sampleConfigs/config2.txt");
        assertEquals(BuilderConfig.Join.BNLJ, config2.getJoinType());
        assertEquals(5, config2.getJoinBufferSize());
        assertEquals(BuilderConfig.Sort.EXTERNAL, config2.getSortType());
        assertEquals(4, config2.getSortBufferSize());

        BuilderConfig config3 = new BuilderConfig("src/test/resources/sampleConfigs/config3.txt");
        assertEquals(BuilderConfig.Join.SMJ, config3.getJoinType());
        assertEquals(BuilderConfig.Sort.EXTERNAL, config3.getSortType());
        assertEquals(9, config3.getSortBufferSize());
    }
}