package com.ys.mr.InverIndex;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class InverIndexStepOne {

	static class InverIndexStepOneMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

		Text k = new Text();
		IntWritable v = new IntWritable(1);

		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

			String line = value.toString();

			String[] words = line.split(" ");

			FileSplit inputSplit = (FileSplit) context.getInputSplit();
			String fileName = inputSplit.getPath().getName();
			for (String word : words) {
				k.set(word + "--" + fileName);
				context.write(k, v);

			}

		}

	}

	static class InverIndexStepOneReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

		@Override
		protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

			int count = 0;
			for (IntWritable value : values) {

				count += value.get();
			}

			context.write(key, new IntWritable(count));

		}

	}

	public static void main(String[] args) throws Exception {
		if (args == null || args.length == 0) {
			args = new String[2];
			//args[0] = "hdfs://192.168.162.130:9000/wordcount/input/a.txt";
			//args[1] = "hdfs://192.168.162.130:9000/wordcount/output8";

			args[0] = "E:/joinindex";
			args[1] = "e:/temp";

		}

		Configuration conf = new Configuration();

		Job job = Job.getInstance(conf);
		job.setJarByClass(InverIndexStepOne.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		//FileInputFormat.setInputPaths(job, new Path("D:/srcdata/inverindexinput"));
		//FileOutputFormat.setOutputPath(job, new Path("D:/temp/out"));
		// 指定job的输入原始文件所在目录
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		// 指定job的输出结果所在目录
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapperClass(InverIndexStepOneMapper.class);
		job.setReducerClass(InverIndexStepOneReducer.class);

		job.waitForCompletion(true);

	}

}
