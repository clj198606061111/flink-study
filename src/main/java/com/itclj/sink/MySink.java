package com.itclj.sink;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public class MySink extends RichSinkFunction<String> {

    /**
     * sink 的核心逻辑，写入的逻辑就在这个方法里面
     *
     * @param value
     * @param context
     * @throws Exception
     */
    @Override
    public void invoke(String value, Context context) throws Exception {
        //写出逻辑
        // 这个方法是 来一条数据调用一次，所以不在这里创建链接对象
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        //这里 创建 链接对象
    }

    @Override
    public void close() throws Exception {
        super.close();
        //这里 关闭 链接对象
    }
}
