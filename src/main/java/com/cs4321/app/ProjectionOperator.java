package com.cs4321.app;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.List;

/**
 * The ProjectOperator support SELECT queries that choose parts of the row.:
 * e.g. SELECT Table.A, Table.B FROM Table
 */
public class ProjectionOperator extends Operator {

    private final ColumnMap columnMap;
    private final List<SelectItem> selectItems;
    private final Operator child;

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
                    int offset = ((JoinOperator) this.child).getTableOffsets().get(columnName);
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
     * Closes the Operator such that subsequent calls to getNextTuple will fail.
     */
    @Override
    public void finalize() {
        this.child.finalize();
    }

}
