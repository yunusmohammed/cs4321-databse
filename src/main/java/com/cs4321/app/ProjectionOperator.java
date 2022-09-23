package com.cs4321.app;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The ProjectOperator support SELECT queries that choose parts of the row.:
 * e.g. SELECT Table.A, Table.B FROM Table
 */
public class ProjectionOperator extends Operator {

    private final ColumnMap columnMap;
    private final List<SelectItem> selectItems;
    private final Operator child;
    private final Map<String, Integer> columnOrder;

    /**
     * Creates an Operator that will represent a SELECT statement containing the selection of particular columns
     *
     * @param columnMap   The mapping from a table's column name to the index that column represents in a row
     * @param selectItems The list of columns that will be chosen from a row
     * @param child       The Operator (either Scan or Select) that will provide the rows in a column
     */
    public ProjectionOperator(ColumnMap columnMap, List<SelectItem> selectItems, Operator child) {
        this.columnMap = columnMap;
        this.selectItems = selectItems;
        this.child = child;
        this.columnOrder = new HashMap<>();
        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem item = selectItems.get(i);
            Expression exp = (((SelectExpressionItem) item).getExpression());
            Column c = (Column) exp;
            this.columnOrder.put(c.toString(), i);
        }
    }

    /**
     * If possible, gets the necessary columns of the next row that its child returns
     *
     * @return The next tuple of the ScanOperator’s output
     */
    @Override
    public Tuple getNextTuple() {
        Tuple row = this.child.getNextTuple();
        if (row == null) return null;
        // Extract only the necessary columns
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.selectItems.size(); i++) {
            SelectItem item = selectItems.get(i);
            if (item instanceof AllColumns) {
                builder.append(row);
            } else {
                SelectExpressionItem expItem = (SelectExpressionItem) item;
                Column column = (Column) expItem.getExpression();
                String columnName = column.getColumnName();
                int index = this.columnMap.get((Column) expItem.getExpression());
                if (this.child instanceof JoinOperator) {
                    String tableName = column.getTable().getAlias();
                    tableName = (tableName != null) ? tableName : column.getTable().getName();
                    int offset = ((JoinOperator) this.child).getTableOffsets().get(tableName);
                    index += offset;
                }
                builder.append(row.get(index));
            }
            if (i < this.selectItems.size() - 1) builder.append(",");
        }
        return new Tuple(builder.toString());

    }

    /**
     * Resets the Project Operator such that it starts projecting from the first row.
     */
    @Override
    public void reset() {
        this.child.reset();
    }

    /**
     * Returns the new index of the column after a merge.
     *
     * @param column The column whose index needs to be searched.
     */
    public int getColumnIndex(Column column) {
        return this.columnOrder.get(column.toString());
    }

    /**
     * Closes the Operator such that subsequent calls to getNextTuple will fail.
     */
    @Override
    public void finalize() {
        this.child.finalize();
    }

}
