package com.cs4321.app.planbuilder;

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
            // Extract sort config ino
            String sort_line = configLines.get(1);
            String[] sort_configs = sort_line.split(" ");
            if (sort_configs[0].equals("0")) {
                sortType = Sort.MEMORY;
            } else {
                sortType = Sort.EXTERNAL;
                sortBufferSize = Integer.parseInt(sort_configs[1]);
            }
        } catch (Exception ignored) {
        }
    }

    public Sort getSortType() {
        return sortType;
    }

    public int getSortBufferSize() {
        return sortBufferSize;
    }


    public Join getJoinType() {
        return joinType;
    }

    public int getJoinBufferSize() {
        return joinBufferSize;
    }
}
