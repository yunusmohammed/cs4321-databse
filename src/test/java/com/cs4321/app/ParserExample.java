package com.cs4321.app;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

/** Example class for getting started with JSQLParser. Reads SQL statements from a file and prints
 * them to screen; then extracts SelectBody from each query and also prints it to screen.
 *
 * @author Lucja Kot */
public class ParserExample {

    private static final String queriesFile= "/Users/jessicatweneboah/cs4321-databse/src/test/resources/input/queries.sql";

    public static void main(String[] args) {
        try {
//            System.out.println("Working Directory = " + System.getProperty("user.dir"));
            String file_path = "/Users/jessicatweneboah/cs4321-databse/src/test/java/com/cs4321/app/new_file";
            File new_file = new File(file_path);
            if (new_file.isFile()){
                new_file.delete();
            }
            Files.createFile(Paths.get(file_path));

            FileWriter fileWriter = new FileWriter(file_path);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println("Some String");
            printWriter.println("Product name is %s and its price is %d $");
            printWriter.close();
            return;

//            CCJSqlParser parser= new CCJSqlParser(new FileReader(queriesFile));
//            Statement statement;
//            while ((statement= parser.Statement()) != null) {
//                System.out.println("Read statement: " + statement);
//                Select select= (Select) statement;
//
//                PlainSelect selectBody= (PlainSelect) select.getSelectBody();
//                System.out.println("Select body is " + selectBody);
//
//                List<SelectItem> selectItemsList = selectBody.getSelectItems();
//                System.out.println(selectItemsList);
//
//                FromItem fromItem= selectBody.getFromItem();
//                System.out.println("From Table is " + fromItem);
//
//                List<Join> otherFromItemsArrayList= selectBody.getJoins();
//                System.out.println("Other from tables: " + otherFromItemsArrayList);
//
//                Expression whereExpression= selectBody.getWhere();
//                System.out.println("Where Expression: " + whereExpression);
//                System.out.println();
//
//            }
        } catch (Exception e) {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
    }
}