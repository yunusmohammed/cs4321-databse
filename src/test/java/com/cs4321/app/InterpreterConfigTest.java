package com.cs4321.app;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterConfigTest {
    private static final String sep = File.separator;
    private final String basePath = System.getProperty("user.dir") + sep
            + "src" + sep + "test" + sep + "resources" + sep + "sampleInterpreterConfigs";

    @Test
    void testInit() {
        InterpreterConfig interpreterConfig1 = new InterpreterConfig(basePath + sep + "config1.txt");
        assertEquals("src/test/resources/input_binary", interpreterConfig1.getInputdir());
        assertEquals("src/test/resources/output", interpreterConfig1.getOutputdir());
        assertEquals("src/test/resources/output", interpreterConfig1.getTempdir());
//        assertFalse(interpreterConfig1.shouldBuildIndexes());
//        assertTrue(interpreterConfig1.shouldEvaluateQueries());
        assertFalse(interpreterConfig1.isHumanReadable());

        InterpreterConfig interpreterConfig2 = new InterpreterConfig(basePath + sep + "config2.txt");
        assertEquals("src/test/resources/input_binary", interpreterConfig2.getInputdir());
        assertEquals("src/test/resources/output", interpreterConfig2.getOutputdir());
        assertEquals("src/test/resources/output", interpreterConfig2.getTempdir());
//        assertTrue(interpreterConfig2.shouldBuildIndexes());
//        assertFalse(interpreterConfig2.shouldEvaluateQueries());
        assertFalse(interpreterConfig2.isHumanReadable());

        InterpreterConfig interpreterConfig3 = new InterpreterConfig(basePath + sep + "config3.txt");
        assertEquals("src/test/resources/input_binary", interpreterConfig3.getInputdir());
        assertEquals("src/test/resources/output", interpreterConfig3.getOutputdir());
        assertEquals("src/test/resources/output", interpreterConfig3.getTempdir());
//        assertTrue(interpreterConfig3.shouldBuildIndexes());
//        assertTrue(interpreterConfig3.shouldEvaluateQueries());
        assertFalse(interpreterConfig3.isHumanReadable());
    }
}