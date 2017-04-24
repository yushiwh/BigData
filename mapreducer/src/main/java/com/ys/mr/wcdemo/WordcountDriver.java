package com.ys.mr.wcdemo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


/**
 * 相当于一个yarn集群的客户端
 * 需要在此封装我们的mr程序的相关运行参数，指定jar包
 * 最后提交给yarn
 *
 * 统计单词出现次数
 *
 * @author
 */
public class WordcountDriver {

    public static void main(String[] args) throws Exception {

        if (args == null || args.length == 0) {
            args = new String[2];
            args[0] = "hdfs://192.168.162.130:9000/wordcount/input/a.txt";
            //args[1] = "hdfs://192.168.162.130:9000/wordcount/output8";

           // args[0] = "e:/a.txt";
          //  args[1] = "e:/out.log";

            args[1] =  "file:///e:\\out.log";

        }

        Configuration conf = new Configuration();

        /**
         * 连接hadoop集群配置
         */

        conf.set("fs.default.name", "hdfs://192.168.162.130:9000");
        conf.set("hadoop.job.user", "hadoop");
        conf.set("mapreduce.framework.name", "yarn");
        conf.set("mapreduce.jobtracker.address", "192.168.162.130:9001");
        conf.set("yarn.resourcemanager.hostname", "192.168.162.130");
        conf.set("yarn.resourcemanager.admin.address", "192.168.162.130:8033");
        conf.set("yarn.resourcemanager.address", "192.168.162.130:8032");
        conf.set("yarn.resourcemanager.resource-tracker.address", "192.168.162.130:8036");
        conf.set("yarn.resourcemanager.scheduler.address", "192.168.162.130:8030");


        //设置的没有用!  ??????
        //conf.set("HADOOP_USER_NAME", "hadoop");
        //conf.set("dfs.permissions.enabled", "false");



        //conf.set("mapreduce.framework.name", "yarn");
        //conf.set("yarn.resoucemanager.hostname", "mini1");
        Job job = Job.getInstance(conf);
		
		/*job.setJar("/home/hadoop/wc.jar");*/
        //指定本程序的jar包所在的本地路径
        job.setJarByClass(WordcountDriver.class);

        //指定本业务job要使用的mapper/Reducer业务类
        job.setMapperClass(WordcountMapper.class);
        job.setReducerClass(WordcountReducer.class);
        //指定mapper输出数据的kv类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        //指定最终输出的数据的kv类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        //指定job的输入原始文件所在目录
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        //指定job的输出结果所在目录
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        //将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn去运行
		/*job.submit();*/
        boolean res = job.waitForCompletion(true);
        System.exit(res ? 0 : 1);

    }


}
