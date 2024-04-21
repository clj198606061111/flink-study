package com.itclj.window;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class MyProcess extends ProcessWindowFunction<String, String, String, TimeWindow> {


    @Override
    public void process(String key, ProcessWindowFunction<String, String, String, TimeWindow>.Context context, Iterable<String> iterable, Collector<String> out) throws Exception {
        long startTs = context.window().getStart();
        long endTs = context.window().getEnd();
        String windowStart = DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(startTs);
        String windowEnd = DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(endTs);

        long count = iterable.spliterator().estimateSize();
        out.collect("key=" + key + "的窗口[" + windowStart + "," + windowEnd + "]包含" + count + "条数据===>" + iterable.toString());
    }
}
