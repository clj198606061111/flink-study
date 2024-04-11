package com.itclj.transform;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * RichXXXFunction: 富函数
 * 1. 多了生命周期管理方法
 *    open(): 每个子任务在 启动 时调用一次
 *    close()：每个子任务在 结束 时调用一次
 * 2. 多了一个运行时上下文
 */
public class RichFunctionDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<Integer> source = env.fromElements(1, 2, 3, 4);
        SingleOutputStreamOperator<Integer> map = source.map(new RichMapFunction<Integer, Integer>() {
            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                System.out.println("子任务编号=" + getRuntimeContext().getIndexOfThisSubtask() + "启动,子任务名称="
                        + getRuntimeContext().getTaskNameWithSubtasks() + "启动");
            }

            @Override
            public void close() throws Exception {
                super.close();
                System.out.println("子任务编号=" + getRuntimeContext().getIndexOfThisSubtask() + "启动,子任务名称="
                        + getRuntimeContext().getTaskNameWithSubtasks() + "关闭");
            }

            @Override
            public Integer map(Integer value) throws Exception {
                return value + 1;
            }
        });
        map.print();

        env.execute();
    }
}
