package com.cs4321.physicaloperators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cs4321.app.AliasMap;
import com.cs4321.app.DatabaseCatalog;
import com.cs4321.app.Tuple;

import net.sf.jsqlparser.schema.Table;

public class IndexScanOperatorTests {
    private static IndexScanOperator scanOperator;
    private static Table tableUnclustered;
    private static Table tableClustered;
    private static final String sep = File.separator;
    private static final String indexAttributeName = "E";
    private static final String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep
            + "resources" + sep + "input_indexes";
    private static final String boatsEUnclustedIndexFilePath = System.getProperty("user.dir") + sep + "src" + sep
            + "test"
            + sep + "resources" + sep + "expected_indexes" + sep + "Boats.E";
    private static final String sailorsAClustedIndexFilePath = System.getProperty("user.dir") + sep + "src" + sep
            + "test"
            + sep + "resources" + sep + "expected_indexes" + sep + "Boats.E";
    private final DatabaseCatalog dbc = DatabaseCatalog.getInstance();
    private static AliasMap mockAliasMapUnclustered;
    private static AliasMap mockAliasMapClustered;

    @BeforeAll
    static void setup() {
        DatabaseCatalog.setInputDir(inputdir);
        tableUnclustered = new Table();
        tableUnclustered.setName("Boats");
        tableClustered = new Table();
        tableClustered.setName("Sailors");
        mockAliasMapUnclustered = Mockito.mock(AliasMap.class);
        Mockito.when(mockAliasMapUnclustered.getBaseTable(Mockito.any())).thenReturn("Boats");
        mockAliasMapClustered = Mockito.mock(AliasMap.class);
        Mockito.when(mockAliasMapClustered.getBaseTable(Mockito.any())).thenReturn("Sailors");
    }

    @Test
    void getNextTupleUnclusteredIndexTest() throws FileNotFoundException, IOException {

        // lowKey equal to highKey
        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered, boatsEUnclustedIndexFilePath,
                indexAttributeName, 10, 10, false);
        assertEquals(new Tuple("4748,10,1432"), scanOperator.getNextTuple());
        assertNull(scanOperator.getNextTuple());

