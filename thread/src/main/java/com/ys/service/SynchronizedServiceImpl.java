package com.ys.service;

import org.springframework.stereotype.Service;

/**
 * Created by yushi on 2017/3/20.
 */
@Service
public class SynchronizedServiceImpl implements SynchronizedService {

    //SynchronizedServiceImpl t1 = new SynchronizedServiceImpl();
    //SynchronizedServiceImpl t2 = new SynchronizedServiceImpl();

    String t1 = "t1";
    String t2 = "t2";

    @Override
    public void getSynchronized() {
        new Thread("thread1") {
            public void run() {
                synchronized (t1) {
                    try {
                        System.out.println(this.getName() + " start");
                      //  int i = 1 / 0;   //如果发生异常，jvm会将锁释放
                        Thread.sleep(5000);
                        System.out.println(this.getName() + "醒了");
                        System.out.println(this.getName() + " end");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.             start();

        new

                Thread("thread2") {
                    public void run() {
                        synchronized (t1) {         //争抢同一把锁时，线程1没释放之前，线程2只能等待
//					synchronized (t2) {    //如果不是一把锁，可以看到两句话同时打印
                            System.out.println(this.getName() + " start");
                            System.out.println(this.getName() + " end");

                        }
                    }
                }.
                start();
    }


}
