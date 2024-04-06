flink历史服务器是在flink异常退出后，没有界面能够查看原来flink作业页面上的状态。

1. 创建归档目录
```shell
hadoop fs -mkdir -p /logs/flink-job
```

2. 历史服务器配置
```shell
# jobmanager归档目录
jobmanager.archive.fs.dir: hdfs://flink01:9000/logs/flink-job/

# 历史服务器地址
historyserver.web.address: flink02

# historyserver归档目录
historyserver.archive.fs.dir: hdfs://flink01:9000/logs/flink-job/

# 历史服务器刷新频率
historyserver.archive.fs.refresh-interval: 10000

```

3. 同步配置
```shell
xsync /opt/flink/flink-1.17.1/conf
```

5. 启动&停止历史服务器
```shell
# 启动flink history server
/opt/flink/flink-1.17.1/bin/historyserver.sh start

# 停止flink history server
/opt/flink/flink-1.17.1/bin/historyserver.sh stop
```

5. 进入flink history server页面
```shell
http://flink02:8082/
```
   