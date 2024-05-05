package com.itclj.cdc;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class MysqlSourceCDCCheckPointDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        //1. 启用checkpointing ,默认是barrier对象，,间隔为5000毫米,检查模式为Exactly once
        env.enableCheckpointing(5000, CheckpointingMode.EXACTLY_ONCE);
        //状态后端可以不用在代码里面直接设置，一般用flink集群开启默认状态后端，直接用集群默认的状态后端就可以了
        //env.setStateBackend(new FsStateBackend("file:///D:/bigdata/flink/data/state-backend"));
        CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        //2. 设置检查点存储位置，
        //检查点位置也不用在代码里面直接设置，集群设置默认检查点保存位置比较好，或者在退吹的时候在命令行设置检查点位置，在程序代码里面设置检查点位置是一种不优化的动作
        //checkpointConfig.setCheckpointStorage("file:///D:/bigdata/flink/data/checkpoint");
        //3. checkpoint的超时时间：默认10分钟
        checkpointConfig.setCheckpointTimeout(60000);//设置60秒
        //4. 同时运行中的checkpoint的最大数量
        checkpointConfig.setMaxConcurrentCheckpoints(1);
        //5. 最小等待间隔：上一轮checkpoint结束 到下一轮checkpoint开始之间的间隔，设置了 > 0,并发会变成1
        checkpointConfig.setMinPauseBetweenCheckpoints(1000);
        //6. 取消作业时，checkpoint的数据 是否 保留在外边系统
        checkpointConfig.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        //7. 允许 checkpoint 连续失败的次数，默认0，表示，表示checkpointy一次失败，job就挂掉
        checkpointConfig.setTolerableCheckpointFailureNumber(0);

        MySqlSource<String> source = MySqlSource.<String>builder()
                .hostname("192.168.0.105")
                .port(3306)
                .username("itclj")
                .password("itclj123456")
                .databaseList("itclj")
                .tableList("itclj.tags")
                .deserializer(new StringDebeziumDeserializationSchema())
                .startupOptions(StartupOptions.initial())
                .build();

        DataStreamSource<String> outDataStreamSource = env.fromSource(source, WatermarkStrategy.noWatermarks(), "mysql-cdc");
        outDataStreamSource.print();

        env.execute();
    }
}
