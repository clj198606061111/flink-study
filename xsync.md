
# 远程同步工具
## rsync

### 1. rsync是什么

rsync主要用于备份和镜像，具有速度快、避免复制相同内容和支持符号链接的优点。

### 2. 基本语法
```shell
rsync    -av       $pdir/$fname              $user@hadoop$host:$pdir/$fname
命令   选项参数   要拷贝的文件路径/名称    目的用户@主机:目的路径/名称
```

- 参数说明

|选项|功能|
|---|---|
|-a | 归档拷贝|
|-v | 显示复制过程|

### 3. 案例

把机器1上的目录同步到机器2上
```shell
## 安装rsync
yum install rsync

rsync -av /opt/flink/ root@flink03:/opt/flink/
```

### rsync和 scp 区别
- 1） 用rsync做文件的复制要比scp的速度快，rsync只对差异文件做更新。
- 2） scp是把所有文件都复制过去

## xsync集群分发脚本

```shell
#!/bin/bash
#1 获取输入参数个数，如果没有参数，直接退出
pcount=$#
if [ $pcount -lt 1 ]
then
    echo Not Enough Arguement!
    exit;
fi

#2. 遍历集群所有机器
# 也可以采用：
# for host in hadoop{102..104};
for host in flink01 flink02 flink03
do
    echo ====================    $host    ====================
    #3. 遍历所有目录，挨个发送
    for file in $@
    do
        #4 判断文件是否存在
        if [ -e $file ]
        then
            #5. 获取父目录
            pdir=$(cd -P $(dirname $file); pwd)
            echo pdir=$pdir
            
            #6. 获取当前文件的名称
            fname=$(basename $file)
            echo fname=$fname
            
            #7. 通过ssh执行命令：在$host主机上递归创建文件夹（如果存在该文件夹）
            ssh $host "mkdir -p $pdir"
            
			#8. 远程同步文件至$host主机的$USER用户的$pdir文件夹下
            rsync -av $pdir/$fname $USER@$host:$pdir
        else
            echo $file does not exists!
        fi
    done
done
```

修改xsync具有执行权限
```shell
 chmod 777 xsync
```

调用脚本, 如果将xsync放到/home/zxy/bin目录下仍然不能实现全局使用，可以将xsync移动到/usr/local/bin目录下。
```shell
xsync /home/zxy/bin
```

## 配置集群间免密登录
### 生成密钥对
````shell
ssh-keygen -t rsa 
````
- 然后敲（三个回车），就会生成两个文件id_rsa（私钥）、id_rsa.pub（公钥）
- 使用rsa算法生成秘钥对
- 生成的秘钥对在用户家目录下： /home/user/.ssh

### 将公钥拷贝到要免密登录的目标机器上
````shell
[root@flink01 .ssh]# ssh-copy-id flink02
[root@flink01 .ssh]# ssh-copy-id flink03
````

### 分别ssh直接登陆其他机器
```shell
[root@flink01 ~]# ssh flink02
```


----
#### 参考文章
- [《xsync同步脚本的创建及使用》](https://blog.csdn.net/weixin_41399650/article/details/125347570)
- [《xsync 集群同步工具》](https://blog.csdn.net/qq_39363204/article/details/128439311)
- [《Linux集群分发脚本xsync》](https://cloud.tencent.com/developer/article/2078844)