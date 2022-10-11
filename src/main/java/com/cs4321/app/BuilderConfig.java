package com.cs4321.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * A data class for storing information regarding the configuration file.
 *
 * @author Lenhard Thomas
 */
public class BuilderConfig {

    public enum Sort {MEMORY, EXTERNAL}

    public enum Join {TNLJ, SMJ, BNLJ}

    private Sort sortType;
    private int sortBufferSize;

    private Join joinType;
    private int joinBufferSize;

    /**
     * Extracts the config info from the file at `filePath`.
     *
     * @param filePath The path to the config file.
     */
    public BuilderConfig(String filePath) {
        try {
            List<String> configLines = Files.readAllLines(Paths.get(filePath));
            // Extract join config info
            String join_line = configLines.get(0);
            String[] join_configs = join_line.split(" ");
            if (join_configs[0].equals("0")) {
                joinType = Join.TNLJ;
            } else if (join_configs[0].equals("1")) {
                joinType = Join.BNLJ;
                joinBufferSize = Integer.parseInt(join_configs[1]);
            } else {
                joinType = Join.SMJ;
            }
            // Extract sort config info
            String sort_line = configLines.get(1);
            String[] sort_configs = sort_line.split(" ");
            if (sort_configs[0].equals("0")) {
                sortType = Sort.MEMORY;
            } else {
                sortType = Sort.EXTERNAL;
                sortBufferSize = Integer.parseInt(sort_configs[1]);
            }
        } catch (IOException e) {
            Logger.getInstance().log("Invalid file path for config.");
        }
    }

    /**
     * Gets the sort type that dictates how to sort tuples.
     *
     * @return The sort type from the config file.
     */
    public Sort getSortType() {
        return sortType;
    }

    /**
     * The size of the buffer to use when sorting; 0 if sorting doesn't use buffer.
     *
     * @return Size of buffer for sorting.
     */
    public int getSortBufferSize() {
        return sortBufferSize;
    }

    /**
     * Gets the join type that dictates how to join tables.
     *
     * @return The join type from the config file.
     */
    public Join getJoinType() {
        return joinType;
    }

    /**
     * The size of the buffer to use when joining; 0 if joining doesn't use buffer.
     *
     * @return Size of buffer for joining.
     */
    public int getJoinBufferSize() {
        return joinBufferSize;
    }
}
