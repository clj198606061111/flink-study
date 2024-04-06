package com.itclj.source;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class KafkaSourceDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1); //并行度

        //从kafka读数据
        KafkaSource kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers("")
                .setGroupId("itclj-flink-kafka-group")
                .setTopics("topic_01")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setStartingOffsets(OffsetsInitializer.latest())
                .build();

        env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "itcljKafkaSource")
                .print();
        env.execute();
    }
}
