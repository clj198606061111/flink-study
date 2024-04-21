package com.itclj.window;

import com.itclj.bean.WaterSensor;
import com.itclj.functions.WaterSensorMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

public class WindowReduceDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<WaterSensor> sensorDS = env
                .socketTextStream("flink01", 7777)
                .map(new WaterSensorMapFunction());

        KeyedStream<WaterSensor, String> sensorKS = sensorDS.keyBy(sensor -> sensor.getId());

        WindowedStream<WaterSensor, String, TimeWindow> sensorWS = sensorKS.window(TumblingProcessingTimeWindows.of(Time.seconds(5)));
        SingleOutputStreamOperator<WaterSensor> reduce = sensorWS.reduce(new ReduceFunction<WaterSensor>() {
            @Override
            public WaterSensor reduce(WaterSensor v1, WaterSensor v2) throws Exception {
                System.out.println("调用Reduce方法，value1=" + v1.getVc() + ",value2=" + v2.getVc());
                return new WaterSensor(v1.getId(), v2.getTs(), v1.getVc() + v2.getVc());
            }
        });

        reduce.print();

        env.execute();
    }
}
