package com.iogogogo;

import com.iogogogo.util.Java8DateTimeUtils;
import com.iogogogo.util.JsonParse;
import com.iogogogo.util.TypeMapUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by tao.zeng on 2020/6/4.
 */
@Slf4j
public class JsonTests {


    @Test
    public void test() {
        String json = "{ \"hostname\": \"NODE-01\", \"manager_ip\": \"139.0.0.15\", \"service_ip\": \"182.241.15.101\", \"requesttime\": 20.5, \"unix_ts\": 1591239967135, \"@timestamp\": \"2020-06-04 11:37:59\" }";


        Map<String, Object> map = JsonParse.parse(json, JsonParse.MAP_STR_OBJ_TYPE);

        Map<String, List<TypeMapUtils.TypeMap>> typeMap = TypeMapUtils.getTypeMap(map);

        System.out.println(map);
        System.out.println(typeMap);

        System.out.println(JsonParse.toJson(new X(Java8DateTimeUtils.nowDateTime())));
    }


    @Data
    @AllArgsConstructor
    private final static class X {
        LocalDateTime ts;
    }
}
