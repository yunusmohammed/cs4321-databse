package com.cs4321.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest {

    private static final String sep = File.separator;
    private static final String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "input";
    private static String outputdir;

    static {
        try {
            outputdir = Files.createTempDirectory("output").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @BeforeAll
    static void setup() {
        DatabaseCatalog.setInputDir(inputdir);
    }


    @Test
    void outputdir() {
        Interpreter.setOutputdir(outputdir);
        assertEquals(outputdir, Interpreter.getOutputdir());
    }

    @Test
    void inputdir() {
        Interpreter.setInputdir(inputdir);
        assertEquals(inputdir, Interpreter.getInputdir());
    }

    @Test
    void queriesPath() {
        Interpreter.setInputdir(inputdir);
        assertEquals(inputdir + sep + "queries.sql", Interpreter.queriesPath());
    }
}