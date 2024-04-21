package com.itclj.window;

import com.itclj.bean.WaterSensor;
import org.apache.flink.api.common.functions.AggregateFunction;

public class MyAgg implements AggregateFunction<WaterSensor, Integer, String> {
    @Override
    public Integer createAccumulator() {
        System.out.println("创建累加器");
        return 0;
    }
    @Override
    public Integer add(WaterSensor waterSensor, Integer integer) {
        System.out.println("调用Add方法,value=" + integer);
        return integer + waterSensor.getVc();
    }
    @Override
    public String getResult(Integer integer) {
        System.out.println("获取result方法");
        return integer.toString();
    }
    @Override
    public Integer merge(Integer integer, Integer acc1) {
        System.out.println("调用merge方法");
        return 0;
    }
}
