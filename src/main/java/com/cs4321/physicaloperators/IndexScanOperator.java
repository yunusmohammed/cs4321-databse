package com.cs4321.physicaloperators;

import com.cs4321.app.AliasMap;
import com.cs4321.app.Tuple;

import net.sf.jsqlparser.schema.Table;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 
 */
public class IndexScanOperator extends ScanOperator {

  private final int lowKey;
  private final int highKey;
  private final String indexFileName;
  private final boolean indexIsClustered;

  private final int PAGE_SIZE = 4096;

  private int rootIndexNodePageNumber;
  private int numberOfLeafPages;
  private int treeOrder;
  private int currentLeafPage; // 1..numberOfLeafPages inclusive

  private FileInputStream fin;
  private FileChannel fc;
  private ByteBuffer buffer;

  /**
   * Constructor that initialises a IndexScanOperator to get tuples from lowKey to
   * highKey from a prebuilt B+ tree index. The tuples produced by this operator
   * are those whose key attribute values >= lowKey and <= highKey
   *
   * @param table            The table in the database the IndexScanOperator is
   *                         scanning
   * @param aliasMap         The mapping from table names to base table names
   * @param indexFileName    The filename of a serialized B+ tree index on some
   *                         key
   *                         (single attribute)
   * @param lowKey           The lowest key value such that all produced tuples
   *                         have
   *                         key attribute value of at least this key
   * @param highKey          The highest key value such that all produced tuples
   *                         have
   *                         key attribute value of at most this key
   * @param indexIsClustered True if and only if the index pointed to by
   *                         indexFileName is clustered
   * @throws IOException
   * @throws FileNotFoundException
   */
  public IndexScanOperator(Table table, AliasMap aliasMap, String indexFileName, Integer lowKey, Integer highKey,
      boolean indexIsClustered) throws FileNotFoundException, IOException {
    super(table, aliasMap);
    this.lowKey = lowKey;
    this.highKey = highKey;
    this.indexIsClustered = indexIsClustered;
    this.indexFileName = indexFileName;
    this.initIndex();
  }

  /**
   * Gets the next tuple of the IndexScanOperator’s output
   *
   * @return The next tuple of the IndexScanOperator’s output
   */
  @Override
  public Tuple getNextTuple() {
    return null;
  }

  /**
   * Resets the Table Index of the next tuple of the
   * ScanOperator’s output to the beginning of the table
   */
  @Override
  public void reset() {
  }

  /**
   * Closes the initialised BufferedReader
   */
  @Override
  public void finalize() {
    super.finalize();
  }

  private void initIndex() throws FileNotFoundException, IOException {
    this.fin = new FileInputStream(this.indexFileName);
    this.fc = fin.getChannel();
    this.buffer = ByteBuffer.allocate(PAGE_SIZE);

    // Read header page
    fc.read(buffer);
    this.rootIndexNodePageNumber = buffer.getInt();
    this.numberOfLeafPages = buffer.getInt();
    this.treeOrder = buffer.getInt();
    this.clearBuffer();
    this.walkIndexTree();

  }

  private void walkIndexTree() throws IOException {
    // Read Root Index Page
    fc.position(this.rootIndexNodePageNumber * PAGE_SIZE);
    fc.read(buffer);
    int flag = buffer.getInt();

    while (flag == 1) {
      int numberOfKeys = buffer.getInt();
      int[] keysOnPage = new int[numberOfKeys];
      for (int i = 0; i < numberOfKeys; i++) {
        keysOnPage[i] = buffer.getInt();
      }

      int[] addressesOnPage = new int[numberOfKeys + 1];
      for (int i = 0; i < numberOfKeys + 1; i++) {
        addressesOnPage[i] = buffer.getInt();
      }

      // find correct next child page using binary search
      int start = 0;
      int end = numberOfKeys - 1;
      int mid;
      while (start < end) {
        mid = start + (end - start) / 2;
        if (this.lowKey >= keysOnPage[mid]) {
          start = mid + 1;
        } else {
          end = mid - 1;
        }
      }
      int pageNumberOfChildPage = addressesOnPage[start];
      fc.position(pageNumberOfChildPage * PAGE_SIZE);
      clearBuffer();
      fc.read(buffer);
      flag = buffer.getInt();
    }

    // Buffer now has content of the first relevant leaf page
    int numberOfDataEntries;

  }

  private void clearBuffer() {
    buffer.clear();
    buffer.put(new byte[PAGE_SIZE]);
    buffer.clear();
  }

  /**
   * Returns the string representation of the Index Scan Operator
   *
   * @return The string representation of the Index Scan Operator
   *         Eg:
   *         IndexScanOperator{baseTablePath='../src/test/resources/input_binary/db/data/Boats'}
   */
  @Override
  public String toString() {
    return "IndexScanOperator{" +
        "baseTablePath='" + baseTablePath + '\'' +
        '}';
  }
}
