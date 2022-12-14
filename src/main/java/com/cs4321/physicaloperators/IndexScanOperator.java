package com.cs4321.physicaloperators;

import com.cs4321.app.AliasMap;
import com.cs4321.app.Tuple;
import com.cs4321.indexes.DataEntry;
import com.cs4321.indexes.Rid;
import net.sf.jsqlparser.schema.Table;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * IndexScanOperator supports data scans using B+ Index trees
 */
public class IndexScanOperator extends ScanOperator {

  private final int lowKey;
  private final int highKey;
  private final String indexFilePath;
  private final String indexAttributeName;
  private final boolean indexIsClustered;

  private final int PAGE_SIZE = 4096;

  private int rootIndexNodePageNumber;
  private int numberOfLeafPages;
  private int firstRelevantLeafPage;
  private int currentLeafPage; // 1..numberOfLeafPages inclusive

  // Tuple Information
  private List<Rid> ridList = new ArrayList<Rid>();
  private int ridNextIndex = 0;
  private boolean tuplesExhausted = false;

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
   * @param indexFilePath    The absolute file path of a serialized B+ tree index
   *                         on some key (single attribute). The file must exist
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
  public IndexScanOperator(Table table, AliasMap aliasMap, String indexFilePath, String indexAttributeName,
      Integer lowKey, Integer highKey,
      boolean indexIsClustered) throws FileNotFoundException, IOException {
    super(table, aliasMap);
    this.lowKey = lowKey;
    this.highKey = highKey;
    this.indexIsClustered = indexIsClustered;
    this.indexFilePath = indexFilePath;
    this.indexAttributeName = indexAttributeName;
    this.initIndex();
  }

  /**
   * Gets the next tuple of the IndexScanOperator???s output
   *
   * @return The next tuple of the IndexScanOperator???s output
   * @throws IOException
   */
  @Override
  public Tuple getNextTuple() {
    if (this.indexIsClustered) {
      if (this.tuplesExhausted)
        return null;
      try {
        Tuple nextTuple = this.tupleReader.readNextTuple();
        if (nextTuple != null
            && nextTuple.get(dbc.columnMap(this.baseTableName).get(this.indexAttributeName)) <= this.highKey) {
          return nextTuple;
        }
        this.tuplesExhausted = true;
        return null;
      } catch (IOException e) {
        logger.log(e.getMessage());
      }
      return null;
    } else {
      // Unclustered Index
      if (this.ridNextIndex < this.ridList.size()) {
        try {
          Rid nextRid = this.ridList.get(this.ridNextIndex);
          Tuple nextTuple = this.tupleReader.randomAccess(nextRid.getPageId(), nextRid.getTupleId());
          this.ridNextIndex++;
          return nextTuple;
        } catch (Exception e) {
          logger.log(e.getMessage());
        }
      }

      if (this.tuplesExhausted)
        return null;

      // Fetch next batch of tuples and return next tuple
      this.currentLeafPage += 1;
      try {
        fc.position(this.currentLeafPage * PAGE_SIZE);
        clearBuffer();
        readIntoBuffer();
        buffer.getInt(); // discard flag. Use this to debug that it is actually a leaf page (=0)
        this.ridList = getValidRidsOnPageUnclustered(false);
        this.ridNextIndex = 0;
        return this.getNextTuple();
      } catch (Exception e) {
        logger.log(e.getMessage());
      }

      return null;
    }
  }

