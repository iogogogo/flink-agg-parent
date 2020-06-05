package com.iogogogo.table;

import lombok.AllArgsConstructor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.sources.DefinedProctimeAttribute;
import org.apache.flink.table.sources.DefinedRowtimeAttributes;
import org.apache.flink.table.sources.RowtimeAttributeDescriptor;
import org.apache.flink.table.sources.StreamTableSource;
import org.apache.flink.table.types.DataType;
import org.apache.flink.types.Row;

import java.util.List;
import java.util.Map;

/**
 * Created by tao.zeng on 2020/6/3.
 */
@AllArgsConstructor
public class MapStreamTableSource implements StreamTableSource<Row>, DefinedProctimeAttribute, DefinedRowtimeAttributes {

    private TableSchema tableSchema;

    private List<RowtimeAttributeDescriptor> rowtimeAttributeDescriptors;

    private String proctimeAttribute;

    private DataStream<Map<String, Object>> mapDataStream;


    @Override
    public DataType getProducedDataType() {
        return tableSchema.toRowDataType();
    }

    @Override
    public TypeInformation<Row> getReturnType() {
        return tableSchema.toRowType();
    }

    @Override
    public TableSchema getTableSchema() {
        return tableSchema;
    }

    @Override
    public List<RowtimeAttributeDescriptor> getRowtimeAttributeDescriptors() {
        return rowtimeAttributeDescriptors;
    }

    @Override
    public String getProctimeAttribute() {
        return proctimeAttribute;
    }

    @Override
    public DataStream<Row> getDataStream(StreamExecutionEnvironment execEnv) {
        return mapDataStream.map(new MapToRowMapFunction(tableSchema.getFieldNames(), tableSchema.toRowType()));
    }
}
