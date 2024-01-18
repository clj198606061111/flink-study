## flink 学习

- 有界流
- 无界流

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

## 获取github ip 进行加速，获取到ip 后配置到hosts
https://sites.ipaddress.com/github.com/
