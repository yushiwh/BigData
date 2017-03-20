package com.ys.service;

import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Created by yushi on 2017/3/20.
 */
@Service
public class ThreadServiceImpl extends Thread implements ThreadService {


    @Override
    public void run() {
        String tname = Thread.currentThread().getName();
        System.out.println(tname + "被调用");

        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            try {
                Thread.sleep(random.nextInt(10) * 500);
                System.out.println(tname );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * start，正确开启多线程的方法
     * 交替执行
     * 主线程执行完成不影响线程的执行
     * Thread-4被调用
     Thread-5被调用
     Thread-5
     Thread-5
     Thread-4
     Thread-4
     Thread-5
     Thread-4
     Thread-5
     */
    @Override
    public void getthread() {
        ThreadServiceImpl thread1 = new ThreadServiceImpl();
        ThreadServiceImpl thread2 = new ThreadServiceImpl();

        thread1.start();
        thread2.start();
    }

    /**
     * 如果是run方法，则是普通方法的调用，没有开启多线程
     * http-nio-8080-exec-2
     http-nio-8080-exec-2
     http-nio-8080-exec-2
     http-nio-8080-exec-2

     */
    @Override
    public void getrin() {
        ThreadServiceImpl thread1 = new ThreadServiceImpl();
        ThreadServiceImpl thread2 = new ThreadServiceImpl();

        thread1.run();
        thread2.run();
    }
}
