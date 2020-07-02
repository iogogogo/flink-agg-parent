package com.iogogogo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iogogogo.util.JsonParse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDate date = LocalDate.now();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new JsonParse.LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new JsonParse.LocalDateTimeAdapter())
                .create();

        String json = gson.toJson(dateTime);

        log.info("dateTime Serialization:{}", json);
        log.info("dateTime Deserialization:{}", gson.fromJson(json, LocalDateTime.class));

        System.out.println();

        json = gson.toJson(date);
        log.info("date Serialization:{}", json);
        log.info("date Deserialization:{}", gson.fromJson(json, LocalDate.class));
    }


    @Data
    @AllArgsConstructor
    private final static class X {
        LocalDateTime ts;
    }
}
