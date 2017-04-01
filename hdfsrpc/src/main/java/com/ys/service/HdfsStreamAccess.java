package com.ys.service;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;


/**
 * 用流的方式来操作hdfs上的文件
 * 可以实现读取指定偏移量范围的数据
 *
 * @author
 */
public class HdfsStreamAccess {

    FileSystem fs = null;
    Configuration conf = null;

    @Before
    public void init() throws Exception {

        conf = new Configuration();
        //拿到一个文件系统操作的客户端实例对象
//		fs = FileSystem.get(conf);
        //可以直接传入 uri和用户身份
        fs = FileSystem.get(new URI("hdfs://192.168.162.130:9000"), conf, "hadoop"); //最后一个参数为用户名
    }


    /**
     * 通过流的方式上传文件到hdfs
     *
     * @throws Exception
     */
    @Test
    public void testUpload() throws Exception {

        FSDataOutputStream outputStream = fs.create(new Path("/angelababy.love"), true);
        FileInputStream inputStream = new FileInputStream("e:/angelababy.love");

        IOUtils.copy(inputStream, outputStream);

    }


    /**
     * 通过流的方式获取hdfs上数据
     *
     * @throws Exception
     */
    @Test
    public void testDownLoad() throws Exception {

        FSDataInputStream inputStream = fs.open(new Path("/angelababy.love"));

        FileOutputStream outputStream = new FileOutputStream("e:/angelababy.love");

        IOUtils.copy(inputStream, outputStream);

    }


    @Test
    /**
     * 指定读取
     */
    public void testRandomAccess() throws Exception {

        FSDataInputStream inputStream = fs.open(new Path("/angelababy.love"));

        inputStream.seek(200);//从哪个字节偏量开始读取

        FileOutputStream outputStream = new FileOutputStream("e:/angelababy.love.part2");

        IOUtils.copy(inputStream, outputStream);


    }


    /**
     * 显示hdfs上文件的内容
     *
     * @throws IOException
     * @throws IllegalArgumentException
     */
    @Test
    public void testCat() throws IllegalArgumentException, IOException {

        FSDataInputStream in = fs.open(new Path("/angelababy.love"));

        IOUtils.copy(in, System.out);


        //IOUtils.copyBytes(in, System.out, 1024);


    }


}
