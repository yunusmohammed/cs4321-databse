package com.cs4321.indexes;

import java.util.HashMap;
import java.util.List;

public class IndexNode extends Node{
    int numberOfKeys;
    List<Integer> keysList;
    List<Integer> addressList;
    HashMap<Integer, Integer> keyToAddressMap;

    IndexNode () {
        super(1);
    }

    public int getNumberOfKeys() {
        return numberOfKeys;
    }
}
