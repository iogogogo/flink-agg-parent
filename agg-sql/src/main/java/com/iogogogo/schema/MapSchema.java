package com.iogogogo.schema;

import com.iogogogo.definition.JobDefinition;
import com.iogogogo.util.Java8DateTimeUtils;
import com.iogogogo.util.JsonParse;
import com.iogogogo.util.MathUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;


/**
 * Created by tao.zeng on 2020/6/3.
 */
@Slf4j
public class MapSchema implements DeserializationSchema<Map<String, Object>>, SerializationSchema<Map<String, Object>> {

    private JobDefinition jobDefinition;

    public MapSchema(JobDefinition jobDefinition) {
        this.jobDefinition = jobDefinition;
    }

    @Override
    public Map<String, Object> deserialize(byte[] message) {

        final JobDefinition.JobDefinitionTable.JobTableSource tableSource = jobDefinition.getJobTable().getTableSource();
        final JobDefinition.JobDefinitionTable.JobDefRowtime sourceRowtime = tableSource.getRowtime();
        final JobDefinition.JobDefinitionTable.JobDefTsTransfer tsTransfer = sourceRowtime.getTsTransfer();
        final JobDefinition.JobDefinitionTable.JobDefNumberToLong numberToLong = sourceRowtime.getNumberToLong();

        boolean debug = sourceRowtime.isDebugLog();

        String msg = new String(message, StandardCharsets.UTF_8);

        Map<String, Object> row = JsonParse.parse(msg, JsonParse.MAP_STR_OBJ_TYPE);

        if (tsTransfer.isEnable()) {
            String srcValue = row.getOrDefault(tsTransfer.getSrc(), "").toString();
            String targetKey = tsTransfer.getTarget();
            String tsFormat = tsTransfer.getTsFormat();

            if (debug) log.info("tsTransfer srcValue:{} targetKey:{} format:{}", srcValue, targetKey, tsFormat);

            if (StringUtils.isNotEmpty(srcValue)) {
                LocalDateTime dateTime = Java8DateTimeUtils.parseJava8(srcValue, tsFormat);
                long epochMilli = Java8DateTimeUtils.toEpochMilli(dateTime);

                if (debug) log.info("tsTransfer dateTime:{} epochMilli:{}", dateTime, epochMilli);

                row.put(targetKey, epochMilli);
            }
        }

        if (numberToLong.isEnable()) {
            String src = numberToLong.getSrc();
            String target = numberToLong.getTarget();

            String srcValue = row.getOrDefault(src, "").toString();
            Long targetValue = MathUtils.tryLong(srcValue);
            if (debug) log.info("numberToLong {}:{}  {}:{}", src, srcValue, target, targetValue);
            if (Objects.nonNull(targetValue)) {
                row.put(target, targetValue);
            }
        }

        return row;
    }

    @Override
    public boolean isEndOfStream(Map<String, Object> nextElement) {
        return false;
    }

    @Override
    public byte[] serialize(Map<String, Object> element) {
        return JsonParse.toJsonBytes(element);
    }

    @Override
    public TypeInformation<Map<String, Object>> getProducedType() {
        return TypeInformation.of(new TypeHint<Map<String, Object>>() {
        });
    }
}
