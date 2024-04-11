package com.itclj.partition;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class PartitionDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        DataStreamSource<String> sockDS = env.socketTextStream("192.168.10.161", 7777);

        //shuffle 随机分区：random.nextInt (下游算子并行度)
        //sockDS.shuffle().print();

        //rebalance 轮训：
        sockDS.rebalance().print();


        env.execute();
    }
}
