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

- /opt/hadoop/hadoop-3.3.6/etc/hadoop/hdfs-site.xml

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
```

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
