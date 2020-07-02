package com.iogogogo.definition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by tao.zeng on 2020/6/3.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDefinition implements Serializable {

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 执行SQL
     */
    private String sql;

    /**
     * Flink SQL Table 定义
     */
    private JobDefinitionTable jobTable;

    /**
     * stream source
     */
    private Map<String, Object> jobSource;

    /**
     * stream sink
     */
    private Map<String, Object> jobSink;

    /**
     * Created by tao.zeng on 2020/6/3.
     * <p>
     * table 定义
     */
    @Data
    public static class JobDefinitionTable implements Serializable {

        private String dimension;

        private JobTableSource tableSource;

        private JobTableSink tableSink;

        /**
         * Created by tao.zeng on 2020/6/3.
         * <p>
         * 输入转table 定义
         */
        @Data
        public static class JobTableSource implements Serializable {

            private String name;

            private JobDefRowtime rowtime;

            private JobDefProctime proctime;

            private Map<String, List<String>> columns;
        }

        /**
         * Created by tao.zeng on 2020/6/3.
         * <p>
         * 输出table 定义
         */
        @Data
        public static class JobTableSink implements Serializable {

            private String name;

            private String semantic;
        }

        /**
         * Created by tao.zeng on 2020/6/3.
         * <p>
         * rowtime 定义
         */
        @Data
        public static class JobDefRowtime implements Serializable {

            private boolean debugLog;

            private long watermark;

            private String field;

            private boolean enable;

            private JobDefTsTransfer tsTransfer;

            private JobDefNumberToLong numberToLong;
        }

        /**
         * Created by tao.zeng on 2020/6/3.
         * <p>
         * proctime 定义
         */
        @Data
        public static class JobDefProctime implements Serializable {

            private String field;

            private boolean enable;

            private JobDefTsTransfer tsTransfer;
        }

        /**
         * Created by tao.zeng on 2020/6/3.
         * <p>
         * eventTime时间类型转化，预处理
         */
        @Data
        public static class JobDefTsTransfer implements Serializable {

            private boolean enable;

            private String src;

            private String target;

            private String tsFormat;

            private boolean isJava8;
        }

        /**
         * Created by tao.zeng on 2020/6/4.
         * <p>
         * float to long
         */
        @Data
        public static class JobDefNumberToLong implements Serializable {

            private boolean enable;

            private String src;

            private String target;
        }
    }
}
