package com.iogogogo.table;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.types.Row;

import java.util.Map;

/**
 * Created by tao.zeng on 2020/6/3.
 */
public class MapToRowMapFunction implements MapFunction<Map<String, Object>, Row>, ResultTypeQueryable<Row> {

    private String[] fieldNames;
    private TypeInformation<Row> types;

    public MapToRowMapFunction(String[] fieldNames, TypeInformation<Row> types) {
        this.fieldNames = fieldNames;
        this.types = types;
    }

    @Override
    public Row map(Map<String, Object> value) {
        Row r = new Row(fieldNames.length);
        for (int i = 0; i < fieldNames.length; i++) {
            r.setField(i, value.get(fieldNames[i]));
        }
        return r;
    }

    @Override
    public TypeInformation<Row> getProducedType() {
        return types;
    }
}
