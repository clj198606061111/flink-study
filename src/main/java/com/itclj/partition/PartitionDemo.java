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
        //如果是数据源倾斜的场景，source读进来后，调用rebalance,就可以解决数据源的数据倾斜
        //sockDS.rebalance().print();

        //rescale缩放：实现轮训，局部组队，比rebalance更高效
        //sockDS.rescale().print();

        //broadcast 广播，发送给下游所有子任务
        sockDS.broadcast().print();

        //global 全局：全部发往第一个任务。
        sockDS.global().print();


        env.execute();
    }
}
