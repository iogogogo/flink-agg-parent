package com.iogogogo;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import com.iogogogo.definition.JobDefinition;
import com.iogogogo.util.Java8DateTimeUtils;
import com.iogogogo.util.JobDefUtils;
import com.iogogogo.util.JsonParse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static com.iogogogo.consts.BaseConsts.TOPICS;

/**
 * Created by tao.zeng on 2020/6/3.
 */
@Slf4j
public class Tests {

    private JobDefinition jobDefinition;

    private final static List<Device> DEVICE_LIST = new ArrayList<>();

    static {
        DEVICE_LIST.add(new Device("ASCMASS01", "139.99.152.49", "182.251.59.86", "ASCMASS01"));
        DEVICE_LIST.add(new Device("ASCMASS02", "139.99.152.48", "182.251.130.33", "ASCMASS02"));
        DEVICE_LIST.add(new Device("ASCMASS03", "139.99.152.47", "182.251.130.32", "ASCMASS03"));
        DEVICE_LIST.add(new Device("ASCMASS04", "139.99.152.46", "182.251.130.31", "ASCMASS04"));
        DEVICE_LIST.add(new Device("ASCMASS05", "139.99.152.45", "182.251.130.30", "ASCMASS05"));
        DEVICE_LIST.add(new Device("RSSCMOBS06", "139.101.37.46", "182.252.197.11", "RSSCMOBS06"));
    }

    @Before
    public void before() throws IOException {
        jobDefinition = JobDefUtils.getJobDefinition(null);
    }

    @Test
    public void producer() throws InterruptedException {
        Map<String, Object> jobDefinitionJobSource = jobDefinition.getJobSource();
        Properties props = MapUtils.toProperties(jobDefinitionJobSource);

        @SuppressWarnings("unchecked") final List<String> topicList = (List<String>) jobDefinitionJobSource.getOrDefault(TOPICS, Lists.newArrayList());
        final String topic = topicList.get(0);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(props);

        Random random = new Random();

        while (true) {
            DEVICE_LIST.forEach(x -> {
                Data data = new Data();
                data.setHostname(x.getHostname());
                data.setServiceIp(x.getServiceip());
                data.setManagerIp(x.getManagerip());
                data.setRequesttime(random.nextFloat() * 100);
                data.setUnixTs(Java8DateTimeUtils.toEpochMilli());
                data.setTimestamp(Java8DateTimeUtils.nowDateTime());

                String message = JsonParse.toJson(data);
                kafkaProducer.send(new ProducerRecord<>(topic, message), (mate, e) -> {
                    log.info("offset:{} message:{}", mate.offset(), message);
                });
                kafkaProducer.flush();
            });
        }
    }


    @Test
    @Ignore
    public void consumer() {
        Properties props = MapUtils.toProperties(jobDefinition.getJobSource());
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Device {
        private String hostname;
        private String managerip;
        private String serviceip;
        private String object;
    }

    @lombok.Data
    private final static class Data {
        private String hostname;
        @SerializedName("service_ip")
        private String serviceIp;
        @SerializedName("manager_ip")
        private String managerIp;
        private Float requesttime;
        @SerializedName("unix_ts")
        private Long unixTs;
        @SerializedName("@timestamp")
        private LocalDateTime timestamp;
    }
}
