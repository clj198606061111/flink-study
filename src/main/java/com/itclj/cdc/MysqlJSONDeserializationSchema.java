package com.itclj.cdc;

import com.alibaba.fastjson2.JSONObject;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import io.debezium.data.Envelope;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.List;

/**
 * flink-cdc mysql binlog 自定义序列化器
 * 序列化后以一个JSON对象保存，方便后续处理
 * <p>
 * 格式化化后输出样例：
 * {"db":"itclj","tableName":"tags","before":{"id":2,"tags":"c,d,e,f"},"after":{"id":2,"tags":"c,d,e,f,g"},"op":"u"}
 */
public class MysqlJSONDeserializationSchema implements DebeziumDeserializationSchema<String> {

    /**
     * db:库名
     * tableName:表名
     * before：操作前数据
     * after:操作后数据
     * op:操作类型（增删改）
     */
    @Override
    public void deserialize(SourceRecord record, Collector<String> out) throws Exception {
        String topic = record.topic();
        String[] split = topic.split("\\.");
        JSONObject result = new JSONObject();
        result.put("db", split[1]);
        result.put("tableName", split[2]);

        Struct value = (Struct) record.value();
        //构建before数据
        Struct before = value.getStruct("before");
        if (null != before) {
            result.put("before", getFieldsObject(before));
        }

        //够级after数据
        Struct after = value.getStruct("after");
        if (null != after) {
            result.put("after", getFieldsObject(after));
        }
        //构建op（操作类型）
        result.put("op", Envelope.operationFor(record).code());

        out.collect(result.toJSONString());
    }

    @Override
    public TypeInformation<String> getProducedType() {
        return BasicTypeInfo.STRING_TYPE_INFO;
    }

    private JSONObject getFieldsObject(Struct after) {
        //获取列信息
        Schema schema = after.schema();
        List<Field> fields = schema.fields();
        JSONObject jsonObject = new JSONObject();
        for (Field field : fields) {
            jsonObject.put(field.name(), after.get(field));
        }
        return jsonObject;
    }
}
