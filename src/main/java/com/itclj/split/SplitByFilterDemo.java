package com.itclj.split;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 分流
 * 奇数、偶数 分开，分成不同流
 */
public class SplitByFilterDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);//设置并行度
        DataStreamSource<String> socketDS = env.socketTextStream("192.168.10.161", 7777);

        /**
         * 使用filter进行分流性能不好
         */
        socketDS.filter(v -> Integer.parseInt(v) % 2 == 0).print("偶数流");
        socketDS.filter(v -> Integer.parseInt(v) % 2 == 1).print("奇数流");

        env.execute();
    }
}
