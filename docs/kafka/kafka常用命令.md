### 启动
- 启动zookeeper
```
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
```
- 启动kafka

```
.\bin\windows\kafka-server-start.bat .\config\server.properties
```

### topic管理

- 创建topc
```
.\bin\windows\kafka-topics.bat --create --partitions 1 --replication-factor 1 --topic itcljtest --bootstrap-server localhost:9092
```

- 查看创建的topic

```
.\bin\windows\kafka-topics.bat --describe --topic itcljtest --bootstrap-server localhost:9092
```

### 生产者
```
.\bin\windows\kafka-console-producer.bat --topic itcljtest --bootstrap-server localhost:9092
```

### 消费者
```
.\bin\windows\kafka-console-consumer.bat --topic itcljtest --from-beginning --bootstrap-server localhost:9092
```