  /**
   * Resets the Table Index of the next tuple of the
   * ScanOperator???s output to the beginning of the table
   */
  @Override
  public void reset() {
    this.tuplesExhausted = false;
    clearBuffer();

    try {
      fc.position(this.firstRelevantLeafPage * PAGE_SIZE);
      this.currentLeafPage = this.firstRelevantLeafPage;
      readIntoBuffer();
      buffer.getInt(); // discard flag

      if (this.indexIsClustered) {
        // TODO: Further optimize this case
        initializeTupleReaderClustered();
      } else {
        this.ridList = this.getValidRidsOnPageUnclustered(true);
        this.ridNextIndex = 0;
      }

    } catch (Exception e) {
      logger.log(e.getMessage());
    }
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

  /**
   * Closes the initialised BufferedReader
   */
  @Override
  public void finalize() {
    super.finalize();
    try {
      fin.close();
    } catch (Exception e) {
      logger.log(e.getMessage());
    }
  }

  /**
   * Extends the inializer of the scan operator. Initializes the index tree and
   * retrieves the pointer to the first tuple >= lowkey
   * 
   * @throws FileNotFoundException
   * @throws IOException
   */
  private void initIndex() throws FileNotFoundException, IOException {
    this.fin = new FileInputStream(this.indexFilePath);
    this.fc = fin.getChannel();
    this.buffer = ByteBuffer.allocate(PAGE_SIZE);

    // Read header page
    readIntoBuffer();
    this.rootIndexNodePageNumber = buffer.getInt();
    this.numberOfLeafPages = buffer.getInt();
    // this.treeOrder = buffer.getInt();
    this.clearBuffer();
    this.walkIndexTree();
  }

  /**
   * Walks the index tree to retrieve the page on which the first tuple with key
   * >= lowKey falls on
   * 
   * @throws IOException
   */
  private void walkIndexTree() throws IOException {
    // Read Root Index Page
    fc.position(this.rootIndexNodePageNumber * PAGE_SIZE);
    readIntoBuffer();
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
      while (start <= end) {
        int mid = start + (end - start) / 2;
        if (this.lowKey >= keysOnPage[mid]) {
          start = mid + 1;
        } else {
          end = mid - 1;
        }
      }
      int pageNumberOfChildPage = addressesOnPage[start];
      this.currentLeafPage = pageNumberOfChildPage;
      fc.position(pageNumberOfChildPage * PAGE_SIZE);
      clearBuffer();
      readIntoBuffer();
      flag = buffer.getInt();
    }

    // Buffer now has content of the first relevant leaf page
    this.firstRelevantLeafPage = this.currentLeafPage;
    if (this.indexIsClustered) {
      this.initializeTupleReaderClustered();
    } else {
      this.ridList = this.getValidRidsOnPageUnclustered(true);
    }
  }

  /**
   * Gets the list of the valid Rids on this page with their order maintained
   * 
   * @param initializing true if and only if we are initialing or reinitializing
   *                     (during reset)
   * @return A list of the valid Rids on this page with their order maintained
   * @throws IOException
   */
  private List<Rid> getValidRidsOnPageUnclustered(boolean initializing) throws IOException {
    List<Rid> validRids = new ArrayList<Rid>();
    int numberOfDataEntries = buffer.getInt();
    DataEntry[] dataEntriesOnPage = this.getDataEntriesOnPage(numberOfDataEntries);

    // Find correct DataEntry to start returning from using binary search
    int start = 0;
    if (initializing)
      start = findIndexOfFirstValidTuple(dataEntriesOnPage);

    if (start < numberOfDataEntries) {
      // Fill up validTuples with predicate satisfying tuples
      while (start < numberOfDataEntries && dataEntriesOnPage[start].getKey() <= highKey) {
        List<Rid> rIds = dataEntriesOnPage[start].getRids();
        for (Rid rid : rIds) {
          validRids.add(rid);
        }
        start++;
      }

      if (start < numberOfDataEntries && dataEntriesOnPage[start].getKey() > highKey) {
        // Encountered first key higher than highkey
        this.tuplesExhausted = true;
      }

    }

    if (this.currentLeafPage == this.numberOfLeafPages) {
      // We have read the last leaf page
      this.tuplesExhausted = true;
    }
    return validRids;
  }

