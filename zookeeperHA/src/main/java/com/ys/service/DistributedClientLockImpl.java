package com.ys.service;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by yushi on 2017/3/18.
 */
@Service
@Component
public class DistributedClientLockImpl implements DistributedClientLockService, CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DistributedClientLockImpl.class);
    private static final String groupNode = "servers";

    @Value("${zookeeper.address}")
    private String connectString;
    private boolean haveLock = false;

    private static final int sessionTimeOut = 3000;
    //创建ZooKeeper实例
    ZooKeeper zk;


    // 记录自己创建的子节点路径
    private volatile String thisPath;
    /**
     * 创建Watcher实例
     */
    Watcher wh = new Watcher() {
        @Override
        public void process(WatchedEvent watchedEvent) {
            System.out.println("已经触发了" + watchedEvent.getType() + "的事件" + "------" + "路径是：" + watchedEvent.getPath());
            try {
                // 判断事件类型，此处只处理子节点变化事件
                if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged && watchedEvent.getPath().equals("/" + groupNode)) {
                    //获取子节点，并对父节点进行监听
                    List<String> childrenNodes = zk.getChildren("/" + groupNode, true);
                    String thisNode = thisPath.substring(("/" + groupNode + "/").length());
                    // 去比较是否自己是最小id
                    Collections.sort(childrenNodes);
                    if (childrenNodes.indexOf(thisNode) == 0) {
                        //访问共享资源处理业务，并且在处理完成之后删除锁
                        doSomething();

                        //重新注册一把新的锁
                        thisPath = zk.create("/" + groupNode, null, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                                CreateMode.EPHEMERAL_SEQUENTIAL);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    @Override
    public void run(String... strings) throws Exception {
        try {
            zk = new ZooKeeper(connectString, sessionTimeOut, wh);
            // 1、程序一进来就先注册一把锁到zk上
            thisPath = zk.create("/" + groupNode, "qq".getBytes("UTF-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);

            // wait一小会，便于观察
            Thread.sleep(new Random().nextInt(1000));

            // 从zk的锁父目录下，获取所有子节点，并且注册对父节点的监听
            List<String> childrenNodes = zk.getChildren("/" + groupNode, true);

            //如果争抢资源的程序就只有自己，则可以直接去访问共享资源
            if (childrenNodes.size() == 1) {
                doSomething();
                thisPath = zk.create("/" + groupNode, "qq".getBytes("UTF-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.EPHEMERAL_SEQUENTIAL);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 处理业务逻辑，并且在最后释放锁
     */
    private void doSomething() throws Exception {
        try {
            System.out.println("gain lock: " + thisPath);
            Thread.sleep(2000);
            // do something
        } finally {
            System.out.println("finished: " + thisPath);
            //删除锁
            zk.delete(this.thisPath, -1);
        }
    }


    @Override
    public void getLocks() {
        try {

            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
