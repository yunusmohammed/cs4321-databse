package com.cs4321.app;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class TupleReader {
    static FileInputStream fin;
    static FileChannel fc;
    static ByteBuffer buffer;

    public TupleReader(String filename) throws IOException {
        fin = new FileInputStream(filename);
        fc = fin.getChannel();
        buffer = ByteBuffer.allocate(4096);
    }

    public static List<Tuple> readFromFile() throws IOException {
        List<Tuple> tupleList = new ArrayList<>();
        int checkEndOfFile = fc.read(buffer);
        if (checkEndOfFile == -1) {
            return null;
        }
        buffer.clear();
        int i = 0;
        int tupleSize = 0;
        int pageSize = 0;
        while (buffer.hasRemaining()) {
            if (i == 0) {
                tupleSize = buffer.getInt();
            } else if (i == 1){
                pageSize = buffer.getInt();
            } else {
                if (tupleList.size() == pageSize) {
                    break;
                }
                StringBuilder data = new StringBuilder();
                for (int idx = 0; idx < tupleSize; idx++) {
                    data.append(buffer.getInt());
                    if (idx != tupleSize - 1) {
                        data.append(",");
                    }
                }
                tupleList.add(new Tuple(data.toString()));
            }
            i++;
        }
        while (buffer.hasRemaining()) {
            buffer.getInt();
        }
        // Reference: https://stackoverflow.com/a/14937929/13636444
        buffer.clear();
        buffer.put(new byte[4096]);
        buffer.clear();
        return tupleList;
    }

    public static void main(String[] args) throws IOException {
        new TupleReader("src/main/java/com/cs4321/app/query1");
        List<Tuple> tupleList = readFromFile();
        while (tupleList != null) {
            System.out.println(tupleList.size());
            tupleList = readFromFile();
        }
    }

//    public static void close() throws IOException {
//        fin.close();
//    }

//    public static void reset() {
//        buffer.flip();
//    }

}
