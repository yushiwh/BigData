package com.ys.service;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Created by yushi on 2017/3/17.
 */
@Service
@Component
public class SimpleZKClientServiceImpl implements SimpleZKClientService, CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(SimpleZKClientServiceImpl.class);

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
                zk.getChildren("/", true);//不停的监听
            } catch (Exception e) {
                e.printStackTrace();
            }

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


    /////下面是数据的增删改查///////
    @Override
    public void createNode() {

        String create = "";
        try {

            /// 创建 ZooKeeper 节点 (znode ： zoo2, 数据： myData2 ，权限： OPEN_ACL_UNSAFE ，节点类型： Persistent
            zk.create("/yushi", "hello zk".getBytes("UTF-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            logger.info("创建节点成功");
        } catch (Exception e) {
            logger.info("创建节点失败：" + e);
        }

    }

    @Override
    public void getChildren() {

        try {

            List<String> list = zk.getChildren("/", true);
            for (String str : list) {
                logger.info("获取的节点：" + str);
            }

            Thread.sleep(Long.MAX_VALUE);//休眠，为了监听节点的变化

        } catch (Exception e) {
            logger.info("获取节点失败：" + e);
        }
    }

    @Override
    public void exits() {
        try {
            Stat stat = zk.exists("/yushi", false);
            if (null != stat) {
                logger.info("节点存在");
            } else {
                logger.info("节点不存在");
            }

        } catch (Exception e) {
            logger.info("出错了：" + e);
        }
    }

    @Override
    public void getData() {
        try {
            byte[] data = zk.getData("/yushi", false, null);
            if (null != data) {
                logger.info("数据存在：" + new String(data, "UTF-8"));
            } else {
                logger.info("数据不存在");
            }

        } catch (Exception e) {
            logger.info("出错了：" + e);
        }
    }

    @Override
    public void delData() {
        try {
            //-1表示删除所有版本
            zk.delete("/yushi", -1);
            logger.info("数据已经删除");

        } catch (Exception e) {
            logger.info("出错了：" + e);
        }
    }


    @Override
    public void updateData() {
        try {

            zk.setData("/yushi", "哈哈".getBytes("UTF-8"), -1);
            logger.info("数据已经更新");
            byte[] data = zk.getData("/yushi", false, null);
            if (null != data) {
                logger.info("数据存在：" + new String(data, "UTF-8"));
            } else {
                logger.info("数据不存在");
            }

        } catch (Exception e) {
            logger.info("出错了：" + e);
        }
    }

}
