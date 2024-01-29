# flink 部署模式

1. 会话模式（Session MOde）
2. 单作业模式（Per-Job Mode）-- 已标记为过时
3. 应用模型（Application Mode）

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



