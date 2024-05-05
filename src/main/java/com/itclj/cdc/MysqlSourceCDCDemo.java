package com.itclj.cdc;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class MysqlSourceCDCDemo {
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
                .deserializer(new StringDebeziumDeserializationSchema())
                .startupOptions(StartupOptions.initial())
                .build();

        DataStreamSource<String> outDataStreamSource = env.fromSource(source, WatermarkStrategy.noWatermarks(), "mysql-cdc");
        outDataStreamSource.print();

        env.execute();
    }
}