        // Scan multiple items from single index key
        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered, boatsEUnclustedIndexFilePath,
                indexAttributeName, 54, 54, false);
        assertEquals(new Tuple("4631,54,3817"), scanOperator.getNextTuple());
        assertEquals(new Tuple("5868,54,1430"), scanOperator.getNextTuple());
        assertEquals(new Tuple("6967,54,6559"), scanOperator.getNextTuple());
        assertEquals(new Tuple("7746,54,9316"), scanOperator.getNextTuple());
        assertNull(scanOperator.getNextTuple());

        // Scan some Items on one leaf page, with both low and high key present
        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered, boatsEUnclustedIndexFilePath,
                indexAttributeName, 6, 10,
                false);
        assertEquals(new Tuple("4225,6,9275"), scanOperator.getNextTuple());
        assertEquals(new Tuple("2521,8,9005"), scanOperator.getNextTuple());
        assertEquals(new Tuple("4748,10,1432"), scanOperator.getNextTuple());
        assertNull(scanOperator.getNextTuple());

        // Scan Items on one leaf page with low key present but high key absent
        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered, boatsEUnclustedIndexFilePath,
                indexAttributeName, 13,
                15, false);
        assertEquals(new Tuple("3172,13,5062"), scanOperator.getNextTuple());
        assertEquals(new Tuple("3169,14,1890"), scanOperator.getNextTuple());
        assertNull(scanOperator.getNextTuple());

        // Scan Items on one leaf page with low key absent but high key present
        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered, boatsEUnclustedIndexFilePath,
                indexAttributeName, 9, 13,
                false);
        assertEquals(new Tuple("4748,10,1432"), scanOperator.getNextTuple());
        assertEquals(new Tuple("3172,13,5062"), scanOperator.getNextTuple());
        assertNull(scanOperator.getNextTuple());

        // Scan Items on one leaf page with both low key and high key absent
        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered, boatsEUnclustedIndexFilePath,
                indexAttributeName, 9, 15,
                false);
        assertEquals(new Tuple("4748,10,1432"), scanOperator.getNextTuple());
        assertEquals(new Tuple("3172,13,5062"), scanOperator.getNextTuple());
        assertEquals(new Tuple("3169,14,1890"), scanOperator.getNextTuple());
        assertNull(scanOperator.getNextTuple());

        // Scan Items across multiple leaf pages
        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered, boatsEUnclustedIndexFilePath,
                indexAttributeName, 152,
                155, false);
        assertEquals(new Tuple("8840,152,8953"), scanOperator.getNextTuple());
        assertEquals(new Tuple("6809,153,1173"), scanOperator.getNextTuple());
        assertEquals(new Tuple("462,154,7671"), scanOperator.getNextTuple());
        assertEquals(new Tuple("1431,155,3895"), scanOperator.getNextTuple());
        assertNull(scanOperator.getNextTuple());

        // Scan last few items on non-last leaf page with high key higher than all keys
        // present in that leaf page
        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered, boatsEUnclustedIndexFilePath,
                indexAttributeName, 208,
                210, false);
        assertEquals(new Tuple("9720,208,5033"), scanOperator.getNextTuple());
        assertEquals(new Tuple("1026,209,3597"), scanOperator.getNextTuple());
        assertNull(scanOperator.getNextTuple());

        // Scan last few items on last leaf page with high key higher than all keys
        // present
        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered, boatsEUnclustedIndexFilePath,
                indexAttributeName, 9998,
                20000,
                false);
        assertEquals(new Tuple("6437,9998,2317"), scanOperator.getNextTuple());
        assertEquals(new Tuple("8439,9998,6378"), scanOperator.getNextTuple());
        assertEquals(new Tuple("4461,9999,4000"), scanOperator.getNextTuple());
        assertEquals(new Tuple("5317,9999,266"), scanOperator.getNextTuple());
        assertNull(scanOperator.getNextTuple());

        // Scan items on next page with low key falling on previous page
        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered, boatsEUnclustedIndexFilePath,
                indexAttributeName, 36,
                42,
                false);
        assertEquals(new Tuple("9176,41,8867"), scanOperator.getNextTuple());
        assertEquals(new Tuple("50,41,1627"), scanOperator.getNextTuple());
        assertEquals(new Tuple("2459,42,1806"), scanOperator.getNextTuple());
        assertEquals(new Tuple("397,42,1792"), scanOperator.getNextTuple());
        assertNull(scanOperator.getNextTuple());

        // Scan first few items of first page with low key equal to first key
        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered, boatsEUnclustedIndexFilePath,
                indexAttributeName, 4, 6,
                false);
        assertEquals(new Tuple("9206,4,5488"), scanOperator.getNextTuple());
        assertEquals(new Tuple("7775,4,6175"), scanOperator.getNextTuple());
        assertEquals(new Tuple("9076,4,8209"), scanOperator.getNextTuple());
        assertEquals(new Tuple("1803,5,8850"), scanOperator.getNextTuple());
        assertEquals(new Tuple("1109,5,9486"), scanOperator.getNextTuple());
        assertEquals(new Tuple("4225,6,9275"), scanOperator.getNextTuple());
        assertNull(scanOperator.getNextTuple());

        // Scan first few items of first page with low key lower than all keys present
        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered, boatsEUnclustedIndexFilePath,
                indexAttributeName, 1, 6,
                false);
        assertEquals(new Tuple("9206,4,5488"), scanOperator.getNextTuple());
        assertEquals(new Tuple("7775,4,6175"), scanOperator.getNextTuple());
        assertEquals(new Tuple("9076,4,8209"), scanOperator.getNextTuple());
        assertEquals(new Tuple("1803,5,8850"), scanOperator.getNextTuple());
        assertEquals(new Tuple("1109,5,9486"), scanOperator.getNextTuple());
        assertEquals(new Tuple("4225,6,9275"), scanOperator.getNextTuple());
        assertNull(scanOperator.getNextTuple());

        // Scan few items on non-first page
        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered, boatsEUnclustedIndexFilePath,
                indexAttributeName, 1431,
                1434,
                false);
        assertEquals(new Tuple("2884,1431,9374"), scanOperator.getNextTuple());
        assertEquals(new Tuple("1250,1432,9373"), scanOperator.getNextTuple());
        assertEquals(new Tuple("2146,1434,6935"), scanOperator.getNextTuple());
        assertNull(scanOperator.getNextTuple());
    }

    @Test
    void resetUnclusteredIndexTest() throws FileNotFoundException, IOException {
        // reset to same page
        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered, boatsEUnclustedIndexFilePath,
                indexAttributeName, 1431,
                1434,
                false);
        assertEquals(new Tuple("2884,1431,9374"), scanOperator.getNextTuple());
        assertEquals(new Tuple("1250,1432,9373"), scanOperator.getNextTuple());
        assertEquals(new Tuple("2146,1434,6935"), scanOperator.getNextTuple());
        assertNull(scanOperator.getNextTuple());
        scanOperator.reset();
        assertEquals(new Tuple("2884,1431,9374"), scanOperator.getNextTuple());
        assertEquals(new Tuple("1250,1432,9373"), scanOperator.getNextTuple());
        assertEquals(new Tuple("2146,1434,6935"), scanOperator.getNextTuple());
        assertNull(scanOperator.getNextTuple());

        // reset to previous (different) page
        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered, boatsEUnclustedIndexFilePath,
                indexAttributeName, 152,
                155, false);
        assertEquals(new Tuple("8840,152,8953"), scanOperator.getNextTuple());
        assertEquals(new Tuple("6809,153,1173"), scanOperator.getNextTuple());
        assertEquals(new Tuple("462,154,7671"), scanOperator.getNextTuple());
        assertEquals(new Tuple("1431,155,3895"), scanOperator.getNextTuple());
        assertNull(scanOperator.getNextTuple());
        scanOperator.reset();
        assertEquals(new Tuple("8840,152,8953"), scanOperator.getNextTuple());
        assertEquals(new Tuple("6809,153,1173"), scanOperator.getNextTuple());
        assertEquals(new Tuple("462,154,7671"), scanOperator.getNextTuple());
        assertEquals(new Tuple("1431,155,3895"), scanOperator.getNextTuple());
        assertNull(scanOperator.getNextTuple());

        // reset when low key falls on previous page but higher than all keys on that
        // page
        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered, boatsEUnclustedIndexFilePath,
                indexAttributeName, 36, 42, false);
        assertEquals(new Tuple("9176,41,8867"), scanOperator.getNextTuple());
        assertEquals(new Tuple("50,41,1627"), scanOperator.getNextTuple());
        assertEquals(new Tuple("2459,42,1806"), scanOperator.getNextTuple());
        assertEquals(new Tuple("397,42,1792"), scanOperator.getNextTuple());
        assertNull(scanOperator.getNextTuple());
        scanOperator.reset();
        assertEquals(new Tuple("9176,41,8867"), scanOperator.getNextTuple());
        assertEquals(new Tuple("50,41,1627"), scanOperator.getNextTuple());
        assertEquals(new Tuple("2459,42,1806"), scanOperator.getNextTuple());
        assertEquals(new Tuple("397,42,1792"), scanOperator.getNextTuple());
        assertNull(scanOperator.getNextTuple());

        // reset when high key falls on next page but higher than all keys on that page
        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered, boatsEUnclustedIndexFilePath,
                indexAttributeName, 208, 210, false);
        assertEquals(new Tuple("9720,208,5033"), scanOperator.getNextTuple());
        assertEquals(new Tuple("1026,209,3597"), scanOperator.getNextTuple());
        assertNull(scanOperator.getNextTuple());
        scanOperator.reset();
        assertEquals(new Tuple("9720,208,5033"), scanOperator.getNextTuple());
        assertEquals(new Tuple("1026,209,3597"), scanOperator.getNextTuple());
        assertNull(scanOperator.getNextTuple());
    }
}
