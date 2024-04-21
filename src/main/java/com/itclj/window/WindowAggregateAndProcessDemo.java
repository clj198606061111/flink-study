package com.itclj.window;

import com.itclj.bean.WaterSensor;
import com.itclj.functions.WaterSensorMapFunction;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

public class WindowAggregateAndProcessDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<WaterSensor> sensorDS = env
                .socketTextStream("flink01", 7777)
                .map(new WaterSensorMapFunction());

        KeyedStream<WaterSensor, String> sensorKS = sensorDS.keyBy(sensor -> sensor.getId());

        //1. 窗口分配器
        WindowedStream<WaterSensor, String, TimeWindow> sensorWS = sensorKS.window(TumblingProcessingTimeWindows.of(Time.seconds(5)));

        //2. 窗口函数：
        /**
         * 增量聚合 aggregate+全窗口的process
         * 1. 增量聚合函数处理数据，来一条计算一条
         * 2. 窗口触发时，增量聚合结果（只有一条） 传递给全窗口函数
         * 3. 经过全窗口函数处理包装后，输出
         *
         * 集合2者的有点
         * 1. 增量聚合： 来一条计算一条，存储中间的计算结果，占用空间少
         * 2. 全窗口函数：可以通过上下文，实现灵魂的功能
         */
        SingleOutputStreamOperator<String> aggregate = sensorWS.aggregate(new MyAgg(), new MyProcess());

        aggregate.print();

        env.execute();
    }
}
