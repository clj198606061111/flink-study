package com.itclj.transform;

import com.itclj.bean.WaterSensor;
import com.itclj.functions.FilterFunctionImpl;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FilterUDFDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<WaterSensor> sensorDS = env.fromElements(
                new WaterSensor("s1", 1L, 1),
                new WaterSensor("s2", 2L, 2),
                new WaterSensor("s3", 3L, 3)
        );

        //filter 算子
        sensorDS.filter(new FilterFunctionImpl("s1")).print();

        env.execute();
    }
}
