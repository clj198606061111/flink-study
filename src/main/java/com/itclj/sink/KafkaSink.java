package com.itclj.sink;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class KafkaSink {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env=StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        //env.socketTextStream("flink01",7777).map(new WaterSensorMapFunction());

        env.execute();
    }
}
