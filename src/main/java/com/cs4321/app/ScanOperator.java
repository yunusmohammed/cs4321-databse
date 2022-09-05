package com.cs4321.app;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ScanOperator implements Operator {
    private List<Tuple> baseTable;
    private final DatabaseCatalog dbc = DatabaseCatalog.getInstance();
    private int nextIndex = 0;
    private String queryOutputName;

    public ScanOperator(String baseTable) {
        setBaseTable(baseTable);
    }

    public ScanOperator(String baseTable, String queryOutputName)  {
        this(baseTable);
        setQueryOutputName(queryOutputName);
    }

    @Override
    public Tuple getNextTuple() {
        return baseTable.get(getNextIndex());
    }

    @Override
    public void reset() {
        setNextIndex(0);
    }

    @Override
    public void dump() {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(getQueryOutputName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter printWriter = new PrintWriter(fileWriter);
        int tableLength = getBaseTable().size();
        while (getNextIndex() < tableLength) {
            if (getQueryOutputName() == null) {
                System.out.println(getNextTuple());
            } else {
                printWriter.println(getNextTuple().toString());
            }
            setNextIndex(getNextIndex() + 1);
        }
        printWriter.close();
    }

    public List<Tuple> getBaseTable() {
        return baseTable;
    }

    public void setBaseTable(String baseTable) {
        this.baseTable = dbc.getTable(baseTable);
    }

    public int getNextIndex() {
        return nextIndex;
    }

    public void setNextIndex(int nextIndex) {
        this.nextIndex = nextIndex;
    }


    public void setQueryOutputName(String queryOutputName)  {
        this.queryOutputName = queryOutputName;
        File new_file = new File(queryOutputName);
        if (new_file.isFile()) {
            new_file.delete();
        }
        try {
            Files.createFile(Paths.get(queryOutputName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getQueryOutputName() {
        return queryOutputName;
    }
}
