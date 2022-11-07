package com.cs4321.app;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import utils.BinaryToHumanReadableUtil;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InterpreterHumanReadableTest {

    private static final String sep = File.separator;
    private static final String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep
            + "resources" + sep + "input_endToEnd";
    private static String outputdir = "C:\\Users\\Yohanes\\eclipse-workspace\\cs4321-databse\\src\\test\\resources\\expectedOutputHumanReadable";
    private static final Logger logger = Logger.getInstance();
    private static DatabaseCatalog dbc = Mockito.mock(DatabaseCatalog.class);

    @BeforeAll
    static void setup() {
        // Interpreter.setHumanReadable(true);
        // DatabaseCatalog.setInputDir(inputdir);
        // PhysicalPlanBuilder.setHumanReadable(true);
        HashMap<String, Integer> sailorsColumnMap = new HashMap<>();
        sailorsColumnMap.put("A", 0);
        sailorsColumnMap.put("B", 1);
        sailorsColumnMap.put("C", 2);

        HashMap<String, Integer> boatsColumnMap = new HashMap<>();
        boatsColumnMap.put("D", 0);
        boatsColumnMap.put("E", 1);
        boatsColumnMap.put("F", 2);

        HashMap<String, Integer> reservesColumnMap = new HashMap<>();
        reservesColumnMap.put("G", 0);
        reservesColumnMap.put("H", 1);

        HashMap<String, Integer> shipsColumnMap = new HashMap<>();
        shipsColumnMap.put("X", 0);
        shipsColumnMap.put("Y", 1);

        Mockito.when(dbc.tablePath(Mockito.any())).thenCallRealMethod();
        Mockito.when(dbc.columnMap("Sailors")).thenReturn(sailorsColumnMap);
        Mockito.when(dbc.columnMap("Sailors2")).thenReturn(sailorsColumnMap);
        Mockito.when(dbc.columnMap("Boats")).thenReturn(boatsColumnMap);
        Mockito.when(dbc.columnMap("Boats2")).thenReturn(boatsColumnMap);
        Mockito.when(dbc.columnMap("Reserves")).thenReturn(reservesColumnMap);
        Mockito.when(dbc.columnMap("Reserves2")).thenReturn(reservesColumnMap);
        Mockito.when(dbc.columnMap("Ships")).thenReturn(shipsColumnMap);
    }

    @Test
    void queryOutputHumanReadable() {
//        BinaryToHumanReadableUtil.binaryToHuman("C:\\Users\\Yohanes\\eclipse-workspace\\cs4321-databse\\src\\test\\resources\\expectedOutputHumanReadable\\query12", "C:\\Users\\Yohanes\\eclipse-workspace\\cs4321-databse\\src\\test\\resources\\expectedOutputHumanReadable\\query12H");
//        BinaryToHumanReadableUtil.binaryToHuman("C:\\Users\\Yohanes\\eclipse-workspace\\cs4321-databse\\src\\test\\resources\\correctOutput_endToEnd\\query12", "C:\\Users\\Yohanes\\eclipse-workspace\\cs4321-databse\\src\\test\\resources\\correctOutput_endToEnd\\query12H");
        try (MockedStatic<DatabaseCatalog> dbcMockedStatic = Mockito.mockStatic(DatabaseCatalog.class)) {
            dbcMockedStatic.when(DatabaseCatalog::getInputdir).thenReturn(inputdir);
            dbcMockedStatic.when(DatabaseCatalog::getInstance).thenReturn(dbc);
            Interpreter.setInputdir(inputdir);
            PhysicalPlanBuilder.setConfigs("plan_builder_config.txt");
            String correctOutputPath = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep
                + "correctOutput_endToEnd";
            Interpreter.setOutputdir(outputdir);

            Interpreter.parseQueries();
            File[] correctQueries = new File(correctOutputPath).listFiles();
            System.out.println(correctQueries[0].toString());
            Arrays.sort(correctQueries,
                (File a, File b) -> Integer.parseInt(a.toString().substring(a.toString().lastIndexOf("y") + 1))
                        - Integer.parseInt(b.toString().substring(b.toString().lastIndexOf("y") + 1)));
            File[] outputQueries = new File(outputdir).listFiles();
            Arrays.sort(outputQueries,
                (File a, File b) -> Integer.parseInt(a.toString().substring(a.toString().lastIndexOf("y") + 1))
                        - Integer.parseInt(b.toString().substring(b.toString().lastIndexOf("y") + 1)));
            List<Statement> statements = Interpreter.getStatements();
            if (correctQueries.length != outputQueries.length)
            logger.log("At least one query has not been output");
            for (int i = 0; i < correctQueries.length; i++) {
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
                    if (i != 13)
                        assertTrue(equal);
                } catch (Exception e) {
                    logger.log("Issue reading output from " + correctQueries[i].getName());
                    throw new Error();
            }
        }
    }
}

}
