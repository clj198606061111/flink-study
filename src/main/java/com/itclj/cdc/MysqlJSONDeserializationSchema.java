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
            //获取列信息
            Schema beforeSchema = before.schema();
            List<Field> fields = beforeSchema.fields();
            JSONObject beforeJSON = new JSONObject();
            for (Field field : fields) {
                beforeJSON.put(field.name(), before.get(field));
            }

            result.put("before", beforeJSON);
        }


        //够级after数据
        Struct after = value.getStruct("after");
        if (null != after) {
            //获取列信息
            Schema afterSchema = after.schema();
            List<Field> fields = afterSchema.fields();
            JSONObject afterJSON = new JSONObject();
            for (Field field : fields) {
                afterJSON.put(field.name(), after.get(field));
            }

            result.put("after", afterJSON);
        }
        //构建op（操作类型）
        Envelope.Operation operation = Envelope.operationFor(record);
        result.put("op", operation.code());

        out.collect(result.toJSONString());
    }

    @Override
    public TypeInformation<String> getProducedType() {
        return BasicTypeInfo.STRING_TYPE_INFO;
    }
}
