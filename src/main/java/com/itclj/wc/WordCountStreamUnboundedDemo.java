package com.itclj.wc;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * 接收 socket 接收无界流
 *
 * 使用 netcat模拟
 *
 * nc -lp 7777
 */
public class WordCountStreamUnboundedDemo {
    public static void main(String[] args) throws Exception {
        //1. 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //2. 读取数据
        DataStreamSource<String> sockDS = env.socketTextStream("192.168.10.152", 7777);

        //3. 处理数据
        SingleOutputStreamOperator<Tuple2<String, Integer>> sum = sockDS.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
                String[] words = value.split(" ");
                for (String word : words) {
                    Tuple2<String, Integer> wordAndOne = Tuple2.of(word, 1);
                    out.collect(wordAndOne);
                }
            }
        }).keyBy(obj -> obj.f0).sum(1);

        //4. 输出
        sum.print();

        //5. 执行
        env.execute();
    }
}
