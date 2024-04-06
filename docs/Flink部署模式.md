# flink 部署模式

1. standalone
2. 【yarn】会话模式（Session Mode）
3. 【yarn】单作业模式（Per-Job Mode）-- 已标记为过时
4. 【yarn】应用模型（Application Mode） -- 推荐

他们的主要区别在于：集群的 **生命周期** 以及 **资源的分配方式** ；以及应用的main方法到底在哪里执行--客户端（client）,还是jobManager。

应用模式：应用提交到JobManager,由JObManager负责提交应用，不在客户端执行。

# 运行模式
## standalone模式

一个任务一个集群，这种模式基本不用
````shell
## 程序jar包需要放到flink的lib目录
./bin/standalone-job.sh start --job-classname com.itclj.wc.WordCountStreamUnboundedDemo
````
## Yarn运行模式（重点）
flink和hadoop集成，通过配置环境变量方式，最方便。

### 配置环境变量
前提是hadoop要提前准备好
````shell
# sudo vim /etc/profile.d/my_env.sh

HADOOP_HOME=/opt/hadoop/hadoop-3.3.6
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
export HADOP_CONF_DIR=${HADOOP_HOME}/etc/hadoop
export HADOOP_CLASSPATH=`hadoop classpath`
````

###  会话模式

```shell
/opt/flink/flink-1.17.1/bin/yarn-session.sh -nm itclj-20240228-2222 -d 
```
参数说明：
- -d: 分离模式，如果不想让flink yarn 客户端一直前台运行，可以使用这个参数，即使关闭当前对话框，yarn session 也可以后台运行。
- -jm（--jobManagerMemory）: 配置JObManager 所需内存，默认单位 MB。
- -nm（--name）:配置在yarn ui界面上显示的任务名称。

###  应用模式

- 执行命令提交作业
```shell
/opt/flink/flink-1.17.1/bin/flink run-application -t yarn-application -c com.itclj.wc.WordCountStreamUnboundedDemo /opt/flink/libs/flink-study-1.1.1.jar

```

更多参数 及参数说明参看官方文档：https://nightlies.apache.org/flink/flink-docs-release-1.19/docs/deployment/config/

- 执行命令查看或取消作业
```shell
/opt/flink/flink-1.17.1/bin/flink  list -t yarn-application -Dyarn.application.id=application_1712202419852_0003	

/opt/flink/flink-1.17.1/bin/flink  cancel -t yarn-application -Dyarn.application.id=application_XXXXX_xxxx <jobId>

# 举例，eg:
/opt/flink/flink-1.17.1/bin/flink  cancel -t yarn-application -Dyarn.application.id=application_1712202419852_0003 56c46aa1d1f3f441988e8c801af8925f

```

- 提交到hdfs
```shell
## 先把flink相关包上传到hdfs
hadoop fs -mkdir /flink-dist
hadoop fs -put /opt/flink/flink-1.17.1/lib/ /flink-dist
hadoop fs -put /opt/flink/flink-1.17.1/plugins/ /flink-dist


## 再把应用包上传到hdfs
hadoop fs -mkdir /flink-jars
hadoop fs -put /opt/flink/libs/flink-study-1.1.1.jar /flink-jars


## 启动flink程序
/opt/flink/flink-1.17.1/bin/flink run-application -t yarn-application -Dyarn.provided.lib.dirs="hdfs://flink01:9000/flink-dist" -c com.itclj.wc.WordCountStreamUnboundedDemo hdfs://flink01:9000/flink-jars/flink-study-1.1.1.jar
```