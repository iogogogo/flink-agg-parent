package com.iogogogo;

import com.google.gson.Gson;
import com.iogogogo.util.JsonParse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Created by tao.zeng on 2020/6/4.
 */
@Slf4j
public class JsonTests {


    @Test
    public void test() {
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDate date = LocalDate.now();

        Gson gson = new Gson();

        String json = gson.toJson(dateTime);

        log.info("dateTime Serialization:{}", json);
        log.info("dateTime Deserialization:{}", gson.fromJson(json, LocalDateTime.class));

        System.out.println();

        json = gson.toJson(date);
        log.info("date Serialization:{}", json);
        log.info("date Deserialization:{}", gson.fromJson(json, LocalDate.class));
    }

    @Test
    public void test1() {

        String text = "{\"hostname\":\"ASCMASS03\",\"service_ip\":\"182.251.130.32\",\"manager_ip\":\"139.99.152.47\",\"requesttime\":70.37053,\"unix_ts\":1594544385516,\"@timestamp\":\"2020-07-12T16:59:45.516\"}";

        Map<String, Object> map = JsonParse.parse(text, JsonParse.MAP_STR_OBJ_TYPE);

        System.out.println(JsonParse.toJson(map));
        String json = new String(JsonParse.toJsonBytes(map), StandardCharsets.UTF_8);
        System.out.println(json);
    }


    @Data
    @AllArgsConstructor
    private final static class X {
        LocalDateTime ts;
    }
}
