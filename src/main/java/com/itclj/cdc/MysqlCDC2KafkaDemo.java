package com.itclj.cdc;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.kafka.shaded.org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 参考文档：
 * https://blog.csdn.net/WB231444/article/details/135689317
 */
public class MysqlCDC2KafkaDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        MySqlSource<String> source = MySqlSource.<String>builder()
                .hostname("192.168.0.105")
                .port(3306)
                .username("itclj")
                .password("itclj123456")
                .databaseList("itclj")
                .tableList("itclj.tags")
                .deserializer(new JsonDebeziumDeserializationSchema())
                .startupOptions(StartupOptions.initial())
                .build();

        DataStreamSource<String> outDataStreamSource = env.fromSource(source, WatermarkStrategy.noWatermarks(), "mysql-cdc");
        //3.数据同步到kafka
        KafkaSink<String> kafkaSink = KafkaSink.<String>builder()
                //指定Kafka的连接地址
                .setBootstrapServers("192.168.0.105:9092")
                //指定序列化器
                .setRecordSerializer(
                        KafkaRecordSerializationSchema.<String>builder()
                                .setTopic("flink_cdc_itclj_tags")
                                .setValueSerializationSchema(new SimpleStringSchema())
                                .build()
                )
                //写入kafka的一致性级别
                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                //如果是精确一次，必须设置事务的前缀
                .setTransactionalIdPrefix("itclj-")
                //如果是精确一次必须设置事务超时时间
                .setProperty(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, "300000")
                .build();

        outDataStreamSource.sinkTo(kafkaSink);

        env.execute();
    }
}
