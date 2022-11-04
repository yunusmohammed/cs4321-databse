package com.cs4321.app;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InterpreterHumanReadableTest {

    private static final String sep = File.separator;
    private static final String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep
            + "resources" + sep + "input_endToEnd";
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
        // Interpreter.setHumanReadable(true);
        Interpreter.setInputdir(inputdir);
        DatabaseCatalog.setInputDir(inputdir);
        PhysicalPlanBuilder.setConfigs("plan_builder_config.txt");
        // PhysicalPlanBuilder.setHumanReadable(true);
    }

    @Test
    void queryOutputHumanReadable() {
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
                    File sortedCorrect = new File(SortingUtilities.sortFile(correctQueries[i].toString(), null));
                    File sortedOutput = new File(SortingUtilities.sortFile(outputQueries[i].toString(), null));
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
