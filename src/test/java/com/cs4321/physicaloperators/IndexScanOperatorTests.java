package com.cs4321.physicaloperators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
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
        private static final String unclusteredIndexAttributeName = "E";
        private static final String clusteredIndexAttributeName = "A";
        private static final String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep
                        + "resources" + sep + "input_indexes";
        private static final String boatsEUnclustedIndexFilePath = System.getProperty("user.dir") + sep + "src" + sep
                        + "test"
                        + sep + "resources" + sep + "expected_indexes" + sep + "Boats.E";
        private static final String sailorsAClustedIndexFilePath = System.getProperty("user.dir") + sep + "src" + sep
                        + "test"
                        + sep + "resources" + sep + "expected_indexes" + sep + "Sailors.A";
        private final static DatabaseCatalog dbc = Mockito.mock(DatabaseCatalog.class);
        private static AliasMap mockAliasMapUnclustered;
        private static AliasMap mockAliasMapClustered;

        @BeforeAll
        static void setup() {
                tableUnclustered = new Table();
                tableUnclustered.setName("Boats");
                tableClustered = new Table();
                tableClustered.setName("Sailors2");
                mockAliasMapUnclustered = Mockito.mock(AliasMap.class);
                Mockito.when(mockAliasMapUnclustered.getBaseTable(Mockito.any())).thenReturn("Boats");
                mockAliasMapClustered = Mockito.mock(AliasMap.class);
                Mockito.when(mockAliasMapClustered.getBaseTable(Mockito.any())).thenReturn("Sailors2");

                HashMap<String, Integer> boatsColumnMap = new HashMap<>();
                boatsColumnMap.put("D", 0);
                boatsColumnMap.put("E", 1);
                boatsColumnMap.put("F", 2);

                HashMap<String, Integer> sailors2ColumnMap = new HashMap<>();
                sailors2ColumnMap.put("A", 0);
                sailors2ColumnMap.put("B", 1);
                sailors2ColumnMap.put("C", 2);

                Mockito.when(dbc.tablePath(Mockito.any())).thenCallRealMethod();
                Mockito.when(dbc.columnMap("Boats")).thenReturn(boatsColumnMap);
                Mockito.when(dbc.columnMap("Sailors2")).thenReturn(sailors2ColumnMap);
                // Mockito.when(dbc.tableSchema(Mockito.any())).thenCallRealMethod();
        }

        @Test
        void getNextTupleUnclusteredIndexTest() throws FileNotFoundException, IOException {
                try (MockedStatic<DatabaseCatalog> dbcMockedStatic = Mockito.mockStatic(DatabaseCatalog.class)) {
                        dbcMockedStatic.when(DatabaseCatalog::getInputdir).thenReturn(inputdir);
                        assertEquals(inputdir, DatabaseCatalog.getInputdir());
                        dbcMockedStatic.when(DatabaseCatalog::getInstance).thenReturn(dbc);

                        // lowKey equal to highKey
                        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered,
                                        boatsEUnclustedIndexFilePath,
                                        unclusteredIndexAttributeName, 10, 10, false);
                        assertEquals(new Tuple("4748,10,1432"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan multiple items from single index key
                        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered,
                                        boatsEUnclustedIndexFilePath,
                                        unclusteredIndexAttributeName, 54, 54, false);
                        assertEquals(new Tuple("4631,54,3817"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("5868,54,1430"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("6967,54,6559"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("7746,54,9316"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan some Items on one leaf page, with both low and high key present
                        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered,
                                        boatsEUnclustedIndexFilePath,
                                        unclusteredIndexAttributeName, 6, 10,
                                        false);
                        assertEquals(new Tuple("4225,6,9275"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("2521,8,9005"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("4748,10,1432"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan Items on one leaf page with low key present but high key absent
                        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered,
                                        boatsEUnclustedIndexFilePath,
                                        unclusteredIndexAttributeName, 13,
                                        15, false);
                        assertEquals(new Tuple("3172,13,5062"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("3169,14,1890"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan Items on one leaf page with low key absent but high key present
                        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered,
                                        boatsEUnclustedIndexFilePath,
                                        unclusteredIndexAttributeName, 9, 13,
                                        false);
                        assertEquals(new Tuple("4748,10,1432"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("3172,13,5062"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan Items on one leaf page with both low key and high key absent
                        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered,
                                        boatsEUnclustedIndexFilePath,
                                        unclusteredIndexAttributeName, 9, 15,
                                        false);
                        assertEquals(new Tuple("4748,10,1432"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("3172,13,5062"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("3169,14,1890"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan Items across multiple leaf pages
                        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered,
                                        boatsEUnclustedIndexFilePath,
                                        unclusteredIndexAttributeName, 152,
                                        155, false);
                        assertEquals(new Tuple("8840,152,8953"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("6809,153,1173"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("462,154,7671"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("1431,155,3895"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan last few items on non-last leaf page with high key higher than all keys
                        // present in that leaf page
                        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered,
                                        boatsEUnclustedIndexFilePath,
                                        unclusteredIndexAttributeName, 208,
                                        210, false);
                        assertEquals(new Tuple("9720,208,5033"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("1026,209,3597"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan last few items on last leaf page with high key higher than all keys
                        // present
                        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered,
                                        boatsEUnclustedIndexFilePath,
                                        unclusteredIndexAttributeName, 9998,
                                        20000,
                                        false);
                        assertEquals(new Tuple("6437,9998,2317"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("8439,9998,6378"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("4461,9999,4000"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("5317,9999,266"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan items on next page with low key falling on previous page
                        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered,
                                        boatsEUnclustedIndexFilePath,
                                        unclusteredIndexAttributeName, 36,
                                        42,
                                        false);
                        assertEquals(new Tuple("9176,41,8867"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("50,41,1627"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("2459,42,1806"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("397,42,1792"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan first few items of first page with low key equal to first key
                        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered,
                                        boatsEUnclustedIndexFilePath,
                                        unclusteredIndexAttributeName, 4, 6,
                                        false);
                        assertEquals(new Tuple("9206,4,5488"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("7775,4,6175"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("9076,4,8209"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("1803,5,8850"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("1109,5,9486"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("4225,6,9275"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan first few items of first page with low key lower than all keys present
                        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered,
                                        boatsEUnclustedIndexFilePath,
                                        unclusteredIndexAttributeName, 1, 6,
                                        false);
                        assertEquals(new Tuple("9206,4,5488"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("7775,4,6175"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("9076,4,8209"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("1803,5,8850"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("1109,5,9486"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("4225,6,9275"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan few items on non-first page
                        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered,
                                        boatsEUnclustedIndexFilePath,
                                        unclusteredIndexAttributeName, 1431,
                                        1434,
                                        false);
                        assertEquals(new Tuple("2884,1431,9374"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("1250,1432,9373"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("2146,1434,6935"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());
                }
        }

        @Test
        void getNextTupleClusteredIndexTest() throws FileNotFoundException, IOException {
                try (MockedStatic<DatabaseCatalog> dbcMockedStatic = Mockito.mockStatic(DatabaseCatalog.class)) {
                        dbcMockedStatic.when(DatabaseCatalog::getInputdir).thenReturn(inputdir);
                        assertEquals(inputdir, DatabaseCatalog.getInputdir());
                        dbcMockedStatic.when(DatabaseCatalog::getInstance).thenReturn(dbc);

                        // lowKey equal to highKey
                        scanOperator = new IndexScanOperator(tableClustered, mockAliasMapClustered,
                                        sailorsAClustedIndexFilePath, clusteredIndexAttributeName,
                                        5, 5, true);
                        assertEquals(new Tuple("5,2230,9911"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan multiple items from single index key
                        scanOperator = new IndexScanOperator(tableClustered, mockAliasMapClustered,
                                        sailorsAClustedIndexFilePath, clusteredIndexAttributeName,
                                        8, 8, true);
                        assertEquals(new Tuple("8,3868,6975"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("8,4437,838"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("8,7075,8898"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("8,7260,262"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan some Items on one leaf page, with both low and high key present
                        scanOperator = new IndexScanOperator(tableClustered, mockAliasMapClustered,
                                        sailorsAClustedIndexFilePath, clusteredIndexAttributeName,
                                        60, 63, true);
                        assertEquals(new Tuple("60,3855,6938"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("61,7014,8858"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("62,5266,4643"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("63,192,6726"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("63,6425,3535"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan Items on one leaf page with low key present but high key absent
                        scanOperator = new IndexScanOperator(tableClustered, mockAliasMapClustered,
                                        sailorsAClustedIndexFilePath, clusteredIndexAttributeName,
                                        137, 143, true);
                        assertEquals(new Tuple("137,5942,5807"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("138,4966,1721"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("140,9061,8176"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("141,2147,508"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan Items on one leaf page with low key absent but high key present
                        scanOperator = new IndexScanOperator(tableClustered, mockAliasMapClustered,
                                        sailorsAClustedIndexFilePath, clusteredIndexAttributeName,
                                        174, 178, true);
                        assertEquals(new Tuple("175,5789,6646"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("176,5521,2534"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("177,8260,7599"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("178,6521,7422"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan Items on one leaf page with both low key and high key absent
                        scanOperator = new IndexScanOperator(tableClustered, mockAliasMapClustered,
                                        sailorsAClustedIndexFilePath, clusteredIndexAttributeName,
                                        168, 174, true);
                        assertEquals(new Tuple("169,1889,4931"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("171,361,8258"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("173,1090,1928"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("173,9037,4442"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan Items across multiple leaf pages
                        scanOperator = new IndexScanOperator(tableClustered, mockAliasMapClustered,
                                        sailorsAClustedIndexFilePath, clusteredIndexAttributeName,
                                        417, 421, true);
                        assertEquals(new Tuple("417,2355,5596"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("418,2151,395"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("420,2074,6516"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("421,4749,8521"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan last few items on non-last leaf page with high key higher than all keys
                        // present in that leaf page
                        scanOperator = new IndexScanOperator(tableClustered, mockAliasMapClustered,
                                        sailorsAClustedIndexFilePath, clusteredIndexAttributeName,
                                        283, 288, true);
                        assertEquals(new Tuple("283,6378,7366"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("283,6403,4377"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("284,9890,3676"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("286,6800,6390"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("286,9725,3160"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan last few items on last leaf page with high key higher than all keys
                        // present
                        scanOperator = new IndexScanOperator(tableClustered, mockAliasMapClustered,
                                        sailorsAClustedIndexFilePath, clusteredIndexAttributeName,
                                        9998, 20000, true);
                        assertEquals(new Tuple("9998,4045,9183"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("9998,7078,3156"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("9998,9268,9155"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("9998,9702,728"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("10000,8661,5994"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan items on next page with low key falling on previous page
                        scanOperator = new IndexScanOperator(tableClustered, mockAliasMapClustered,
                                        sailorsAClustedIndexFilePath, clusteredIndexAttributeName,
                                        2839, 2842, true);
                        assertEquals(new Tuple("2840,3568,8790"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("2841,6158,4294"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("2842,1160,5435"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("2842,4442,2301"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan first few items of first page with low key equal to first key
                        scanOperator = new IndexScanOperator(tableClustered, mockAliasMapClustered,
                                        sailorsAClustedIndexFilePath, clusteredIndexAttributeName,
                                        2840, 2842, true);
                        assertEquals(new Tuple("2840,3568,8790"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("2841,6158,4294"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("2842,1160,5435"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("2842,4442,2301"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan first few items of first page with low key lower than all keys present
                        scanOperator = new IndexScanOperator(tableClustered, mockAliasMapClustered,
                                        sailorsAClustedIndexFilePath, clusteredIndexAttributeName,
                                        0, 2, true);
                        assertEquals(new Tuple("1,1517,5260"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("2,2956,3580"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("2,4052,6863"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("2,9310,7776"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // Scan few items on non-first page
                        scanOperator = new IndexScanOperator(tableClustered, mockAliasMapClustered,
                                        sailorsAClustedIndexFilePath, clusteredIndexAttributeName,
                                        4176, 4179, true);
                        assertEquals(new Tuple("4176,12,3083"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("4176,1072,3291"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("4176,3710,8133"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("4176,8100,4690"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("4179,1068,740"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("4179,4931,3916"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());
                }
        }

        @Test
        void resetUnclusteredIndexTest() throws FileNotFoundException, IOException {
                try (MockedStatic<DatabaseCatalog> dbcMockedStatic = Mockito.mockStatic(DatabaseCatalog.class)) {
                        dbcMockedStatic.when(DatabaseCatalog::getInputdir).thenReturn(inputdir);
                        assertEquals(inputdir, DatabaseCatalog.getInputdir());
                        dbcMockedStatic.when(DatabaseCatalog::getInstance).thenReturn(dbc);

                        // reset to same page
                        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered,
                                        boatsEUnclustedIndexFilePath,
                                        unclusteredIndexAttributeName, 1431,
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
                        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered,
                                        boatsEUnclustedIndexFilePath,
                                        unclusteredIndexAttributeName, 152,
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
                        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered,
                                        boatsEUnclustedIndexFilePath,
                                        unclusteredIndexAttributeName, 36, 42, false);
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
                        scanOperator = new IndexScanOperator(tableUnclustered, mockAliasMapUnclustered,
                                        boatsEUnclustedIndexFilePath,
                                        unclusteredIndexAttributeName, 208, 210, false);
                        assertEquals(new Tuple("9720,208,5033"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("1026,209,3597"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());
                        scanOperator.reset();
                        assertEquals(new Tuple("9720,208,5033"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("1026,209,3597"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());
                }
        }

        @Test
        void resetClusteredIndexTest() throws FileNotFoundException, IOException {
                try (MockedStatic<DatabaseCatalog> dbcMockedStatic = Mockito.mockStatic(DatabaseCatalog.class)) {
                        dbcMockedStatic.when(DatabaseCatalog::getInputdir).thenReturn(inputdir);
                        assertEquals(inputdir, DatabaseCatalog.getInputdir());
                        dbcMockedStatic.when(DatabaseCatalog::getInstance).thenReturn(dbc);

                        // reset to same page
                        scanOperator = new IndexScanOperator(tableClustered, mockAliasMapClustered,
                                        sailorsAClustedIndexFilePath, clusteredIndexAttributeName,
                                        8, 8, true);
                        assertEquals(new Tuple("8,3868,6975"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("8,4437,838"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("8,7075,8898"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("8,7260,262"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());
                        scanOperator.reset();
                        assertEquals(new Tuple("8,3868,6975"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("8,4437,838"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("8,7075,8898"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("8,7260,262"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // reset to previous (different) page
                        scanOperator = new IndexScanOperator(tableClustered, mockAliasMapClustered,
                                        sailorsAClustedIndexFilePath, clusteredIndexAttributeName,
                                        417, 421, true);
                        assertEquals(new Tuple("417,2355,5596"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("418,2151,395"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("420,2074,6516"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("421,4749,8521"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());
                        scanOperator.reset();
                        assertEquals(new Tuple("417,2355,5596"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("418,2151,395"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("420,2074,6516"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("421,4749,8521"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // reset when low key falls on previous page but higher than all keys on that
                        // page
                        scanOperator = new IndexScanOperator(tableClustered, mockAliasMapClustered,
                                        sailorsAClustedIndexFilePath, clusteredIndexAttributeName,
                                        2839, 2842, true);
                        assertEquals(new Tuple("2840,3568,8790"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("2841,6158,4294"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("2842,1160,5435"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("2842,4442,2301"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());
                        scanOperator.reset();
                        assertEquals(new Tuple("2840,3568,8790"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("2841,6158,4294"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("2842,1160,5435"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("2842,4442,2301"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());

                        // reset when high key higher than all keys on that page
                        scanOperator = new IndexScanOperator(tableClustered, mockAliasMapClustered,
                                        sailorsAClustedIndexFilePath, clusteredIndexAttributeName,
                                        9998, 20000, true);
                        assertEquals(new Tuple("9998,4045,9183"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("9998,7078,3156"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("9998,9268,9155"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("9998,9702,728"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("10000,8661,5994"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());
                        scanOperator.reset();
                        assertEquals(new Tuple("9998,4045,9183"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("9998,7078,3156"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("9998,9268,9155"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("9998,9702,728"), scanOperator.getNextTuple());
                        assertEquals(new Tuple("10000,8661,5994"), scanOperator.getNextTuple());
                        assertNull(scanOperator.getNextTuple());
                }
        }

        @Test
        void toStringTest() throws FileNotFoundException, IOException {
                try (MockedStatic<DatabaseCatalog> dbcMockedStatic = Mockito.mockStatic(DatabaseCatalog.class)) {
                        dbcMockedStatic.when(DatabaseCatalog::getInputdir).thenReturn(inputdir);
                        assertEquals(inputdir, DatabaseCatalog.getInputdir());
                        dbcMockedStatic.when(DatabaseCatalog::getInstance).thenReturn(dbc);

                        String s = String.format("IndexScanOperator{baseTablePath='%s'}", dbc.tablePath("Sailors2"));
                        scanOperator = new IndexScanOperator(tableClustered, mockAliasMapClustered,
                                        sailorsAClustedIndexFilePath, clusteredIndexAttributeName,
                                        8, 8, true);
                        assertEquals(s, scanOperator.toString());
                }
        }

        @Test
        void testToStringForPrinting() throws FileNotFoundException, IOException {
                try (MockedStatic<DatabaseCatalog> dbcMockedStatic = Mockito.mockStatic(DatabaseCatalog.class)) {
                        dbcMockedStatic.when(DatabaseCatalog::getInputdir).thenReturn(inputdir);
                        assertEquals(inputdir, DatabaseCatalog.getInputdir());
                        dbcMockedStatic.when(DatabaseCatalog::getInstance).thenReturn(dbc);

                        scanOperator = new IndexScanOperator(tableClustered, mockAliasMapClustered,
                                        sailorsAClustedIndexFilePath, clusteredIndexAttributeName,
                                        8, 10, true);
                        // Selection at depth 0 of physical query plan tree
                        assertEquals("IndexScan[Sailors2,A,8,10]\n", scanOperator.toString(0));
                        // Projection at depth 3 of physical query plan tree
                        assertEquals("---IndexScan[Sailors2,A,8,10]\n",
                                        scanOperator.toString(3));
                }
        }
}
