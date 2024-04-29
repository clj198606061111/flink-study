## 验证binlog是否已经开启
```sql
SHOW VARIABLES LIKE 'log_bin';
```
如果启用了，这个查询会返回log_bin的值为ON。

## 开启binlog
编辑MySQL配置文件（通常是my.cnf或my.ini），在[mysqld]部分添加以下配置：
```properties
[mysqld]
# 这个参数表示启用 binlog 功能，并指定 binlog 的存储目录
log_bin = D:\database\log\mysql-bin.log

# 设置一个 binlog 文件的最大字节
# 设置最大 100MB
max_binlog_size=104857600

# 设置了 binlog 文件的有效期（单位：天）
expire_logs_days = 7

# 为当前服务取一个唯一的 id（MySQL5.7 之后需要配置）
server-id = 1

# 设置 binlog 的格式，三种
binlog_format = ROW
```
三种binlog格式：
- Statement（Statement-Based Replication,SBR）：每一条会修改数据的 SQL 都会记录在 binlog 中。
- Row（Row-Based Replication,RBR）：不记录 SQL 语句上下文信息，仅保存哪条记录被修改。
- Mixed（Mixed-Based Replication,MBR）：Statement 和 Row 的混合体。

### 配置完成后，重启mysql服务。
- windows 重启mysql
以管理员身份进入命令行
```shell
## 停止服务
net stop mysql

## 启动服务
net start mysql
```