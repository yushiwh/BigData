package com.ys.mr.groupingcomparator;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 统计订单的最大值的那笔，比较简单的方法
 * 1、先进行排序
 * 2、在map中，将排好序的map作为一个整体传入后输出第一个，这样不用再进行排序
 *
 * @author yushi
 */
public class SecondarySort {

    public static String path1 = "file:///E:\\wordcount\\compare"; //file:///代表本地文件系统中路径的意思
    public static String path2 = "file:///e:\\wordcount\\compare\\out";

    static class SecondarySortMapper extends Mapper<LongWritable, Text, OrderBean, NullWritable> {

        OrderBean bean = new OrderBean();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            String line = value.toString();
            String[] fields = StringUtils.split(line, ",");

            bean.set(new Text(fields[0]), new DoubleWritable(Double.parseDouble(fields[2])));

            context.write(bean, NullWritable.get());

        }

    }

    static class SecondarySortReducer extends Reducer<OrderBean, NullWritable, OrderBean, NullWritable> {


        //到达reduce时，相同id的所有bean已经被看成一组，且金额最大的那个一排在第一位
        @Override
        protected void reduce(OrderBean key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
            context.write(key, NullWritable.get());
        }
    }


    public static void main(String[] args) throws Exception {


        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf);
        job.setJobName("yushi");
        job.setJarByClass(SecondarySort.class);



        job.setMapperClass(SecondarySortMapper.class);
        job.setReducerClass(SecondarySortReducer.class);


        job.setOutputKeyClass(OrderBean.class);
        job.setOutputValueClass(NullWritable.class);

        FileInputFormat.setInputPaths(job, new Path(path1));
        FileOutputFormat.setOutputPath(job, new Path(path2));

        //在此设置自定义的Groupingcomparator类
        job.setGroupingComparatorClass(ItemidGroupingComparator.class);
        //在此设置自定义的partitioner类
        job.setPartitionerClass(ItemIdPartitioner.class);

        job.setNumReduceTasks(3);//设置ReduceTasks的数量


        //将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn去运行
        //job.submit();
        boolean res = job.waitForCompletion(true);
        System.exit(res ? 0 : 1);

    }

}
