package com.ys.service.topic;

import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Created by yushi on 2017/3/21.
 */
@Service
public class TopicServiceImpl implements TopicService, Runnable {
    static Thread t1 = null;

    @Override
    public void product() throws Exception {
        ProducerTool producer = new ProducerTool();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {

            Thread.sleep(random.nextInt(10) * 1000);

            producer.produceMessage("Hello, world!--" + i);
            producer.close();
        }
    }

    @Override
    public void consumer() throws InterruptedException {
        t1 = new Thread(new TopicServiceImpl());
        t1.setDaemon(false);
        t1.start();
        /**
         * 如果发生异常，则重启consumer
         */
        /*while (true) {
            System.out.println(t1.isAlive());
			if (!t1.isAlive()) {
				t1 = new Thread(new ConsumerTest());
				t1.start();
				System.out.println("重新启动");
			}
			Thread.sleep(5000);
		}*/
        // 延时500毫秒之后停止接受消息
        // Thread.sleep(500);
        // consumer.close();
    }

    public void run() {
        try {
            ConsumerTool consumer = new ConsumerTool();
            consumer.consumeMessage();
            while (ConsumerTool.isconnection) {
            }
        } catch (Exception e) {
        }

    }

}
