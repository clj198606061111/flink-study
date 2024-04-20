package com.itclj.sink;

import com.itclj.bean.WaterSensor;
import com.itclj.functions.WaterSensorMapFunction;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Sink2MysqlDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        SingleOutputStreamOperator<WaterSensor> sensorDS = env
                .socketTextStream("flink01", 7777)
                .map(new WaterSensorMapFunction());

        String sql = "insert into ws(id,ts,vc)values(?,?,?)";
        SinkFunction<WaterSensor> jdbcSink = JdbcSink.sink(sql, new JdbcStatementBuilder<WaterSensor>() {
                    @Override
                    public void accept(PreparedStatement ps, WaterSensor waterSensor) throws SQLException {
                        ps.setString(1, waterSensor.getId());
                        ps.setLong(2, waterSensor.getTs());
                        ps.setInt(3, waterSensor.getVc());
                    }
                },
                JdbcExecutionOptions
                        .builder()
                        .withMaxRetries(3)
                        .withBatchSize(100)
                        .withBatchIntervalMs(3)
                        .build(),
                new JdbcConnectionOptions
                        .JdbcConnectionOptionsBuilder()
                        .withUrl("jdbc:mysql://127.0.0.1:3306/itclj")
                        .withUsername("root")
                        .withPassword("123456")
                        .withConnectionCheckTimeoutSeconds(60)
                        .build()
        );

        sensorDS.addSink(jdbcSink);

        env.execute();
    }
}
