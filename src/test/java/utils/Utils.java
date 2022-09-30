package utils;

import java.io.StringReader;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

/**
 * Contains test utility functions
 * 
 * @author Yunus (ymm26@cornell.edu)
 */
public class Utils {

  /**
   * Gets the where expression from the query specified by tableNames and s
   * 
   * @param tableNames comma separated string of the names of the tables to query
   * @param s          the where condition of the query
   * @return the where expression of the query specified by tableNames and s
   * @throws ParseException
   */
  public static Expression getExpression(String tableNames, String s) throws ParseException {
    String query = "SELECT * FROM " + tableNames + " WHERE " + s + ";";
    StringReader reader = new StringReader(query);
    Statement statement = new CCJSqlParser(reader).Statement();
    Select select = (Select) statement;
    PlainSelect selectBody = (PlainSelect) select.getSelectBody();
    return selectBody.getWhere();

  }
}
