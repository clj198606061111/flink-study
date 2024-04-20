package com.itclj.window;

import com.itclj.bean.WaterSensor;
import com.itclj.functions.WaterSensorMapFunction;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

public class WindowApiDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<WaterSensor> sensorDS = env
                .socketTextStream("flink01", 7777)
                .map(new WaterSensorMapFunction());

        KeyedStream<WaterSensor, String> sensorKS = sensorDS.keyBy(sensor -> sensor.getId());

        //1. 指定 窗口分配器： 指定用 哪一种 -- 时间 or 计数 ？ 滚动、滑动 、会话？
        //1.1 没有 keyby 的窗口：窗口内的所有数据 会进入同一个子任务，并行度只能是1
        //sensorDS.windowAll();

        //1.2 有 keyby 的窗口：每个key上都定义了一组窗口，各自独立的进行统计计算

        //基于时间的
        //sensorKS.window(TumblingProcessingTimeWindows.of(Time.seconds(10))); //滚动窗口，窗口长度 10s
        //sensorKS.window(SlidingProcessingTimeWindows.of(Time.seconds(10),Time.seconds(2)));//滚动窗口，窗口长度10s,滚动步长2s
        //sensorKS.window(ProcessingTimeSessionWindows.withGap(Time.seconds(5)));//会话窗口，超时间隔 5s

        //基于计数的
        //sensorKS.countWindow(5);//滚动窗口，窗口长度=5个元素
        //sensorKS.countWindow(5,2);//滑动窗口，窗口长度=5个元素，滑动步长=2个元素
        //sensorKS.window(GlobalWindows.create());//全局窗口，计算窗口的底层就是这个,需要自定义一个触发器

        //2. 指定 窗口函数，指定对窗口内数据的计算逻辑
        WindowedStream<WaterSensor, String, TimeWindow> sensorWS = sensorKS.window(TumblingProcessingTimeWindows.of(Time.seconds(10)));

        //增量聚合：来一条数据，计算一条数据，窗口触发的时候输出计算结果

        //全量聚合：

        env.execute();
    }
}
