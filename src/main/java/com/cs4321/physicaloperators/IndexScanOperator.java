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
  private List<Tuple> tupleList = new ArrayList<Tuple>();
  private int tupleNextIndex = 0;
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
   * Gets the next tuple of the IndexScanOperator’s output
   *
   * @return The next tuple of the IndexScanOperator’s output
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
      if (this.tupleNextIndex < this.tupleList.size()) {
        Tuple nextTuple = this.tupleList.get(this.tupleNextIndex);
        this.tupleNextIndex++;
        return nextTuple;
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
        this.tupleList = getValidTuplesOnPageUnclustered(true);
        this.tupleNextIndex = 0;
        return this.getNextTuple();
      } catch (Exception e) {
        logger.log(e.getMessage());
      }

      return null;
    }
  }

  /**
   * Resets the Table Index of the next tuple of the
   * ScanOperator’s output to the beginning of the table
   */
  @Override
  public void reset() {
    this.tuplesExhausted = false;
    clearBuffer();

    try {
      fc.position(this.firstRelevantLeafPage * PAGE_SIZE);
      readIntoBuffer();
      buffer.getInt(); // discard flag

      if (this.indexIsClustered) {
        // TODO: Further optimize this case
        initializeTupleReaderClustered();
      } else {
        this.tupleList = this.getValidTuplesOnPageUnclustered(false);
        this.tupleNextIndex = 0;
      }

    } catch (Exception e) {
      logger.log(e.getMessage());
    }
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
      this.tupleList = this.getValidTuplesOnPageUnclustered(false);
    }
  }

  private List<Tuple> getValidTuplesOnPageUnclustered(boolean startFromFirstTuple) throws IOException {
    List<Tuple> validTuples = new ArrayList<Tuple>();
    int numberOfDataEntries = buffer.getInt();
    DataEntry[] dataEntriesOnPage = this.getDataEntriesOnPage(numberOfDataEntries);

    // Find correct DataEntry to start returning from using binary search
    int start = 0;
    if (!startFromFirstTuple)
      start = findIndexOfFirstValidTuple(dataEntriesOnPage, numberOfDataEntries);

    if (start < numberOfDataEntries) {
      // Fill up validTuples with predicate satisfying tuples
      while (start < numberOfDataEntries && dataEntriesOnPage[start].getKey() <= highKey) {
        List<Rid> rIds = dataEntriesOnPage[start].getRids();
        for (Rid rid : rIds) {
          validTuples.add(this.tupleReader.randomAccess(rid.getPageId(), rid.getTupleId()));
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
    return validTuples;
  }

  private void initializeTupleReaderClustered() throws IOException {
    int numberOfDataEntries = buffer.getInt();
    DataEntry[] dataEntriesOnPage = this.getDataEntriesOnPage(numberOfDataEntries);
    int start = findIndexOfFirstValidTuple(dataEntriesOnPage, numberOfDataEntries);
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

  private int findIndexOfFirstValidTuple(DataEntry[] dataEntriesOnPage, int numberOfDataEntries) {
    int start = 0;
    int end = numberOfDataEntries - 1;
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

  private void clearBuffer() {
    buffer.clear();
    buffer.put(new byte[PAGE_SIZE]);
    buffer.clear();
  }

  private void readIntoBuffer() throws IOException {
    fc.read(buffer);
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
