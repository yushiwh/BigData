package com.ys.service.queue;

import org.springframework.stereotype.Service;

/**
 * Created by yushi on 2017/3/21.
 */
@Service
public class QueueServiceImpl implements QueueService, Runnable {

    static Thread t1 = null;

    @Override
    public void product() throws Exception {
        ProducerTool producer = new ProducerTool();
        producer.produceMessage("Hello, world!");
        producer.close();

    }

    @Override
    public void consumer() throws InterruptedException {


        t1 = new Thread(new QueueServiceImpl());
        t1.start();
        while (true) {
            System.out.println(t1.isAlive());
            if (!t1.isAlive()) {
                t1 = new Thread(new QueueServiceImpl());
                t1.start();
                System.out.println("重新启动");
            }
            Thread.sleep(5000);
        }
        // 延时500毫秒之后停止接受消息
        // Thread.sleep(500);
        // consumer.close();
    }

    public void run() {
        try {
            ConsumerTool consumer = new ConsumerTool();
            consumer.consumeMessage();
            while (ConsumerTool.isconnection) {
                //System.out.println(123);
            }
        } catch (Exception e) {
        }

    }

}
