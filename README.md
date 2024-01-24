## flink 学习

- 有界流
- 无界流

----
### flink 部署
flink client -> JobManager -> TaskManager

**机器规划**

| 机器名        | ip              | 部署角色                   |
|------------|-----------------|------------------------|
| flink01    | 192.168.10.152  | JobManager/TaskManager |
| flink02    | 192.168.10.153  | TaskManager            |
| flink03    | 192.168.10.154  | TaskManager            |

**hosts**
```shell
192.168.10.152 flink01
192.168.10.153 flink02
192.168.10.154 flink03
```

**jdk配置**
```shell
## 编辑环境变量
vim /etc/profile

## 使环境变量生效
source /etc/profile
```
添加环境变量
```shell
export JAVA_HOME=/opt/java/jdk1.8.0_221
export PATH=$JAVA_HOME/bin:$PATH
```

#### flink部署
```shell
# 部署路径
/opt/flink
/opt/flink/flink-1.17.1
```

**修改集群配置**
```shell
vim /opt/flink/flink-1.17.1/conf/flink-conf.yaml
```
- 配置项(flink01)
```yaml
jobmanager.rpc.address: flink01
jobmanager.bind-host:  0.0.0.0
taskmanager.bind-host: 0.0.0.0
taskmanager.host: flink01
rest.address: flink01
rest.bind-address: 0.0.0.0
```

- 配置workers节点信息
````shell
vim /opt/flink/flink-1.17.1/conf/workers 
````
workers相关配置值
````shell
flink01
flink02
flink03
````

- 配置maser信息
```shell
vim /opt/flink/flink-1.17.1/conf/masters
```

masters相关配置信息
````shell
flink01:8081
````

- 配置项(flink02)
```yaml
## 只需改下面这一项就可以了，其他的和flink01保持一致
taskmanager.host: flink02
```

- 配置项(flink03)
```yaml
## 只需改下面这一项就可以了，其他的和flink01保持一致
taskmanager.host: flink03
```

----
## 获取github ip 进行加速，获取到ip 后配置到hosts
https://sites.ipaddress.com/github.com/
