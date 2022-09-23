package com.cs4321.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;
import org.apache.commons.io.FileUtils;

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
    void queryOutput() {
        String resourcesPath = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep;
        String correctOutputPath = resourcesPath + "correctOutput";
        Interpreter.setOutputdir(outputdir);
        Interpreter.parseQueries();
        File[] correctQueries = new File(correctOutputPath).listFiles();
        File[] outputQueries = new File(outputdir).listFiles();
        if(correctQueries.length != outputQueries.length) System.out.println("At least one query has not been output");
        if(correctQueries != null) {
            for(int i=0; i<correctQueries.length; i++) {
                try {
                    boolean equal = FileUtils.contentEquals(correctQueries[i], outputQueries[i]);
                    if(!equal) System.out.println(correctQueries[i].getName() + " is incorrect");
                    assertTrue(equal);
                } catch (Exception e) {
                    System.out.println("Issue reading output from " + correctQueries[i].getName());
                    e.printStackTrace();
                }
            }
        }
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