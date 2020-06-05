package com.iogogogo.agg;

import com.google.common.collect.Lists;
import com.iogogogo.context.FlinkEnvironment;
import com.iogogogo.definition.JobDefinition;
import com.iogogogo.schema.MapSchema;
import com.iogogogo.table.MapStreamTableSource;
import com.iogogogo.table.TableSchemaConfig;
import com.iogogogo.util.IdHelper;
import com.iogogogo.util.Java8DateTimeUtils;
import com.iogogogo.util.JobDefUtils;
import com.iogogogo.util.JsonParse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.sources.StreamTableSource;
import org.apache.flink.types.Row;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.iogogogo.consts.BaseConsts.TOPIC;
import static com.iogogogo.consts.BaseConsts.TOPICS;

/**
 * Created by tao.zeng on 2020/6/5.
 */
@Slf4j
public class AggSql {

    public void agg(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        JobDefinition jobDefinition = JobDefUtils.getJobDefinition(parameterTool);

        log.info("jobDefinition:{}", jobDefinition);

        Tuple2<StreamExecutionEnvironment, StreamTableEnvironment> context = FlinkEnvironment.context();

        StreamExecutionEnvironment env = context.f0;
        StreamTableEnvironment tableEnv = context.f1;

        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        final Map<String, Object> jobDefinitionJobSource = jobDefinition.getJobSource();

        Properties props = MapUtils.toProperties(jobDefinitionJobSource);

        @SuppressWarnings("unchecked")
        List<String> inTopicList = (List<String>) jobDefinitionJobSource.getOrDefault(TOPICS, Lists.newArrayList());

        DataStream<Map<String, Object>> sourceDataStream = env
                .addSource(new FlinkKafkaConsumer<>(inTopicList, new MapSchema(jobDefinition), props))
                .uid(IdHelper.id())
                .name("Deserialization Messages");

        sourceDataStream.print();

        toTable(tableEnv, sourceDataStream, jobDefinition, inTopicList);


        env.execute(jobDefinition.getJobName());
    }

    private void toTable(StreamTableEnvironment tableEnv, DataStream<Map<String, Object>> sourceDataStream, JobDefinition jobDefinition, List<String> inTopicList) {
        final Map<String, Object> jobSink = jobDefinition.getJobSink();
        final JobDefinition.JobDefinitionTable jobTable = jobDefinition.getJobTable();
        final JobDefinition.JobDefinitionTable.JobTableSource jobTableSource = jobTable.getTableSource();
        final JobDefinition.JobDefinitionTable.JobTableSink jobTableSink = jobTable.getTableSink();
        final String semantic = jobTableSink.getSemantic();
        final String dimension = jobTable.getDimension();
        final Map<String, List<String>> columnMap = jobTableSource.getColumns();
        final String outTopic = jobSink.getOrDefault(TOPIC, "").toString();
        final String sql = jobDefinition.getSql();
        final String tableName = jobTableSource.getName();

        log.info("semantic:{}", semantic);

        log.info("columnMap:{}", columnMap);

        TableSchemaConfig tableSchemaConfig = new TableSchemaConfig(jobDefinition);

        StreamTableSource<?> tableSource = new MapStreamTableSource(tableSchemaConfig.getTableSchema(),
                tableSchemaConfig.getRowtimeAttributeDescriptors(),
                tableSchemaConfig.getProctimeAttribute(),
                sourceDataStream);

        // select TUMBLE_START(rowtime, interval '1' MINUTE) as `@timestamp`, count(*) as cnt from input GROUP BY TUMBLE(rowtime, interval '1' MINUTE)
        log.info("source tableName:{} sql:{}", tableName, sql);

        tableEnv.registerTableSource(tableName, tableSource);
        Table table = tableEnv.sqlQuery(sql);
        table.printSchema();


        Properties props = MapUtils.toProperties(jobSink);

        /*DataStream<Tuple2<Boolean, Row>> sinkDataStream = tableEnv.toRetractStream(table, Row.class);
        sinkDataStream.print();*/

        DataStream<Row> dataStream = tableEnv.toAppendStream(table, Row.class);
        dataStream.print();

        final String[] fieldNames = table.getSchema().getFieldNames();


        FlinkKafkaProducer<Row> kafkaProducer = new FlinkKafkaProducer<>(outTopic, new KafkaSerializationSchema<Row>() {

            @Override
            public ProducerRecord<byte[], byte[]> serialize(Row element, @Nullable Long timestamp) {

                Map<String, Object> resultMap = new HashMap<>();

                for (int i = 0; i < fieldNames.length; i++) {
                    resultMap.put(fieldNames[i], element.getField(i));
                }

                resultMap.put("unix_ts", Java8DateTimeUtils.toEpochMilli());
                resultMap.put("dimension", dimension);
                resultMap.put("in_topic", inTopicList);
                resultMap.put("out_topic", outTopic);

                byte[] bytes = JsonParse.toJsonBytes(resultMap);
                return new ProducerRecord<>(outTopic, bytes);
            }
        }, props, getSemantic(semantic));

        dataStream.addSink(kafkaProducer).uid(IdHelper.id()).name("Serialization Messages");
    }

    private FlinkKafkaProducer.Semantic getSemantic(String semantic) {
        switch (semantic.toUpperCase()) {
            case "EXACTLY_ONCE":
                return FlinkKafkaProducer.Semantic.EXACTLY_ONCE;
            case "AT_LEAST_ONCE":
                return FlinkKafkaProducer.Semantic.AT_LEAST_ONCE;
            default:
                return FlinkKafkaProducer.Semantic.NONE;
        }
    }
}
