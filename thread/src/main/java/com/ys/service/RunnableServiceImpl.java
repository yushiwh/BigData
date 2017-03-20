package com.ys.service;

import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Created by yushi on 2017/3/20.
 */
@Service
public class RunnableServiceImpl implements RunnableService, Runnable {
    @Override
    public void getthread() {


        Thread t1 = new Thread(new ThreadServiceImpl(), "thread1");
        Thread t2 = new Thread(new ThreadServiceImpl(), "thread2");

        //多线程的正确方式
        //t1.start();
        //t2.start();

        //run是主线程中进行调用，没有启动多线程
        t1.run();
        t2.run();


    }

    @Override
    public void run() {
        String tname = Thread.currentThread().getName();
        System.out.println(tname + "被调用");

        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            try {
                Thread.sleep(random.nextInt(10) * 500);
                System.out.println(tname);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
