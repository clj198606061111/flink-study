## 通过启动脚本配置检查点
```shell
/opt/flink/flink-1.17.1/bin/flink run-application -t yarn-application -Dyarn.provided.lib.dirs="hdfs://flink01:9000/flink-dist" -Dstate.checkpoints.dir="hdfs://flink01:9000/flink-checkpoints" -Dstate.savepoints.dir="hdfs://flink01:9000/flink-savepoint" -Dstate.backend.type="rocksdb" -c com.itclj.cdc.MysqlSourceCDCCheckPointDemo hdfs://flink01:9000/flink-jars/flink-study-1.1.3.jar


```

## 退出时保存检查点
```shell
# stop 优雅停止，要求source实现StoppableFunction接口
bin/flink stop -p hdfs://hadoop102:8020/sp {job-id} -yid {application-id}

# cancel 立即停止
bin/flink cancel -s hdfs://hadoop102:8082/sp {job-id} -yid {application-id}
```

## 从检查点启动
```shell
bin/flink run-application -d -t yarn-application -s hdfs://hadoop102:8082/sp/savepoint-xxxx-xxxxx -c com.itclj.checkpoint.SavepointDemo Flink-study-1.0.0.jar


## jar包都在yarn上
/opt/flink/flink-1.17.1/bin/flink run-application -t yarn-application -Dyarn.provided.lib.dirs="hdfs://flink01:9000/flink-dist" -s hdfs://flink01:9000/flink-savepoint/savepoint-9ffcf1-277a3e21ac40 -c com.itclj.cdc.MysqlSourceCDCCheckPointDemo hdfs://flink01:9000/flink-jars/flink-study-1.1.3.jar
```