package com.ys.service;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;


import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

/**
 * Created by yushi on 2017/3/27.
 */
public class Test {

    FileSystem fs = null;
    Configuration conf = null;


    public void init() throws Exception {

        conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://192.168.162.130:9000");

        //拿到一个文件系统操作的客户端实例对象
        /*fs = FileSystem.get(conf);*/
        //可以直接传入 uri和用户身份
        fs = FileSystem.get(new URI("hdfs://192.168.162.130:9000"), conf, "hadoop"); //最后一个参数为用户名
    }


    public void testUpload() throws Exception {

        Thread.sleep(2000);
        fs.copyFromLocalFile(new Path("e:/aa.log"), new Path("/access.log.copy"));
        fs.close();
        System.out.println();
    }


    public void testDownload() throws Exception {

        fs.copyToLocalFile(new Path("/access.log.copy"), new Path("d:/"));
        fs.close();
    }

    //@Test
    //public void testConf(){
    //    Iterator<Entry<String, String>> iterator = conf.iterator();
    //    while (iterator.hasNext()) {
    //        Entry<String, String> entry = iterator.next();
    //        System.out.println(entry.getValue() + "--" + entry.getValue());//conf加载的内容
    //    }
    //}

    /**
     * 创建目录
     */
    //@Test
    //public void makdirTest() throws Exception {
    //    boolean mkdirs = fs.mkdirs(new Path("/aaa/bbb"));
    //    System.out.println(mkdirs);
    //}

    /**
     * 删除
     */
    //@Test
    //public void deleteTest() throws Exception{
    //    boolean delete = fs.delete(new Path("/aaa"), true);//true， 递归删除
    //    System.out.println(delete);
    //}

    //@Test
    //public void listTest() throws Exception{
    //
    //    FileStatus[] listStatus = fs.listStatus(new Path("/"));
    //    for (FileStatus fileStatus : listStatus) {
    //        System.err.println(fileStatus.getPath()+"================="+fileStatus.toString());
    //    }
    //    //会递归找到所有的文件
    //    RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path("/"), true);
    //    while(listFiles.hasNext()){
    //        LocatedFileStatus next = listFiles.next();
    //        String name = next.getPath().getName();
    //        Path path = next.getPath();
    //        System.out.println(name + "---" + path.toString());
    //    }
    //}
    public static void main(String[] args) throws Exception {




        URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
        final URL url = new URL("hdfs://192.168.162.130:9000/");
        final InputStream in = url.openStream();
        OutputStream out = new FileOutputStream("hello.txt");
        IOUtils.copyBytes(in, out, 1024, true);
        out.close();
        in.close();

    }
}
