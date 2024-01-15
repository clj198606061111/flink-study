package com.itclj.wc;

import org.apache.flink.api.java.ExecutionEnvironment;

public class WordCountBatchDemo {
    public static void main(String[] args) {
        //1. 创建执行环境
        ExecutionEnvironment env=ExecutionEnvironment.getExecutionEnvironment();

        //2. 读取数据，从文件中读取
        env.readTextFile("input/word.txt");

        //3. 切分，转换
        //4. 按照 word 分组
        //5. 各分组内聚合
        //6. 输出
    }
}
