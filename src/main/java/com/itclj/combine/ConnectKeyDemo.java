package com.itclj.combine;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectKeyDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);//设置并行度

        DataStreamSource<Tuple2<Integer, String>> source1 = env.fromElements(
                Tuple2.of(1, "a1"),
                Tuple2.of(1, "a2"),
                Tuple2.of(2, "b"),
                Tuple2.of(3, "c")
        );
        DataStreamSource<Tuple3<Integer, String, Integer>> source2 = env.fromElements(
                Tuple3.of(1, "aa1", 1),
                Tuple3.of(1, "aa2", 2),
                Tuple3.of(2, "bb", 1),
                Tuple3.of(3, "cc", 1)
        );

        ConnectedStreams<Tuple2<Integer, String>, Tuple3<Integer, String, Integer>> connect = source1.connect(source2);



        /**
         * 实现互相匹配的效果
         * 1. 两条流，不一定谁的数据先来
         * 2. 每条流，有数据来，存到一个变量中
         * 3. 每条流有数据来的时候，除了存变量中，不知道对方是否有匹配的数据，要求另一条流存的变量中 查找是否有匹配上的。
         */
        SingleOutputStreamOperator<String> process = connect.process(new CoProcessFunction<Tuple2<Integer, String>, Tuple3<Integer, String, Integer>, String>() {
            Map<Integer, List<Tuple2<Integer, String>>> s1Cache = new HashMap<>();
            Map<Integer, List<Tuple3<Integer, String, Integer>>> s2Cache = new HashMap<>();

            /**
             * 第一条流的处理逻辑
             * @param va 输入参数
             * @param context 上下文
             * @param out 采集器
             * @throws Exception
             */
            @Override
            public void processElement1(Tuple2<Integer, String> va, CoProcessFunction<Tuple2<Integer, String>, Tuple3<Integer, String, Integer>, String>.Context context, Collector<String> out) throws Exception {
                //1. s1的数据来了，就存在变量中
                Integer id = va.f0;
                if (!s1Cache.containsKey(id)) {
                    List<Tuple2<Integer, String>> s1Value = new ArrayList<>();
                    s1Value.add(va);
                    s1Cache.put(id, s1Value);
                } else {
                    s1Cache.get(id).add(va);
                }
                //2. 去 s2Cache中查找是否已经有id能匹配上的
                if (s2Cache.containsKey(id)) {
                    for (Tuple3<Integer, String, Integer> s2e : s2Cache.get(id)) {
                        out.collect("s1:" + va + "---------------" + "s2:" + s2e);
                    }
                }
            }

            /**
             * 第二条流的处理逻辑
             * @param va
             * @param context
             * @param out
             * @throws Exception
             */
            @Override
            public void processElement2(Tuple3<Integer, String, Integer> va, CoProcessFunction<Tuple2<Integer, String>, Tuple3<Integer, String, Integer>, String>.Context context, Collector<String> out) throws Exception {
                //1. s2的数据来了，就存在变量中
                Integer id = va.f0;
                if (!s2Cache.containsKey(id)) {
                    List<Tuple3<Integer, String, Integer>> s2Value = new ArrayList<>();
                    s2Value.add(va);
                    s2Cache.put(id, s2Value);
                } else {
                    s2Cache.get(id).add(va);
                }
                //2. 去 s2Cache中查找是否已经有id能匹配上的
                if (s1Cache.containsKey(id)) {
                    for (Tuple2<Integer, String> s1e : s1Cache.get(id)) {
                        out.collect("s2:" + va + "---------------" + "s1:" + s1e);
                    }
                }
            }
        });

        process.print();

        env.execute();
    }
}
