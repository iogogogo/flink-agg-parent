package com.iogogogo.table;

import com.iogogogo.definition.JobDefinition;
import com.iogogogo.exception.BizException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.descriptors.DescriptorProperties;
import org.apache.flink.table.descriptors.Rowtime;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.table.descriptors.SchemaValidator;
import org.apache.flink.table.sources.RowtimeAttributeDescriptor;
import org.apache.flink.table.sources.tsextractors.StreamRecordTimestamp;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.flink.table.descriptors.Schema.SCHEMA;

/**
 * Created by tao.zeng on 2020/6/3.
 */
@Slf4j
@Getter
public class TableSchemaConfig implements Serializable {

    private transient Schema schema;
    private transient TableSchema tableSchema;
    private transient List<RowtimeAttributeDescriptor> rowtimeAttributeDescriptors;
    private transient String proctimeAttribute;


    /**
     * @param jobDefinition 任务定义
     */
    public TableSchemaConfig(JobDefinition jobDefinition) {

        final JobDefinition.JobDefinitionTable.JobTableSource jobTableSource = jobDefinition.getJobTable().getTableSource();
        final JobDefinition.JobDefinitionTable.JobDefRowtime jobDefRowtime = jobTableSource.getRowtime();
        final JobDefinition.JobDefinitionTable.JobDefProctime jobDefProctime = jobTableSource.getProctime();

        try {
            Map<String, List<String>> columns = jobTableSource.getColumns();
            if (MapUtils.isNotEmpty(columns)) {
                schema = buildSchema(columns);
            } else {
                throw new BizException("column is null");
            }

            log.info("schema:{}", schema.toProperties());


            /*
             * 该参数是为了控制，是否在schema生成时，使用 eventTime
             * 如果上游的数据流已经自己生成了event time以及watermark，则可以直接从stream中读取
             */
            String eventTime = jobDefRowtime.getField();
            if (jobDefRowtime.isEnable()) {
                schema = schema.field(Rowtime.ROWTIME, Types.SQL_TIMESTAMP)
                        .rowtime(new Rowtime()
                                .timestampsFromField(eventTime)
                                .watermarksPeriodicBounded(jobDefRowtime.getWatermark())
                        );
            } else {
                schema = schema.field(Rowtime.ROWTIME, Types.SQL_TIMESTAMP)
                        .rowtime(new Rowtime()
                                .timestampsFromExtractor(new StreamRecordTimestamp())
                                .watermarksPeriodicBounded(jobDefRowtime.getWatermark())
                        );
            }

            if (jobDefProctime.isEnable()) {
                schema = schema.field(jobDefProctime.getField(), Types.SQL_TIMESTAMP).proctime();
            }

            Map<String, String> properties = schema.toProperties();
            DescriptorProperties dp = new DescriptorProperties(true);
            dp.putProperties(properties);
            tableSchema = dp.getTableSchema(SCHEMA);
            rowtimeAttributeDescriptors = SchemaValidator.deriveRowtimeAttributes(dp);
            proctimeAttribute = SchemaValidator.deriveProctimeAttribute(dp).orElse(null);
        } catch (Exception ex) {
            log.error("process exception", ex);
        }
    }


    private Schema buildSchema(Map<String, List<String>> columns) {
        Schema schema = new Schema();
        columns.forEach((k, v) -> {

            List<String> collect = v.stream().distinct().collect(Collectors.toList());

            switch (k.toLowerCase()) {
                case "string":
                    collect.forEach(x -> schema.field(x, Types.STRING));
                    break;
                case "boolean":
                    collect.forEach(x -> schema.field(x, Types.BOOLEAN));
                    break;
                case "byte":
                    collect.forEach(x -> schema.field(x, Types.BYTE));
                    break;
                case "short":
                    collect.forEach(x -> schema.field(x, Types.SHORT));
                    break;
                case "int":
                    collect.forEach(x -> schema.field(x, Types.INT));
                    break;
                case "long":
                    collect.forEach(x -> schema.field(x, Types.LONG));
                    break;
                case "float":
                    collect.forEach(x -> schema.field(x, Types.FLOAT));
                    break;
                case "decimal":
                    collect.forEach(x -> schema.field(x, Types.BIG_DEC));
                    break;
                case "double":
                    collect.forEach(x -> schema.field(x, Types.DOUBLE));
                    break;
                case "timestamp":
                    collect.forEach(x -> schema.field(x, Types.SQL_TIMESTAMP));
                    break;
                case "local_date":
                    collect.forEach(x -> schema.field(x, Types.LOCAL_DATE));
                    break;
                case "local_time":
                    collect.forEach(x -> schema.field(x, Types.LOCAL_TIME));
                    break;
                case "local_date_time":
                    collect.forEach(x -> schema.field(x, Types.LOCAL_DATE_TIME));
                    break;
                case "instant":
                    collect.forEach(x -> schema.field(x, Types.INSTANT));
                    break;
            }
        });
        return schema;
    }
}
