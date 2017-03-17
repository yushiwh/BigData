package com.ys.service;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yushi on 2017/3/17.
 */
@Service
@Component
public class DistributedClientImpl implements DistributedClient, CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DistributedClientImpl.class);
    private static final String ParentNode = "/servers";

    //注意加上volatile，
    private volatile List<String> serverList = null;
    @Value("${zookeeper.address}")
    private String connectString;


    private static final int sessionTimeOut = 3000;
    //创建ZooKeeper实例
    ZooKeeper zk;

    /**
     * 创建Watcher实例
     */
    Watcher wh = new Watcher() {
        @Override
        public void process(WatchedEvent watchedEvent) {
            System.out.println("已经触发了" + watchedEvent.getType() + "的事件" + "------" + "路径是：" + watchedEvent.getPath());
            try {
                getServerLists();
            } catch (Exception e) {
                e.printStackTrace();
            }
            getServerLists();
        }
    };


    @Override
    public void run(String... strings) throws Exception {
        try {
            zk = new ZooKeeper(connectString, sessionTimeOut, wh);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取服务器信息列表
     */
    @Override
    public void getServerLists() {

        //获取服务器子节点信息，监听父节点
        try {
            List<String> list = zk.getChildren(ParentNode, true);
            List<String> servers = new ArrayList<>();
            //子节点循环取值
            for (String child : list) {
                byte[] data = zk.getData(ParentNode + "/" + child, false, null);
                servers.add(new String(data));
            }

            //把servers赋值给成员变量serverList，已提供给各业务线程使用
            serverList = servers;


            //打印一下服务器列表
            System.out.println(serverList);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void client() {
        getServerLists();//获取列表

        //业务线程
        System.out.println("client start working");

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
