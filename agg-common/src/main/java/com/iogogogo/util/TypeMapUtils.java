package com.iogogogo.util;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by tao.zeng on 2020/6/5.
 */
@Slf4j
public class TypeMapUtils {

    public static Map<String, List<TypeMap>> getTypeMap(Map<String, Object> map) {
        if (MapUtils.isEmpty(map)) return Maps.newHashMap();

        List<TypeMapUtils.TypeMap> typeMaps = new ArrayList<>();
        map.forEach((k, v) -> {
            Class<?> clz = v.getClass();

            String typeName = clz.getTypeName();
            String simpleName = clz.getSimpleName();

            typeMaps.add(new TypeMapUtils.TypeMap(k, simpleName, typeName));

            log.debug("field:{} typeName:{} simpleName:{}", k, typeName, simpleName);
        });
        return typeMaps.stream().collect(Collectors.groupingBy(TypeMap::getSimpleName));
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public final static class TypeMap implements Serializable {

        private String field;

        private String simpleName;

        private String typeName;
    }
}
