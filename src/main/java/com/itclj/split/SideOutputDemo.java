package com.itclj.split;

import com.itclj.bean.WaterSensor;
import com.itclj.functions.WaterSensorMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SideOutputDataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

/**
 * 侧输出流
 */
public class SideOutputDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);//设置并行度
        DataStreamSource<String> socketDS = env.socketTextStream("192.168.10.161", 7777);

        SingleOutputStreamOperator<WaterSensor> sensorDS = socketDS.map(new WaterSensorMapFunction());

        /**
         * 使用侧输出流进行分流
         * 需求：watersensor 的数据，s1,s2的数据分别分开
         */

        /**
         * 创建OutputTag对象
         * 第一个参数：标签名称
         * 第二个参数：放入侧输出流中的娥数据类型，TypeInfomation
         */
        OutputTag<WaterSensor> s1Tag = new OutputTag<>("s1", Types.POJO(WaterSensor.class));
        OutputTag<WaterSensor> s2Tag = new OutputTag<>("s2", Types.POJO(WaterSensor.class));
        SingleOutputStreamOperator<WaterSensor> process = sensorDS.process(new ProcessFunction<WaterSensor, WaterSensor>() {
            @Override
            public void processElement(WaterSensor waterSensor, ProcessFunction<WaterSensor, WaterSensor>.Context ctx, Collector<WaterSensor> collector) throws Exception {
                String id = waterSensor.getId();
                if ("s1".equals(id)) {
                    //如果是s1,放入侧输出流s1中
                    /**
                     * 上下文调用output，将数据放入侧输出流
                     * 第一个参数：Tag对象
                     * 第二个参数：放入侧输出流中的数据
                     */
                    ctx.output(s1Tag, waterSensor);
                } else if ("s2".equals(id)) {
                    //如果是s2，放入侧输出流s2中
                    ctx.output(s2Tag, waterSensor);
                } else {
                    //非s1,s2，放入原输出流
                    collector.collect(waterSensor);
                }
            }
        });

        //打印主流
        process.print("主输出流 -> ");

        //打印侧输出流s1
        SideOutputDataStream<WaterSensor> s1 = process.getSideOutput(s1Tag);
        s1.print("侧输出流s1 -> ");

        //打印侧输出流s2
        SideOutputDataStream<WaterSensor> s2 = process.getSideOutput(s2Tag);
        s2.print("侧输出流s2 -> ");

        env.execute();
    }
}
