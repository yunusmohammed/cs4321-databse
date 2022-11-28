package com.cs4321.app;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;

class UnionFindTest {

    private static Column c1;
    private static Column c2;
    private static Column c3;
    private static Column c4;
    private static Column c5;
    private static Column c6;
    private UnionFind unionFind;

    private static Column toColumn(String column) {
        String[] info = column.split("\\.");
        Table table = new Table();
        table.setName(info[0]);

        return new Column(table, info[1]);
    }

    @BeforeAll
    static void beforeAll() {
        c1 = toColumn("R.A");
        c2 = toColumn("R.B");
        c3 = toColumn("R.C");
        c4 = toColumn("R.D");
        c5 = toColumn("S.C");
        c6 = toColumn("T.X");
    }

    @BeforeEach
    void setUp() {
        AliasMap aliasMap = Mockito.mock(AliasMap.class);
        Mockito.when(aliasMap.columnWithBaseTable(any())).then(
                invocation -> invocation.getArgument(0, Column.class).getWholeColumnName());
        unionFind = new UnionFind(aliasMap);
    }

    @Test
    void nonExistentFind() {
        UnionFindElement element = unionFind.find(c1);

        assertNull(element.getParent());
        assertEquals("[R.A]", element.getAttributes().toString());
    }

    @Test
    void existentFind() {
        // Creates first element
        UnionFindElement element1 = unionFind.find(c1);

        // Element should now exist and be the same as before
        UnionFindElement element2 = unionFind.find(c1);

        assertEquals(element1.getLowerBound(), element2.getLowerBound());
        assertEquals(element1.getUpperBound(), element2.getUpperBound());
        assertEquals(element1.getEqualityConstraint(), element2.getEqualityConstraint());
        assertEquals(element1.getParent(), element2.getParent());
    }

    @Test
    void multipleNonExistentFind() {
        unionFind.find(c1);
        unionFind.find(c2);
        UnionFindElement element = unionFind.find(c5);

        assertNull(element.getParent());
        assertEquals("[S.C]", element.getAttributes().toString());
    }

    @Test
    void mergeTwoElements() {
        unionFind.find(c1);
        unionFind.find(c2);

        unionFind.union(c1, c2);

        UnionFindElement e = unionFind.find(c1);
        assertNull(e.getParent());
        assertEquals("[R.A, R.B]", e.getAttributes().toString());
    }

    @Test
    void mergeMultipleElements() {
        unionFind.find(c1);
        unionFind.find(c2);
        unionFind.find(c3);
        unionFind.find(c4);

        unionFind.union(c1, c2);
        unionFind.union(c2, c3);
        unionFind.union(c3, c4);

        UnionFindElement e = unionFind.find(c1);
        assertNull(e.getParent());
        assertEquals("[R.A, R.B, R.C, R.D]", e.getAttributes().toString());
    }

    @Test
    void mergeTwoForests() {
        unionFind.find(c1);
        unionFind.find(c2);
        unionFind.find(c3);
        unionFind.find(c4);

        unionFind.union(c1, c2);
        unionFind.union(c3, c4);

        UnionFindElement e5 = unionFind.find(c1);
        assertNull(e5.getParent());
        assertEquals("[R.A, R.B]", e5.getAttributes().toString());

        UnionFindElement e6 = unionFind.find(c3);
        assertNull(e6.getParent());
        assertEquals("[R.C, R.D]", e6.getAttributes().toString());
    }

    @Test
    void oneCollection() {
        unionFind.find(c1);
        assertEquals(1, unionFind.getCollections().size());
    }

    @Test
    void multipleCollections() {
        unionFind.find(c1);
        unionFind.find(c2);
        unionFind.find(c3);

        assertEquals(3, unionFind.getCollections().size());

        unionFind.union(c1, c2);
        assertEquals(2, unionFind.getCollections().size());

        unionFind.union(c3, c2);
        assertEquals(1, unionFind.getCollections().size());
    }

    @Test
    void toStringAllNull() {
        unionFind.find(c1);
        unionFind.find(c2);
        unionFind.find(c3);
        unionFind.find(c4);
        unionFind.find(c5);
        unionFind.find(c6);

        unionFind.union(c1, c2);
        unionFind.union(c2, c3);

        unionFind.union(c4, c5);

        String firstElement = "[[R.A, R.B, R.C], equals null, min null, max null]";
        String secondElement = "[[R.D, S.C], equals null, min null, max null]";
        String thirdElement = "[[T.X], equals null, min null, max null]";
        String expected = firstElement + "\n" + secondElement + "\n" + thirdElement;
        assertEquals(expected, unionFind.toString());
    }

    @Test
    void toStringNonNull() {
        unionFind.find(c1);
        unionFind.find(c2);
        unionFind.find(c3);
        unionFind.find(c4);
        unionFind.find(c5);
        unionFind.find(c6);

        unionFind.union(c1, c2);
        unionFind.union(c2, c3);

        unionFind.union(c4, c5);

        unionFind.find(c1).setEqualityConstraint(57);
        unionFind.find(c4).setLowerBound(194);
        unionFind.find(c6).setUpperBound(-50);

        String firstElement = "[[R.A, R.B, R.C], equals 57, min 57, max 57]";
        String secondElement = "[[R.D, S.C], equals null, min 194, max null]";
        String thirdElement = "[[T.X], equals null, min null, max -50]";
        String expected = firstElement + "\n" + secondElement + "\n" + thirdElement;
        assertEquals(expected, unionFind.toString());
    }

}