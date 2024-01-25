## 同步执行命令脚本(xcall)

- 在一个节点上输入命令可以同步到其余节点执行
- 建议创建脚本目录为:/usr/local/bin

shell脚本
```shell
#!/bin/bash

# 获取控制台指令

cmd=$*

# 判断指令是否为空
if [ ! -n "$cmd" ]
then
        echo "command can not be null !"
        exit
fi

# 获取当前登录用户
user=`whoami`

# 在从机执行指令,这里需要根据你具体的集群情况配置，host与具体主机名一致，同上
for host in flink01 flink02 flink03
do
        echo "================current host is node0$host================="
        echo "--> excute command \"$cmd\""
        ssh $user@node0$host $cmd
done

echo "excute successfully !"

```