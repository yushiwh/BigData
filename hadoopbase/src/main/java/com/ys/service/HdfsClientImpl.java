package com.ys.service;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by yushi on 2017/3/27.
 */
@Service
@Component
public class HdfsClientImpl implements HdfsClient, CommandLineRunner {
    FileSystem fs = null;
    Configuration conf = new Configuration();

    @Override
    public void init() throws URISyntaxException, IOException, InterruptedException {


        conf.set("fs.defaultFS", "hdfs://192.168.162.130:9000");
        //拿到一个文件系统的客户端实例对象
        //可以直接传入 uri和用户身份
        fs = FileSystem.get(new URI("hdfs://192.168.162.130:9000"), conf, "hadoop"); //最后一个参数为用户名
        // fs = FileSystem.get(conf);
    }

    @Override
    public void upload() throws IOException {
        fs.copyFromLocalFile(new Path("e:/aa.log"), new Path("/access.log.copy"));
        fs.close();
    }


    @Override
    public void download() throws Exception {

        fs.copyToLocalFile(new Path("/access.log.copy"), new Path("e:/"));
        fs.close();
    }

    @Override
    public void conf() {
        Iterator<Map.Entry<String, String>> iterator = conf.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            System.out.println(entry.getValue() + "--" + entry.getValue());//conf加载的内容
        }
    }

    /**
     * 创建目录
     */
    @Override
    public void makdir() throws Exception {
        boolean mkdirs = fs.mkdirs(new Path("/aaa/bbb"));
        System.out.println(mkdirs);
    }

    /**
     * 删除
     */
    @Override
    public void delete() throws Exception {
        boolean delete = fs.delete(new Path("/aaa"), true);//true， 递归删除
        System.out.println(delete);
    }

    @Override
    public void list() throws Exception {

        FileStatus[] listStatus = fs.listStatus(new Path("/"));
        for (FileStatus fileStatus : listStatus) {
            System.err.println(fileStatus.getPath() + "=================" + fileStatus.toString());
        }
        //会递归找到所有的文件
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path("/"), true);
        while (listFiles.hasNext()) {
            LocatedFileStatus next = listFiles.next();
            String name = next.getPath().getName();
            Path path = next.getPath();
            System.out.println(name + "---" + path.toString());
        }
    }


    @Override
    public void run(String... strings) throws Exception {
        init();
    }
}
