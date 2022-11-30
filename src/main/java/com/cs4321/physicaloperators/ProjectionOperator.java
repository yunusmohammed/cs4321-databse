package com.cs4321.physicaloperators;

import com.cs4321.app.AliasMap;
import com.cs4321.app.Tuple;
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

    private final AliasMap aliasMap;
    private final List<SelectItem> selectItems;
    private final Operator child;

    /**
     * Creates an Operator that will represent a SELECT statement containing the
     * selection of particular columns
     *
     * @param aliasMap    The mapping from a table's column name to the index that
     *                    column represents in a row
     * @param selectItems The list of columns that will be chosen from a row
     * @param child       The Operator (either Scan or Select) that will provide the
     *                    rows in a column
     */
    public ProjectionOperator(AliasMap aliasMap, List<SelectItem> selectItems, Operator child) {
        this.aliasMap = aliasMap;
        this.selectItems = selectItems;
        this.child = child;
        Map<String, Integer> columnMap = new HashMap<>();
        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem item = selectItems.get(i);
            Expression exp = (((SelectExpressionItem) item).getExpression());
            Column c = (Column) exp;
            columnMap.put(c.toString(), i);
        }
        this.setColumnMap(columnMap);
    }

    /**
     * If possible, gets the necessary columns of the next row that its child
     * returns
     *
     * @return The next tuple of the ScanOperator’s output
     */
    @Override
    public Tuple getNextTuple() {
        Tuple row = this.child.getNextTuple();
        if (row == null)
            return null;
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
                int index = this.aliasMap.get((Column) expItem.getExpression());
                if (this.child instanceof JoinOperator) {
                    String tableName = column.getTable().getAlias();
                    tableName = (tableName != null) ? tableName : column.getTable().getName();
                    int offset = ((JoinOperator) this.child).getTableOffsets().get(tableName);
                    index += offset;
                }
                builder.append(row.get(index));
            }
            if (i < this.selectItems.size() - 1)
                builder.append(",");
        }
        return new Tuple(builder.toString());

    }

    /**
     * Returns the child of this operator
     */
    public Operator getChild() {
        return this.child;
    }

    /**
     * Returns the select items of this projection operator
     * 
     * @return the select items of this projection operator
     */
    public List<SelectItem> getSelectItems() {
        return this.selectItems;
    }

    /**
     * Resets the Project Operator such that it starts projecting from the first
     * row.
     */
    @Override
    public void reset() {
        this.child.reset();
    }

    /**
     * Returns the string representation of the Projection Operator formatted as
     * ProjectionOperator{child, [c1, c2, ... cn]} where c is the column.
     *
     * @return The string representation of the Projection Operator
     */
    @Override
    public String toString() {
        return "ProjectionOperator{" + this.child.toString() + ", " + this.selectItems.toString() + "}";
    }

    @Override
    public String toString(int level) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < level; i++) {
            builder.append("-");
        }
        builder.append("Project" + this.getSelectItems().toString());
        builder.append("\n");
        builder.append(this.getChild().toString(level + 1));
        return builder.toString();
    }

    /**
     * Closes the Operator such that subsequent calls to getNextTuple will fail.
     */
    @Override
    public void finalize() {
        this.child.finalize();
    }

}
