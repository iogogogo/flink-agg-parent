package com.iogogogo.schema.sink;


import com.iogogogo.context.FlinkKafkaPartitionType;
import com.iogogogo.util.JsonParse;
import org.apache.flink.streaming.connectors.kafka.KafkaContextAware;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by tao.zeng on 2020/7/1.
 */
public class MapKafkaSerializationSchema implements KafkaSerializationSchema<Map<String, Object>>, KafkaContextAware<Map<String, Object>> {

    FlinkKafkaPartitionType partitionType;
    int numParallelInstances;
    int parallelInstanceId;
    int[] partitions;
    String keyField;
    String topic;
    boolean writeTimestampToKafka;

    public MapKafkaSerializationSchema(String topic) {
        this(topic, FlinkKafkaPartitionType.FIXED, null);
    }

    public MapKafkaSerializationSchema(String topic, FlinkKafkaPartitionType partitionType) {
        this(topic, partitionType, null);
    }

    public MapKafkaSerializationSchema(String topic, FlinkKafkaPartitionType partitionType, String keyField) {
        this.partitionType = partitionType;
        this.keyField = keyField;
        this.topic = topic;
    }

    public MapKafkaSerializationSchema(String topic, FlinkKafkaPartitionType partitionType, boolean writeTimestampToKafka) {
        this.partitionType = partitionType;
        this.topic = topic;
        this.writeTimestampToKafka = writeTimestampToKafka;
    }

    @Override
    public ProducerRecord<byte[], byte[]> serialize(Map<String, Object> element, @Nullable Long timestamp) {
        ProducerRecord<byte[], byte[]> producerRecord;
        if (!writeTimestampToKafka) {
            // 设置写入kafka record的timestamp为null，则kafka会自动设置当前时间为record的timestamp
            // 避免flink把数据event time的时间作为kafka记录时间写入，否则可能会造成数据很快过期被自动清理掉
            // 参考：https://ci.apache.org/projects/flink/flink-docs-release-1.9/dev/connectors/kafka.html#using-kafka-timestamps-and-flink-event-time-in-kafka-010
            timestamp = null;
        }
        try {
            if (partitionType == FlinkKafkaPartitionType.FIXED) {
                int pid = partitions[parallelInstanceId % partitions.length];
                producerRecord = new ProducerRecord<>(topic, pid, timestamp, null, JsonParse.toJsonBytes(element));
            } else if (partitionType == FlinkKafkaPartitionType.ROUND_ROBIN) {
                producerRecord = new ProducerRecord<>(topic, null, timestamp, null, JsonParse.toJsonBytes(element));
            } else {
                byte[] key = null;
                if (element.get(keyField) != null) {
                    key = element.get(keyField).toString().getBytes();
                }
                producerRecord = new ProducerRecord<>(topic, null, timestamp, key, JsonParse.toJsonBytes(element));
            }
        } catch (Exception ex) {
            producerRecord = new ProducerRecord<>(topic, null, timestamp, null, new byte[0]);
        }
        return producerRecord;
    }

    @Override
    public String getTargetTopic(Map<String, Object> element) {
        return null;
    }

    @Override
    public void setNumParallelInstances(int numParallelInstances) {
        this.numParallelInstances = numParallelInstances;
    }

    @Override
    public void setParallelInstanceId(int parallelInstanceId) {
        this.parallelInstanceId = parallelInstanceId;
    }

    @Override
    public void setPartitions(int[] partitions) {
        this.partitions = partitions;
    }
}
