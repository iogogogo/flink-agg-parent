package com.iogogogo.context;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.java.StreamTableEnvironment;

/**
 * Created by tao.zeng on 2020/6/3.
 */
public class FlinkEnvironment {


    public static Tuple2<StreamExecutionEnvironment, StreamTableEnvironment> context() {


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, settings);

        return Tuple2.of(env, tableEnv);
    }

}
