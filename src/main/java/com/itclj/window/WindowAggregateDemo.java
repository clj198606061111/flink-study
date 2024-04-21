package com.itclj.window;

import com.itclj.bean.WaterSensor;
import com.itclj.functions.WaterSensorMapFunction;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

public class WindowAggregateDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<WaterSensor> sensorDS = env
                .socketTextStream("flink01", 7777)
                .map(new WaterSensorMapFunction());

        KeyedStream<WaterSensor, String> sensorKS = sensorDS.keyBy(sensor -> sensor.getId());

        //1. 窗口分配器
        WindowedStream<WaterSensor, String, TimeWindow> sensorWS = sensorKS.window(TumblingProcessingTimeWindows.of(Time.seconds(5)));

        //2. 窗口函数：增量聚合 aggregate
        /**
         * 1. 属于本窗口的第一条数据来，创建窗口，创建累加器
         * 2. 增量聚合：来一条计算一条，调用一次add方法
         * 3. 窗口输出时，调用一次getResult方法
         * 4. 输入、中间累加器、输出 输出类型可以不一样，非常灵活
         */
        SingleOutputStreamOperator<String> aggregate = sensorWS.aggregate(
                /**
                 * 第一个类型：输入数据类型
                 * 第二个类型：累加器类型，存储的中间计算结果的类型
                 * 第三个类型：输出类型
                 */
                new AggregateFunction<WaterSensor, Integer, String>() {

                    /**
                     * 初始化累加器
                     * @return
                     */
                    @Override
                    public Integer createAccumulator() {
                        System.out.println("创建累加器");
                        return 0;
                    }

                    /**
                     * 聚合逻辑
                     * @param waterSensor
                     * @param integer
                     * @return
                     */
                    @Override
                    public Integer add(WaterSensor waterSensor, Integer integer) {
                        System.out.println("调用Add方法,value=" + integer);
                        return integer + waterSensor.getVc();
                    }

                    /**
                     * 获取最终结果，窗口触发时输出
                     * @param integer
                     * @return
                     */
                    @Override
                    public String getResult(Integer integer) {
                        System.out.println("获取result方法");
                        return integer.toString();
                    }

                    /**
                     * 会话窗口才会用到，
                     * @param integer
                     * @param acc1
                     * @return
                     */
                    @Override
                    public Integer merge(Integer integer, Integer acc1) {
                        System.out.println("调用merge方法");
                        return 0;
                    }
                });

        aggregate.print();

        env.execute();
    }
}
