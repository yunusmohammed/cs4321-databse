package com.cs4321.app;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InterpreterTest {

    private static final String sep = File.separator;
    private String outputdir;
    private String tempdir;
    private static final Logger logger = Logger.getInstance();

    void testQueries(String inputdir, String correctOutputPath) {
        Interpreter.setInputdir(inputdir);
        Interpreter.parseQueries();
        File[] correctQueries = new File(correctOutputPath).listFiles();
        Stream<File> correctStream = Arrays.stream(correctQueries);
        List<File> listCorrectQueries = correctStream.filter(file -> file.toString().indexOf("plan") == -1)
                .collect(Collectors.toList());
        correctQueries = new File[listCorrectQueries.size()];
        for (int i = 0; i < correctQueries.length; i++) {
            correctQueries[i] = listCorrectQueries.get(i);
        }
        Arrays.sort(correctQueries,
                (File a, File b) -> Integer.parseInt(a.toString().substring(a.toString().lastIndexOf("y") + 1))
                        - Integer.parseInt(b.toString().substring(b.toString().lastIndexOf("y") + 1)));
        File[] outputQueries = new File(outputdir).listFiles();
        Stream<File> outputStream = Arrays.stream(outputQueries);
        List<File> listOutputQueries = outputStream.filter(file -> file.toString().indexOf("plan") == -1)
                .collect(Collectors.toList());
        outputQueries = new File[listOutputQueries.size()];
        for (int i = 0; i < outputQueries.length; i++) {
            outputQueries[i] = listOutputQueries.get(i);
        }
        Arrays.sort(outputQueries,
                (File a, File b) -> Integer.parseInt(a.toString().substring(a.toString().lastIndexOf("y") + 1))
                        - Integer.parseInt(b.toString().substring(b.toString().lastIndexOf("y") + 1)));
        List<Statement> statements = Interpreter.getStatements();
        if (correctQueries.length != outputQueries.length)
            logger.log("At least one query has not been output");
        for (int i = 0; i < outputQueries.length; i++) {
            try {
                boolean equal;
                Select select = (Select) statements.get(i);
                PlainSelect selectBody = (PlainSelect) select.getSelectBody();
                List<OrderByElement> orderByElementsList = selectBody.getOrderByElements();
                if (orderByElementsList == null || orderByElementsList.size() == 0) {
                    File sortedCorrect = new File(SortingUtilities.sortFile(correctQueries[i].toString(), null, null));
                    File sortedOutput = new File(SortingUtilities.sortFile(outputQueries[i].toString(), null, null));
                    equal = FileUtils.contentEquals(sortedCorrect, sortedOutput);
                } else
                    equal = FileUtils.contentEquals(correctQueries[i], outputQueries[i]);
                if (!equal)
                    logger.log(correctQueries[i].getName() + " is incorrect");
                assertTrue(equal);
            } catch (Exception e) {
                logger.log("Issue reading output from " + correctQueries[i].getName());
                throw new Error();
            }
        }
    }

    @BeforeEach
    void setUp() {

        try {
            outputdir = "/Users/ymm26/Desktop/Senior Fall/CS 4321/cs4321-databse/src/test/resources/output";// Files.createTempDirectory("output").toString();
            tempdir = Files.createTempDirectory("temp").toString();
            Interpreter.setOutputdir(outputdir);
            Interpreter.setTempdir(tempdir);
        } catch (IOException e) {
            logger.log(e.getMessage());
        }
    }

    @Test
    void queryOutput() {
        String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep
                + "input_binary";
        String correctOutputPath = "/Users/ymm26/Desktop/Senior Fall/CS 4321/cs4321-databse/src/test/resources/output";
        DatabaseCatalog.setInputDir(inputdir);
        PhysicalPlanBuilder.setConfigs("plan_builder_config.txt");
        testQueries(inputdir, correctOutputPath);
    }

    @Test
    void SMJQueryOutput() {
        String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep
                + "input_SMJ";
        String correctOutputPath = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep
                + "expected_SMJ";
        DatabaseCatalog.setInputDir(inputdir);
        PhysicalPlanBuilder.setConfigs("plan_builder_config.txt");
        testQueries(inputdir, correctOutputPath);
    }

    @Test
    void outputdir() {
        Interpreter.setOutputdir(outputdir);
        assertEquals(outputdir, Interpreter.getOutputdir());
    }

    @Test
    void inputdir() {
        String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep
                + "input_binary";
        Interpreter.setInputdir(inputdir);
        assertEquals(inputdir, Interpreter.getInputdir());
    }

    @Test
    void queriesPath() {
        String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep
                + "input_binary";
        Interpreter.setInputdir(inputdir);
        assertEquals(inputdir + sep + "queries.sql", Interpreter.queriesPath());
    }
}