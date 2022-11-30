package com.cs4321.physicaloperators;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;

import com.cs4321.logicaloperators.LogicalOperator;
import com.cs4321.app.AliasMap;
import com.cs4321.app.ColumnStatsInfo;
import com.cs4321.app.DSUExpressionVisitor;
import com.cs4321.app.DatabaseCatalog;
import com.cs4321.app.Logger;
import com.cs4321.app.TableStatsInfo;
import com.cs4321.app.UnionFindElement;
import com.cs4321.logicaloperators.LogicalScanOperator;
import com.cs4321.logicaloperators.LogicalSelectionOperator;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectBody;
import utils.LogicalQueryPlanUtils;
import utils.Utils;
import static org.mockito.ArgumentMatchers.any;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;

import net.sf.jsqlparser.parser.ParseException;

@Disabled
class JoinOrderTest {

        @BeforeAll
        static void setup() {

        }

        @Test
        void getJoinOrder() throws ParseException {
                try (MockedStatic<DatabaseCatalog> dbcMockedStatic = Mockito.mockStatic(DatabaseCatalog.class)) {
                        DatabaseCatalog dbc = Mockito.mock(DatabaseCatalog.class);
                        dbcMockedStatic.when(DatabaseCatalog::getInstance).thenReturn(dbc);

                        ColumnStatsInfo sailorsA = new ColumnStatsInfo("A");
                        ColumnStatsInfo sailorsB = new ColumnStatsInfo("B");
                        ColumnStatsInfo sailorsC = new ColumnStatsInfo("C");
                        sailorsA.setMinValue(1);
                        sailorsA.setMaxValue(6);
                        sailorsB.setMinValue(100);
                        sailorsB.setMaxValue(300);
                        sailorsC.setMinValue(50);
                        sailorsC.setMaxValue(400);

                        ColumnStatsInfo bD = new ColumnStatsInfo("D");
                        ColumnStatsInfo bE = new ColumnStatsInfo("E");
                        ColumnStatsInfo bF = new ColumnStatsInfo("F");
                        bD.setMinValue(101);
                        bD.setMaxValue(107);
                        bE.setMinValue(1);
                        bE.setMaxValue(104);
                        bF.setMinValue(1);
                        bF.setMaxValue(8);

                        ColumnStatsInfo rG = new ColumnStatsInfo("G");
                        ColumnStatsInfo rH = new ColumnStatsInfo("H");
                        rG.setMinValue(1);
                        rG.setMaxValue(4);
                        rH.setMinValue(101);
                        rH.setMaxValue(104);

                        TableStatsInfo sailorsInfo = new TableStatsInfo(Arrays.asList(sailorsA, sailorsB, sailorsC),
                                        "Sailors");
                        TableStatsInfo boatsInfo = new TableStatsInfo(Arrays.asList(bD, bE, bF), "Boats");
                        TableStatsInfo reservesInfo = new TableStatsInfo(Arrays.asList(rG, rH), "Reserves");
                        sailorsInfo.setNumberOfTuples(6);
                        boatsInfo.setNumberOfTuples(5);
                        // set to 7 to avoid tie-breaking when choosing join orders
                        reservesInfo.setNumberOfTuples(7);

                        Map<String, TableStatsInfo> tableStatsMap = new HashMap<>();
                        tableStatsMap.put("Sailors", sailorsInfo);
                        tableStatsMap.put("Boats", boatsInfo);
                        tableStatsMap.put("Reserves", reservesInfo);

                        Mockito.when(dbc.getTableStatsMap()).thenReturn(tableStatsMap);
                        String[] sailorsSchema = new String[] { "Sailors", "A", "B", "C" };
                        String[] boatsSchema = new String[] { "Boats", "D", "E", "F" };
                        String[] reservesSchema = new String[] { "Reserves", "G", "H" };

                        Mockito.when(dbc.tableSchema("Sailors")).thenReturn(sailorsSchema);
                        Mockito.when(dbc.tableSchema("Boats")).thenReturn(boatsSchema);
                        Mockito.when(dbc.tableSchema("Reserves")).thenReturn(reservesSchema);

                        AliasMap aliasMap = Mockito.mock(AliasMap.class);
                        Mockito.when(aliasMap.getBaseTable("Sailors")).thenReturn("Sailors");
                        Mockito.when(aliasMap.getBaseTable("Boats")).thenReturn("Boats");
                        Mockito.when(aliasMap.getBaseTable("Reserves")).thenReturn("Reserves");
                        Mockito.when(aliasMap.getBaseTable("S")).thenReturn("Sailors");
                        Mockito.when(aliasMap.getBaseTable("B")).thenReturn("Boats");
                        Mockito.when(aliasMap.getBaseTable("R")).thenReturn("Reserves");

                        Mockito.when(aliasMap.columnWithBaseTable(any())).then(
                                        invocation -> invocation.getArgument(0, Column.class).getWholeColumnName());

                        LogicalScanOperator sailorsScan = Mockito.mock(LogicalScanOperator.class);
                        LogicalScanOperator boatsScan = Mockito.mock(LogicalScanOperator.class);
                        LogicalScanOperator reservesScan = Mockito.mock(LogicalScanOperator.class);
                        Table sailorsTable = Mockito.mock(Table.class);
                        Table boatsTable = Mockito.mock(Table.class);
                        Table reservesTable = Mockito.mock(Table.class);

                        sailorsTable.setName("Sailors");

                        Mockito.when(sailorsTable.getName()).thenReturn("Sailors");
                        Mockito.when(boatsTable.getName()).thenReturn("Boats");
                        Mockito.when(reservesTable.getName()).thenReturn("Reserves");
                        Mockito.when(sailorsScan.getBaseTableName()).thenReturn("Sailors");
                        Mockito.when(boatsScan.getBaseTableName()).thenReturn("Boats");
                        Mockito.when(reservesScan.getBaseTableName()).thenReturn("Reserves");
                        Mockito.when(sailorsScan.getTable()).thenReturn(sailorsTable);
                        Mockito.when(boatsScan.getTable()).thenReturn(boatsTable);
                        Mockito.when(reservesScan.getTable()).thenReturn(reservesTable);
                        Mockito.when(sailorsScan.getTableName()).thenReturn("Sailors");
                        Mockito.when(boatsScan.getTableName()).thenReturn("Boats");
                        Mockito.when(reservesScan.getTableName()).thenReturn("Reserves");

                        LogicalSelectionOperator sailorsSelection = Mockito.mock(LogicalSelectionOperator.class);
                        LogicalSelectionOperator reservesSelection = Mockito.mock(LogicalSelectionOperator.class);
                        Mockito.when(sailorsSelection.getChild()).thenReturn(sailorsScan);
                        Mockito.when(reservesSelection.getChild()).thenReturn(reservesScan);

                        List<LogicalOperator> joinChildren = Arrays.asList(sailorsSelection, reservesSelection,
                                        boatsScan);
                        List<LogicalOperator> joinChildren2 = Arrays.asList(boatsScan, sailorsSelection,
                                        reservesSelection);

                        PlainSelect selectBody = Utils.getSelectBody("Sailors, Boats, Reserves",
                                        "Sailors.A > 2 AND Reserves.G = Sailors.A AND Sailors.C > Reserves.H");
                        List<Table> tables = new ArrayList<>();
                        tables.add((Table) selectBody.getFromItem());
                        List<Join> joins = selectBody.getJoins();
                        for (Join join : joins) {
                                tables.add((Table) join.getRightItem());
                        }
                        Expression whereExpression = Utils.getExpression("Sailors, Boats, Reserves",
                                        "Sailors.A > 2 AND Reserves.G = Sailors.A AND Sailors.C > Reserves.H");

                        List<LogicalOperator> correctOrder = Arrays.asList(sailorsSelection, reservesSelection,
                                        boatsScan);

                        // expression without aliases
                        assertEquals(correctOrder,
                                        JoinOrder.getJoinOrder(joinChildren, whereExpression, aliasMap));
                        assertEquals(correctOrder,
                                        JoinOrder.getJoinOrder(joinChildren2, whereExpression, aliasMap));

                        // expression with aliases
                        Mockito.when(sailorsScan.getTableName()).thenReturn("S");
                        Mockito.when(boatsScan.getTableName()).thenReturn("B");
                        Mockito.when(reservesScan.getTableName()).thenReturn("R");
                        Mockito.when(sailorsTable.getAlias()).thenReturn("S");
                        Mockito.when(boatsTable.getAlias()).thenReturn("B");
                        Mockito.when(reservesTable.getAlias()).thenReturn("R");

                        Expression whereExpression2 = Utils.getExpression("Sailors S, Boats B, Reserves R",
                                        "S.A > 2 AND R.G = S.A AND S.C > R.H");
                        assertEquals(correctOrder, JoinOrder.getJoinOrder(joinChildren, whereExpression2, aliasMap));
                        assertEquals(correctOrder, JoinOrder.getJoinOrder(joinChildren2, whereExpression2, aliasMap));

                        Expression whereExpression3 = Utils.getExpression("Sailors S, Boats B, Reserves R",
                                        "B.E = R.G AND 4 > R.G AND 399 >= S.C AND B.D < 105 AND 105 <> B.F AND B.F = S.A AND B.D = R.H");

                        List<LogicalOperator> correctOrder3 = Arrays.asList(boatsScan, reservesSelection,
                                        sailorsSelection);
                        assertEquals(correctOrder3, JoinOrder.getJoinOrder(joinChildren, whereExpression3, aliasMap));
                        assertEquals(correctOrder3, JoinOrder.getJoinOrder(joinChildren2, whereExpression3, aliasMap));

                }

        }
}