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
import net.sf.jsqlparser.parser.ParseException;

class JoinOrderTest {

    private final static DatabaseCatalog dbc = Mockito.mock(DatabaseCatalog.class);


    @BeforeAll
    static void setup() {

    }

    @Test
    void getJoinOrder() throws ParseException {
        try (MockedStatic<DatabaseCatalog> dbcMockedStatic = Mockito.mockStatic(DatabaseCatalog.class)) {
            dbcMockedStatic.when(DatabaseCatalog::getInstance).thenReturn(dbc);

            ColumnStatsInfo sailorsA = new ColumnStatsInfo("A");
            sailorsA.setMaxValue(107);

            TableStatsInfo sailorsInfo = new TableStatsInfo(null, null)
            TableStatsInfo boatsInfo = new TableStatsInfo(null, null)
            TableStatsInfo reservesInfo = new TableStatsInfo(null, null)

            Map<String, TableStatsInfo> tableStatsMap = new HashMap<>();


            Mockito.when(dbc.getTableStatsMap()).thenReturn(tableStatsMap);
            // dbc needs tableStatsMap and tableSchema

            // expression without aliases
            List<LogicalOperator> joinChildren = new ArrayList<>();
            PlainSelect selectBody = Utils.getSelectBody("Sailors, Boats, Reserves",
                    "Sailors.A > 2 AND Reserves.G = Sailors.A AND Sailors.C > Reserves.H");
            List<Table> tables = new ArrayList<>();
            tables.add((Table) selectBody.getFromItem());
            List<Join> joins = selectBody.getJoins();
            for (Join join : joins) {
                tables.add((Table) join.getRightItem());
            }
            Expression joinExpression = Utils.getExpression("Sailors, Boats, Reserves", "Reserves.G = Sailors.A");
            Expression whereExpression = Utils.getExpression("Sailors, Boats, Reserves",
                    "Sailors.A > 2 AND Reserves.G = Sailors.A AND Sailors.C > Reserves.H");
            AliasMap aliasMap = Mockito.mock(AliasMap.class);
            Mockito.when(aliasMap.columnWithBaseTable(any())).then(
                    invocation -> invocation.getArgument(0, Column.class).getWholeColumnName());
            assertEquals(Arrays.asList(),
                    JoinOrder.getJoinOrder(joinChildren, tables, joinExpression, whereExpression, aliasMap));

            // expression with aliases
        }

    }
}