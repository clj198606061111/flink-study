package com.itclj.sink;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.producer.ProducerConfig;

public class KafkaSinkDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        //如果是 精准一次，必须开启 checkpoint，否则不能 写入kafka
        env.enableCheckpointing(2000, CheckpointingMode.EXACTLY_ONCE);

        SingleOutputStreamOperator<String> sensorDS = env.socketTextStream("flink01", 7777);

        //KafkaSink
        /**
         * kafka sink
         * 注意： 如果要使用 精准一次 写入kafka，需要满足以下条件，缺一不可
         * 1. 开启 checkpoint
         * 2. 设置事务前缀
         * 3. 设置事务超时时间： checkpoint 间隔 < 事务超时时间 < max的15分钟
         */
        KafkaSink<String> kafkaSink = KafkaSink.<String>builder()
                //指定 kafka 的地址和端口
                .setBootstrapServers("localhost:9092")
                // 指定序列化器：指定 topic 名称，具体的序列化
                .setRecordSerializer(
                        KafkaRecordSerializationSchema
                                .<String>builder()
                                .setTopic("itcljtest")
                                .setValueSerializationSchema(new SimpleStringSchema())
                                .build())
                // 写到 kafka 的一致性级别，精准一次，至少一次
                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                // 如果是精准一次，必须设置事务前缀
                .setTransactionalIdPrefix("itclj-")
                // 如果是精准一次，必须设置事务超时时间，大于 checkpoint 间隔，小于 max 15 分钟
                .setProperty(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 10 * 60 * 1000 + "")
                .build();
        sensorDS.sinkTo(kafkaSink);

        env.execute();
    }
}
