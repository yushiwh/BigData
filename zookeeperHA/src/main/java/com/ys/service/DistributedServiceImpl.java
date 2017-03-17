package com.ys.service;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by yushi on 2017/3/17.
 */
@Service
@Component
public class DistributedServiceImpl implements DistributedService, CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DistributedServiceImpl.class);
    private static final String ParentNode = "/servers";


    @Value("${zookeeper.address}")
    private String connectString;


    private DistributedClient distributedClient;


    private static final int sessionTimeOut = 3000;


    //创建ZooKeeper实例
    ZooKeeper zkServer;

    //创建客户端ZooKeeper实例
    ZooKeeper zkClient;

    /**
     * 创建Watcher实例
     */
    Watcher whServer = new Watcher() {
        @Override
        public void process(WatchedEvent watchedEvent) {
            System.out.println("已经触发了" + watchedEvent.getType() + "的事件" + "------" + "路径是：" + watchedEvent.getPath());
            try {
                zkServer.getChildren("/", true);//不停的监听
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public void run(String... strings) throws Exception {
        try {
            zkServer = new ZooKeeper(connectString, sessionTimeOut, whServer);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 向zk集群注册信息
     *
     * @param hostName
     */
    @Override
    public void registerServer(String hostName) {

        try {

            String create = zkServer.create(ParentNode + "/servers", hostName.getBytes("UTF-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);//带序号的临时节点
            logger.info(hostName + "is on line" + create);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理业务
     *
     * @param hostName
     */
    @Override
    public void handleBussiness(String hostName) {
        System.out.println(hostName + " start woking....");
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void server(String hostName) {
        //先进行注册
        registerServer(hostName);

        //再进行业务处理

        handleBussiness(hostName);

    }


}
