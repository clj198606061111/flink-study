package com.itclj.source;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * kafka部署和使用命令
 * https://note.youdao.com/web/#/file/E50C4FCCEC434C66B6EACB224A57383D/markdown/3AFE19EFCD74471BB00A295408766517/
 */
public class KafkaSourceDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1); //并行度

        //从kafka读数据
        KafkaSource kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers("192.168.0.105:9092")
                .setGroupId("itclj-flink-kafka-group")
                .setTopics("itcljtest")
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setStartingOffsets(OffsetsInitializer.latest())
                .build();

        env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "itcljKafkaSource")
                .print();
        env.execute();
    }
}
