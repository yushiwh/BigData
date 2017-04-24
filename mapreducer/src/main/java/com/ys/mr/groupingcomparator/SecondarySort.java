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
 *
 */
public class SecondarySort {
	
	static class SecondarySortMapper extends Mapper<LongWritable, Text, OrderBean, NullWritable>{
		
		OrderBean bean = new OrderBean();
		
		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

			String line = value.toString();
			String[] fields = StringUtils.split(line, ",");
			
			bean.set(new Text(fields[0]), new DoubleWritable(Double.parseDouble(fields[2])));
			
			context.write(bean, NullWritable.get());
			
		}
		
	}
	
	static class SecondarySortReducer extends Reducer<OrderBean, NullWritable, OrderBean, NullWritable>{
		
		
		//到达reduce时，相同id的所有bean已经被看成一组，且金额最大的那个一排在第一位
		@Override
		protected void reduce(OrderBean key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
			context.write(key, NullWritable.get());
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
		
		job.setJarByClass(SecondarySort.class);
		
		job.setMapperClass(SecondarySortMapper.class);
		job.setReducerClass(SecondarySortReducer.class);
		
		
		job.setOutputKeyClass(OrderBean.class);
		job.setOutputValueClass(NullWritable.class);
		
		FileInputFormat.setInputPaths(job, new Path("E:/wordcount/compare"));
		FileOutputFormat.setOutputPath(job, new Path("e:/wordcount/compare/out"));
		
		//在此设置自定义的Groupingcomparator类 
		job.setGroupingComparatorClass(ItemidGroupingComparator.class);
		//在此设置自定义的partitioner类
		job.setPartitionerClass(ItemIdPartitioner.class);
		
		job.setNumReduceTasks(2);
		
		job.waitForCompletion(true);
		
	}

}
