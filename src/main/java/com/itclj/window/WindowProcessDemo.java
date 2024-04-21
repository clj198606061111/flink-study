package com.itclj.window;

import com.itclj.bean.WaterSensor;
import com.itclj.functions.WaterSensorMapFunction;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class WindowProcessDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<WaterSensor> sensorDS = env
                .socketTextStream("flink01", 7777)
                .map(new WaterSensorMapFunction());

        KeyedStream<WaterSensor, String> sensorKS = sensorDS.keyBy(sensor -> sensor.getId());

        //1. 窗口分配器
        WindowedStream<WaterSensor, String, TimeWindow> sensorWS = sensorKS.window(TumblingProcessingTimeWindows.of(Time.seconds(10)));

        // 旧写法
//        sensorWS.apply(new WindowFunction<WaterSensor, String, String, TimeWindow>() {
//            @Override
//            public void apply(String s, TimeWindow timeWindow, Iterable<WaterSensor> iterable, Collector<String> collector) throws Exception {
//
//            }
//        })

        /**
         * 全窗口函数，窗口触发时才触发一次，统一计算
         */
        SingleOutputStreamOperator<String> process = sensorWS.process(new ProcessWindowFunction<WaterSensor, String, String, TimeWindow>() {
            /**
             *
             * @param key 分组的key
             * @param context 上下文
             * @param iterable 存储的数据
             * @param out 采集器
             * @throws Exception
             */
            @Override
            public void process(String key, ProcessWindowFunction<WaterSensor, String, String, TimeWindow>.Context context, Iterable<WaterSensor> iterable, Collector<String> out) throws Exception {
                long startTs = context.window().getStart();
                long endTs = context.window().getEnd();
                String windowStart = DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(startTs);
                String windowEnd = DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(endTs);

                long count = iterable.spliterator().estimateSize();
                out.collect("key=" + key + "的窗口[" + windowStart + "," + windowEnd + "]包含" + count + "条数据===>" + iterable.toString());
            }
        });

        process.print();

        env.execute();
    }
}
