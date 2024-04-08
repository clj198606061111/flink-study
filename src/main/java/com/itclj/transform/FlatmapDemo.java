package com.itclj.transform;

import com.itclj.bean.WaterSensor;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class FlatmapDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<WaterSensor> sensorDS = env.fromElements(
                new WaterSensor("s1", 1L, 1),
                new WaterSensor("s2", 2L, 2),
                new WaterSensor("s3", 3L, 3)
        );

        //flatmap 算子，一进多出
        SingleOutputStreamOperator<Integer> flatmap = sensorDS.flatMap(new FlatMapFunction<WaterSensor, Integer>() {

            @Override
            public void flatMap(WaterSensor i, Collector<Integer> o) throws Exception {
                if ("s1".equals(i.getId())) {
                    o.collect(i.getVc());
                } else if ("s2".equals(i.getId())) {
                    o.collect(i.getVc());
                    o.collect(33);
                }
            }
        });

        flatmap.print();

        env.execute();
    }
}
