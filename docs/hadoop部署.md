## hadoop 集群部署
1. hdfs
2. yarn

### 部署

### 目录规划
- 安装到/opt/hadoop下面
- 安装版本：hadoop-3.3.6.tar
```shell
# 安装到/opt/hadoop下面
xsync /opt/hadoop/hadoop-3.3.6

# hadoop pid 存储位置
xcall mkdir /opt/hadoop/pid

# hadoop log 存储位置
xcall mkdir /opt/hadoop/log

# hadoop namenode 元数据存储位置
xcall mkdir /opt/hadoop/hdfs
xcall mkdir /opt/hadoop/hdfs/namenode

# hadoop datanode 元数据存储位置
xcall mkdir /opt/hadoop/hdfs/datanode

# hadoop 的缓存目录
xcall mkdir /opt/hadoop/hdfs/tmp

```

### 角色规划

| 机器名        | ip              | 部署角色                                       |
|------------|-----------------|--------------------------------------------|
| flink01    | 192.168.10.152  | NameNode / ResourceManager / WebAppProxy / Map Reduce Job History Server|
| flink02    | 192.168.10.153  | DataNode / NodeManager/ Secondary NameNode |
| flink03    | 192.168.10.154  | DataNode / NodeManager                     |

### 配置修改
#### 环境变量配置

在  `/etc/profile.d`下面新建`hadoop.sh`环境变量配置文件，环境变量配置文件配置如下，
配置好后，执行如下命令时配置文件生效 
```shell

source /etc/profile.d/hadoop.sh

# 检查环境变量是否生效
echo $HADOOP_HOME
```

- /etc/profile.d/hadoop.sh
```shell
HADOOP_HOME=/opt/hadoop/hadoop-3.3.6
export HADOOP_HOME
export PATH=$PATH:$JAVA_HOME/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
```

同步配置到其他节点
```shell
xsync /etc/profile.d/hadoop.sh

xcall source /etc/profile.d/hadoop.sh
xcall echo $HADOOP_HOME
```

#### hadoop-env.sh

| 配置Key | 配置值              |
| --- |------------------|
| HADOOP_PID_DIR | /opt/hadoop/pid  | 
|HADOOP_LOG_DIR | /opt/hadoop/log  |

#### core-site.xml

| 配置key | 配置值 | 配置说明                               |
| --- |-----|------------------------------------|
| fs.defaultFS |     | namenode 地址，eg: 	hdfs://host:port/ |

#### hdfs-site.xml

- /opt/hdoaop/hadoop-3.3.6/etc/hadoop/hdfs-site.xml

| 配置key | 配置值                       | 配置说明            |
| --- |---------------------------|-----------------|
| dfs.namenode.name.dir | /opt/hadoop/hdfs/namenode | namenode元数据存储位置 |
| dfs.datanode.data.dir | /opt/hadoop/hdfs/datanode | datanode元数据存储位置 |

#### yarn-site.xml


| 配置key | 配置值                       | 配置说明            |
| --- |---------------------------|-----------------|
| dfs.namenode.name.dir | /opt/hadoop/hdfs/namenode | namenode元数据存储位置 |


#### 添加用户
hadoop 和 yarn不能在root下执行。
```shell
xcall groupadd hdfs
xcall useradd -g hdfs hdfs

# 更改目录所有者
xcall chown hdfs:hdfs -R /opt/hadoop
```

#### hadoop-env.sh

- /opt/hadoop/hadoop-3.3.6/etc/hadoop/hadoop-env.sh

在文件尾添加
```shell
# java home 必须要配置
export JAVA_HOME=/opt/java/jdk1.8.0_221

# Hadoop 用户名
export HADOOP_SHELL_EXECNAME=hdfs

# NameNode 用户名
export HDFS_NAMENODE_USER=hdfs

# DataNode 用户名
export HDFS_DATANODE_USER=hdfs

# Secondary NameNode 用户名
export HDFS_SECONDARYNAMENODE_USER=hdfs

# Yarn Resource Manager 用户名
export YARN_RESOURCEMANAGER_USER=hdfs

# Yarn NodeManager 用户名
export YARN_NODEMANAGER_USER=hdfs
```

### 启动集群
```shell
start-dfs.sh && start-yarn.sh

stop-dfs.sh && stop-yarn.sh

/opt/hadoop/hadoop-3.3.6/sbin/start-dfs.sh && /opt/hadoop/hadoop-3.3.6/sbin/start-yarn.sh

/opt/hadoop/hadoop-3.3.6/sbin/stop-dfs.sh && /opt/hadoop/hadoop-3.3.6/sbin/stop-yarn.sh

hdfs dfsadmin -report
```

- 启动后查看hdfs状态
````shell
[hdfs@flink01 logs]$ hdfs dfsadmin -report
Configured Capacity: 36477861888 (33.97 GB)
Present Capacity: 29015072768 (27.02 GB)
DFS Remaining: 28794200064 (26.82 GB)
DFS Used: 220872704 (210.64 MB)
DFS Used%: 0.76%
Replicated Blocks:
        Under replicated blocks: 0
        Blocks with corrupt replicas: 0
        Missing blocks: 0
        Missing blocks (with replication factor 1): 0
        Low redundancy blocks with highest priority to recover: 0
        Pending deletion blocks: 0
Erasure Coded Block Groups: 
        Low redundancy block groups: 0
        Block groups with corrupt internal blocks: 0
        Missing block groups: 0
        Low redundancy blocks with highest priority to recover: 0
        Pending deletion blocks: 0

-------------------------------------------------
Live datanodes (2):

Name: 192.168.10.153:9866 (flink02)
Hostname: flink02
Decommission Status : Normal
Configured Capacity: 18238930944 (16.99 GB)
DFS Used: 176246784 (168.08 MB)
Non DFS Used: 3727683584 (3.47 GB)
DFS Remaining: 14335000576 (13.35 GB)
DFS Used%: 0.97%
DFS Remaining%: 78.60%
Configured Cache Capacity: 0 (0 B)
Cache Used: 0 (0 B)
Cache Remaining: 0 (0 B)
Cache Used%: 100.00%
Cache Remaining%: 0.00%
Xceivers: 0
Last contact: Wed Apr 03 23:47:24 EDT 2024
Last Block Report: Wed Apr 03 23:46:09 EDT 2024
Num of Blocks: 15


Name: 192.168.10.154:9866 (flink03)
Hostname: flink03
Decommission Status : Normal
Configured Capacity: 18238930944 (16.99 GB)
DFS Used: 44625920 (42.56 MB)
Non DFS Used: 3735105536 (3.48 GB)
DFS Remaining: 14459199488 (13.47 GB)
DFS Used%: 0.24%
DFS Remaining%: 79.28%
Configured Cache Capacity: 0 (0 B)
Cache Used: 0 (0 B)
Cache Remaining: 0 (0 B)
Cache Used%: 100.00%
Cache Remaining%: 0.00%
Xceivers: 0
Last contact: Wed Apr 03 23:47:24 EDT 2024
Last Block Report: Wed Apr 03 23:46:09 EDT 2024
Num of Blocks: 12


````

- 启动后进入namenode节点查看hdfs集群信息
```shell
http://192.168.10.152:9870
```
- yarn监控地址
```shell
http://192.168.10.152:8088/cluster
```

----
#### 参考文章
- [《官方安装文档》](https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/ClusterSetup.html)
- [《Hadoop 3.3.6 安装教程》](https://blog.csdn.net/weixin_44330351/article/details/135328783)
- [《 vmware配置固定网络ip》](https://zhuanlan.zhihu.com/p/27943124)
