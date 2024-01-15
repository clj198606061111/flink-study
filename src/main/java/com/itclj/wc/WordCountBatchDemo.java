package com.itclj.wc;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.operators.UnsortedGrouping;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class WordCountBatchDemo {
    public static void main(String[] args) throws Exception {
        //1. 创建执行环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        //2. 读取数据，从文件中读取
        DataSource<String> lineDS = env.readTextFile("input/word.txt");

        //3. 切分，转换
        FlatMapOperator<String, Tuple2<String, Integer>> wordAndOne = lineDS.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public void flatMap(String s, Collector<Tuple2<String, Integer>> out) throws Exception {
                //3.1 按照 空格 切分单词
                String[] words = s.split(" ");

                //3.2 将 单词 转换为 (word,1)
                for (String word : words) {
                    Tuple2<String, Integer> wordTuple2 = Tuple2.of(word, 1);
                    out.collect(wordTuple2);
                }
            }
        });

        //4. 按照 word 分组
        UnsortedGrouping<Tuple2<String, Integer>> wordAndOnwGroupBy = wordAndOne.groupBy(0);

        //5. 各分组内聚合
        AggregateOperator<Tuple2<String, Integer>> sum = wordAndOnwGroupBy.sum(1);//位置1

        //6. 输出
        sum.print();
    }
}
