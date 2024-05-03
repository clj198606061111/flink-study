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
```