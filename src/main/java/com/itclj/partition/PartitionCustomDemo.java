package com.itclj.partition;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class PartitionCustomDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);//设置并行度
        DataStreamSource<String> socketDS = env.socketTextStream("192.168.10.161", 7777);
        socketDS.partitionCustom(new ItcljPartitioner(), r -> r);


        env.execute();
    }
}
