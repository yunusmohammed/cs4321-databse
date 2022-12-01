package com.cs4321.logicaloperators;

import com.cs4321.app.AliasMap;
import com.cs4321.app.PhysicalPlanBuilder;
import com.cs4321.app.UnionFind;
import com.cs4321.physicaloperators.Operator;

import net.sf.jsqlparser.expression.Expression;

import java.util.List;

/**
 * A Logical Join Operator
 *
 * @author Lenhard Thomas
 */
public class LogicalJoinOperator extends LogicalOperator {

    private final Expression joinCondition;
    private final List<LogicalOperator> children;
    private final UnionFind unionFind;
    private final Expression whereExpression;
    private final AliasMap aliasMap;

    public LogicalJoinOperator(Expression joinCondition, List<LogicalOperator> children, UnionFind unionFind,
            Expression whereExpression, AliasMap aliasMap) {
        this.joinCondition = joinCondition;
        this.children = children;
        this.unionFind = unionFind;
        this.whereExpression = whereExpression;
        this.aliasMap = aliasMap;
    }

    /**
     * The remaining join condition that couldn't fit somewhere into the union find
     * data structure.
     *
     * @return The portion of the join condition that couldn't fit in
     */
    public Expression getJoinCondition() {
        return joinCondition;
    }

    /**
     * The where expression of the query from which the join was obtained
     * 
     * @return The where expression of the query from which the join was obtained
     */
    public Expression getWhereExpression() {
        return whereExpression;
    }

    /**
     * The list of children belonging to this logical join operator.
     *
     * @return list of logical children.
     */
    public List<LogicalOperator> getChildren() {
        return children;
    }

    /**
     * Gets the union find that was used to create this logical join operator.
     *
     * @return The union find used to create this logical join operator.
     */
    public UnionFind getUnionFind() {
        return unionFind;
    }

    /**
     * Gets the alias map of this logical join operator.
     *
     * @return The alias map of this logical join operator.
     */
    public AliasMap getAliasMap() {
        return aliasMap;
    }

    /**
     * Accepts the builder to traverse this operator.
     *
     * @param builder The builder that will traverse this operator.
     * @return The phyiscal tree that this logical operator represents.
     */
    @Override
    public Operator accept(PhysicalPlanBuilder builder) {
        return builder.visit(this);
    }

    @Override
    public String toString(int level) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < level; i++) {
            builder.append("-");
        }
        String joinConditionString = "";
        if (this.getJoinCondition() != null)
            joinConditionString = this.getJoinCondition().toString();
        builder.append("Join[" + joinConditionString + "]");
        builder.append("\n");
        builder.append(this.getUnionFind().toString());
        for (LogicalOperator child : this.getChildren()) {
            builder.append(child.toString(level + 1));
        }
        return builder.toString();
    }
}
