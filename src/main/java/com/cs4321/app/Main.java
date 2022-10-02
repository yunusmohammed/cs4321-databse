package com.cs4321.app;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        TupleReader reader = new TupleReader("/Users/jessicatweneboah/cs4321-databse/src/test/resources/correctOutput_binary/query2");
        List<Tuple> lst = reader.readFromFile();
        System.out.println(lst);
        System.out.println(lst.size());

        reader = new TupleReader("/Users/jessicatweneboah/cs4321-databse/output/query2");
        List<Tuple> lst1 = reader.readFromFile();
        System.out.println(lst1);
        System.out.println(lst1.size());
    }
}
