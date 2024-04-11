package com.itclj.functions;

import com.itclj.bean.WaterSensor;
import org.apache.flink.api.common.functions.FilterFunction;

public class FilterFunctionImpl implements FilterFunction<WaterSensor> {

    private String id;

    public FilterFunctionImpl(String id) {
        this.id = id;
    }

    @Override
    public boolean filter(WaterSensor waterSensor) throws Exception {
        return id.equals(waterSensor.getId());
    }
}
