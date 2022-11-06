package com.cs4321.app;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class SortingUtilitiesTest {

    private static final String sep = File.separator;

    private DatabaseCatalog dbc = DatabaseCatalog.getInstance();
    private Logger logger = Logger.getInstance();

    // creates two tables with one of them sorted, applies sort file to the unsorted table, and checks if the files are equal
    @Test
    void sortFileTest() {
        DatabaseCatalog.setInputDir(System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "input");
        try {
            String testTablePath = Files.createTempFile("testTable", null).toString();
            String testTableSortedPath = Files.createTempFile("testTableSorted", null).toString();
            TupleWriter testTableWriter = new TupleWriter(testTablePath);
            TupleWriter testTableSortedWriter = new TupleWriter(testTableSortedPath);
            Tuple t1 = new Tuple("2,3");
            Tuple t2 = new Tuple("3,2");
            Tuple t3 = new Tuple("2,1");
            Tuple t4 = new Tuple("1,5");
            write(testTableWriter, t1);
            write(testTableWriter, t2);
            write(testTableWriter, t3);
            write(testTableWriter, t4);
            testTableWriter.writeToFile(null, true);
            write(testTableSortedWriter, t4);
            write(testTableSortedWriter, t3);
            write(testTableSortedWriter, t1);
            write(testTableSortedWriter, t2);
            testTableSortedWriter.writeToFile(null, true);
            assertTrue(FileUtils.contentEquals(new File(SortingUtilities.sortFile(testTablePath, null, null)), new File(testTableSortedPath)));
        } catch (IOException e) {
            logger.log("Unable to compare contents of files for sort file test.");
            throw new Error();
        }
    }

    /**
     * Writes tuple t to the given file using the tuple writer.
     * @param writer - the tuple writer corresponding to a table
     * @param t - the tuple which will be added to the file
     * @throws IOException - may throw an IOException when writing to the file
     */
    private void write(TupleWriter writer, Tuple t) throws IOException {
        writer.writeToFile(t, false);
    }
}