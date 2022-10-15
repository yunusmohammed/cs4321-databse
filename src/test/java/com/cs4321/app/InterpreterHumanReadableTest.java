package com.cs4321.app;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InterpreterHumanReadableTest {

    private static final String sep = File.separator;
    private static final String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "input";
    private static String outputdir;
    private static final Logger logger = Logger.getInstance();

    static {
        try {
            outputdir = Files.createTempDirectory("output_humanReadable").toString();
        } catch (IOException e) {
            logger.log(e.getMessage());
        }
    }

    @BeforeAll
    static void setup() {
        Interpreter.setHumanReadable(true);
        Interpreter.setInputdir(inputdir);
        DatabaseCatalog.setInputDir(inputdir);
        PhysicalPlanBuilder.setConfigs("plan_builder_config.txt");
        PhysicalPlanBuilder.setHumanReadable(true);
    }

    @Test
    void queryOutputHumanReadable() {
        String correctOutputPath = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "correctOutput";
        Interpreter.setOutputdir(outputdir);

        Interpreter.parseQueries();
        File[] correctQueries = new File(correctOutputPath).listFiles();
        File[] outputQueries = new File(outputdir).listFiles();
        if (correctQueries.length != outputQueries.length) System.out.println("At least one query has not been output");
        for (int i = 0; i < correctQueries.length; i++) {
            try {
                boolean equal = FileUtils.contentEquals(correctQueries[i], outputQueries[i]);
                if (!equal) System.out.println(correctQueries[i].getName() + " is incorrect");
                assertTrue(equal);
            } catch (Exception e) {
                throw new Error("Issue reading output from " + correctQueries[i].getName());
            }
        }
    }


}



