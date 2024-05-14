package com.itclj.sql;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class KafkaSqlSourceDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        String createTableSql =
                "CREATE TABLE kafka_tags_source (" +
                        "     id INT,\n" +
                        "     tags STRING\n" +
                        ") WITH (" +
                        "   'connector' = 'kafka'," +
                        "   'topic' = 'flink_cdc_topic_tags_sink'," +
                        "   'properties.bootstrap.servers' = '192.168.0.105:9092'," +
                        "   'properties.group.id' = 'flink-cdc-kafka-group-itclj'," +
                        "   'scan.startup.mode' = 'earliest-offset', " +
                        "   'format' = 'json'" +
                        ")";

        String selectSql = "select * from kafka_tags_source";

        tableEnv.executeSql(createTableSql);
        Table table = tableEnv.sqlQuery(selectSql);
        DataStream<Row> changelogStream = tableEnv.toChangelogStream(table);
        changelogStream.print();

        env.execute();
    }
}
