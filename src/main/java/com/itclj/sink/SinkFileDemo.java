package com.itclj.sink;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.connector.datagen.source.GeneratorFunction;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.OutputFileConfig;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.DateTimeBucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;

import java.time.Duration;

public class SinkFileDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);//设置并行度
        //必须开启 checkpoint,否则文件一直都是 .inprogress
        env.enableCheckpointing(2000, CheckpointingMode.EXACTLY_ONCE);

        DataGeneratorSource<String> dataGeneratorSource = new DataGeneratorSource<>(new GeneratorFunction<Long, String>() {
            @Override
            public String map(Long aLong) throws Exception {
                return "num-" + aLong;
            }
        }, Integer.MAX_VALUE,
                RateLimiterStrategy.perSecond(100),
                Types.STRING);

        DataStreamSource<String> dataGen = env
                .fromSource(dataGeneratorSource, WatermarkStrategy.noWatermarks(), "data-generator");

        //输出到文件系统
        FileSink<String> fileSink = FileSink.<String>forRowFormat(new Path("d:/itclj"),
                        new SimpleStringEncoder<>("UTF-8"))
                .withOutputFileConfig(OutputFileConfig.builder()
                        .withPartPrefix("itclj-flink-file-")
                        .withPartSuffix(".txt")
                        .build())
                //文件分桶
                .withBucketAssigner(new DateTimeBucketAssigner<>())
                //滚动策略
                .withRollingPolicy(
                        DefaultRollingPolicy.builder()
                                .withRolloverInterval(Duration.ofSeconds(10))
                                .withMaxPartSize(1024 * 1024)
                                .build()
                ).build();
        dataGen.sinkTo(fileSink);


        env.execute();
    }
}
