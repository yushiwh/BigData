package com.ys.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by yushi on 2017/3/20.
 */
@Service
public class ThreadPoolServiceImpl implements ThreadPoolService {
    /**
     * 基本的线程池提交
     */
    @Override
    public void base() {
        //创建一个线程池
        ExecutorService pool = Executors.newCachedThreadPool();
        for (int i = 1; i < 5; i++) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("thread name: " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        pool.shutdown();
    }

    /**
     * * callable 跟runnable的区别：
     * runnable的run方法不会有任何返回结果，所以主线程无法获得任务线程的返回值
     * <p>
     * callable的call方法可以返回结果，但是主线程在获取时是被阻塞，需要等待任务线程返回才能拿到结果
     */
    @Override
    public void callable() throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 10; i++) {
            Future<String> submit = pool.submit(new Callable<String>() {//提交任务
                @Override
                public String call() throws Exception {
                    //System.out.println("a");
                    Thread.sleep(5000);
                    return "b--" + Thread.currentThread().getName();
                }
            });
            //从Future中get结果，这个方法是会被阻塞的，一直要等到线程任务返回结果
            System.out.println(submit.get());
        }
        pool.shutdown();
    }

    @Override
    public void callableback() throws ExecutionException, InterruptedException {
        Future<?> submit = null;
        Random random = new Random();

        //创建固定数量线程池
//		ExecutorService exec = Executors.newFixedThreadPool(4);

        //创建调度线程池
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(4);

        //用来记录各线程的返回结果
        ArrayList<Future<?>> results = new ArrayList<Future<?>>();

        for (int i = 0; i < 10; i++) {
            //fixedPool提交线程，runnable无返回值，callable有返回值
            /*submit = exec.submit(new TaskRunnable(i));*/
            /*submit = exec.submit(new TaskCallable(i));*/

            //对于schedulerPool来说，调用submit提交任务时，跟普通pool效果一致
			/*submit = exec.submit(new TaskCallable(i));*/
            //对于schedulerPool来说，调用schedule提交任务时，则可按延迟，按间隔时长来调度线程的运行
            submit = exec.schedule(new TaskCallable(i), random.nextInt(10), TimeUnit.SECONDS);
            //存储线程执行结果
            results.add(submit);

        }


        //打印结果
        for (Future f : results) {
            boolean done = f.isDone();
            System.out.println(done ? "已完成" : "未完成");  //从结果的打印顺序可以看到，即使未完成，也会阻塞等待
            System.out.println("线程返回future结果： " + f.get());
        }

        exec.shutdown();
    }
}
