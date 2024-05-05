package com.itclj.cdc;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class MysqlCDC2KafkaSqlDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        String sourceSql = "CREATE TABLE tags (\n" +
                "     id INT,\n" +
                "     tags STRING,\n" +
                "     PRIMARY KEY(id) NOT ENFORCED\n" +
                "     ) WITH (\n" +
                "     'connector' = 'mysql-cdc',\n" +
                "\t 'scan.startup.mode' = 'initial',\n" +
                "     'hostname' = '192.168.0.105',\n" +
                "     'port' = '3306',\n" +
                "     'username' = 'itclj',\n" +
                "     'password' = 'itclj123456',\n" +
                "     'database-name' = 'itclj',\n" +
                "     'table-name' = 'tags')";


        // 配置Kafka Sink
        String sinkDDL = "" +
                "CREATE TABLE kafka_tags_sink (" +
                "     id INT,\n" +
                "     tags STRING,\n" +
                "     PRIMARY KEY(id) NOT ENFORCED\n" +
                ") WITH (" +
                //connector:upsert-kafka
                "   'connector' = 'upsert-kafka'," +
                "   'topic' = 'flink_cdc_topic_tags_sink'," +
                "   'properties.bootstrap.servers' = '192.168.0.105:9092'," +
                "   'properties.group.id' = 'flink-cdc-kafka-group-itclj',"+
                "   'key.format' = 'json'," +
                "   'value.format' = 'json'"+
                ")";

        // 将MySQL的数据写入Kafka
        String insertSql="INSERT INTO kafka_tags_sink SELECT * FROM tags";

        tableEnv.executeSql(sourceSql);
        tableEnv.executeSql(sinkDDL);
        tableEnv.executeSql(insertSql);


        env.execute("Flink CDC mysql to kafka demo");
    }
}
