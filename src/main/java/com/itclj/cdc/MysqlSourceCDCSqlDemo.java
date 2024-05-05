package com.itclj.cdc;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class MysqlSourceCDCSqlDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        String createTableSql = "CREATE TABLE tags (\n" +
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

        String selectSql = "select * from tags";

        tableEnv.executeSql(createTableSql);
        Table table = tableEnv.sqlQuery(selectSql);
        DataStream<Row> changelogStream = tableEnv.toChangelogStream(table);
        changelogStream.print();

        env.execute();
    }
}
