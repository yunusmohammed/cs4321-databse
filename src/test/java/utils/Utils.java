package utils;

import java.io.StringReader;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

/**
 * @author Yunus (ymm26@cornell.edu)
 * 
 *         Contains test utility functions
 */
public class Utils {
  public static Expression getExpression(String tableNames, String s) throws ParseException {
    String query = "SELECT * FROM " + tableNames + " WHERE " + s + ";";
    StringReader reader = new StringReader(query);
    Statement statement = new CCJSqlParser(reader).Statement();
    Select select = (Select) statement;
    PlainSelect selectBody = (PlainSelect) select.getSelectBody();
    return selectBody.getWhere();

  }
}