  /**
   * Initialize TupleReader to start returning from the first Tuple with key value
   * >= lowKey. Requires that the index is Clustered
   * 
   * @throws IOException
   */
  private void initializeTupleReaderClustered() throws IOException {
    int numberOfDataEntries = buffer.getInt();
    DataEntry[] dataEntriesOnPage = this.getDataEntriesOnPage(numberOfDataEntries);
    int start = findIndexOfFirstValidTuple(dataEntriesOnPage);
    if (start < numberOfDataEntries) {
      if (dataEntriesOnPage[start].getKey() <= this.highKey) {
        int pageIdOfFirstTuple = dataEntriesOnPage[start].getRids().get(0).getPageId();
        int tupleIdOfFirstTuple = dataEntriesOnPage[start].getRids().get(0).getTupleId();
        this.tupleReader.indexReset(pageIdOfFirstTuple, tupleIdOfFirstTuple);
      } else {
        this.tuplesExhausted = true;
      }
    } else {
      if (this.currentLeafPage < this.numberOfLeafPages) {
        this.currentLeafPage++;
        fc.position(this.currentLeafPage * PAGE_SIZE);
        clearBuffer();
        readIntoBuffer();
        buffer.getInt(); // discard flag
        buffer.getInt(); // discard number of entries
        // deserialize first entry on page
        int key = buffer.getInt();
        if (key <= this.highKey) {
          // key > lowKey by the way index tree is walked
          buffer.getInt(); // discard number of rids and serialized first rid
          int pageId = buffer.getInt();
          int tupleId = buffer.getInt();
          this.tupleReader.indexReset(pageId, tupleId);
        } else {
          this.tuplesExhausted = true;
        }
      } else {
        // We have read the last leaf page
        this.tuplesExhausted = true;
      }
    }
  }

  /**
   * Gets the data entries on the leaf page currently read into the buffer.
   * Requires: Buffer has content of a leaf page and the positioned at the first
   * byte of the first data entry
   * 
   * @param numberOfDataEntries The number of data entries in buffer
   * @return
   */
  private DataEntry[] getDataEntriesOnPage(int numberOfDataEntries) {
    DataEntry[] dataEntriesOnPage = new DataEntry[numberOfDataEntries];
    for (int i = 0; i < numberOfDataEntries; i++) {
      int key = buffer.getInt();
      int numberOfRids = buffer.getInt();
      List<Rid> rIds = new ArrayList<Rid>();
      for (int j = 0; j < numberOfRids; j++) {
        int pageId = buffer.getInt();
        int tupleId = buffer.getInt();
        rIds.add(new Rid(pageId, tupleId));
      }
      dataEntriesOnPage[i] = new DataEntry(key, rIds);
    }
    return dataEntriesOnPage;
  }

  /**
   * Finds the index of the first data entry with key >= lowKey using binary
   * search
   * 
   * @param dataEntriesOnPage the data entry list
   * @return the index of the first data entry with key >= lowKey using binary
   *         search
   */
  private int findIndexOfFirstValidTuple(DataEntry[] dataEntriesOnPage) {
    int start = 0;
    int end = dataEntriesOnPage.length - 1;
    while (start <= end) {
      int mid = start + (end - start) / 2;
      if (this.lowKey == dataEntriesOnPage[mid].getKey()) {
        start = mid;
        break;
      } else if (this.lowKey > dataEntriesOnPage[mid].getKey()) {
        start = mid + 1;
      } else {
        end = mid - 1;
      }
    }
    return start;
  }

  /**
   * Clears the buffer and fills it with 0s
   */
  private void clearBuffer() {
    buffer.clear();
    buffer.put(new byte[PAGE_SIZE]);
    buffer.clear();
  }

  /**
   * Reads the next chunk (as pointed to by the file channel) into the buffer
   */
  private void readIntoBuffer() throws IOException {
    fc.read(buffer);
    buffer.clear();
  }

  /**
   * Return the index attribute name of this Index Scan
   * 
   * @return the index attribute name of this Index Scan
   */
  public String getIndexAttributeName() {
    return this.indexAttributeName;
  }

  /**
   * Return the low key of this Index Scan
   * 
   * @return the low key of this Index Scan
   */
  public int getLowKey() {
    return this.lowKey;
  }

  /**
   * Return the high key of this Index Scan
   * 
   * @return the high key of this Index Scan
   */
  public int getHighKey() {
    return this.highKey;
  }

  @Override
  public String toString(int level) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < level; i++) {
      builder.append("-");
    }
    builder.append("IndexScan[");
    builder.append(this.getBaseTableName());
    builder.append(",");
    builder.append(this.getIndexAttributeName());
    builder.append(",");
    builder.append(Integer.toString(this.getLowKey()));
    builder.append(",");
    builder.append(Integer.toString(this.getHighKey()));
    builder.append("]");
    builder.append("\n");
    return builder.toString();
  }
}
