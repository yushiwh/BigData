package com.ys.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by yushi on 2017/3/20.
 */
@Service
public class LockServiceImpl implements LockService {


    private static ArrayList<Integer> arrayList = new ArrayList<Integer>();
    static Lock lock = new ReentrantLock(); // 注意这个地方
    private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();


    @Override
    public void getBase() {
        new Thread() {
            public void run() {
                Thread thread = Thread.currentThread();

                lock.lock();
                try {
                    System.out.println(thread.getName() + "得到了锁");
                    for (int i = 0; i < 5; i++) {
                        arrayList.add(i);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                } finally {
                    System.out.println(thread.getName() + "释放了锁");
                    lock.unlock();
                }

            }

            ;
        }.start();

        new Thread() {
            public void run() {
                Thread thread = Thread.currentThread();
                lock.lock();
                try {
                    System.out.println(thread.getName() + "得到了锁");
                    for (int i = 0; i < 5; i++) {
                        arrayList.add(i);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                } finally {
                    System.out.println(thread.getName() + "释放了锁");
                    lock.unlock();
                }

            }

            ;
        }.start();
    }

    /**
     * tryLock
     */
    @Override
    public void getTryLock() {
        new Thread() {
            public void run() {
                Thread thread = Thread.currentThread();
                boolean tryLock = lock.tryLock();//尝试获取锁
                System.out.println(thread.getName() + " " + tryLock);
                if (tryLock) {
                    try {
                        System.out.println(thread.getName() + "得到了锁");
                        for (int i = 0; i < 5; i++) {
                            arrayList.add(i);
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    } finally {
                        System.out.println(thread.getName() + "释放了锁");
                        lock.unlock();
                    }
                }
            }

            ;
        }.start();

        new Thread() {
            public void run() {
                Thread thread = Thread.currentThread();
                boolean tryLock = lock.tryLock();
                System.out.println(thread.getName() + " " + tryLock);
                if (tryLock) {
                    try {
                        System.out.println(thread.getName() + "得到了锁");
                        for (int i = 0; i < 5; i++) {
                            arrayList.add(i);
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    } finally {
                        System.out.println(thread.getName() + "释放了锁");
                        lock.unlock();
                    }
                }

            }

            ;
        }.start();
    }

    /**
     * 中断
     * 观察现象：如果thread-0得到了锁，阻塞。。。thread-1尝试获取锁，如果拿不到，则可以被中断等待
     */
    @Override
    public void interruptibly() {
        LockServiceImpl test = new LockServiceImpl();
        MyThread thread0 = new MyThread(test);
        MyThread thread1 = new MyThread(test);
        thread0.start();
        thread1.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread1.interrupt();//中断
        System.out.println("=====================");
    }

    @Override
    public void reentrantReadWriteLock() {
        final LockServiceImpl test = new LockServiceImpl();

        new Thread() {
            public void run() {
                test.get(Thread.currentThread());
                test.write(Thread.currentThread());
            }

            ;
        }.start();

        new Thread() {
            public void run() {
                test.get(Thread.currentThread());
                test.write(Thread.currentThread());
            }

            ;
        }.start();

    }


    public void insert(Thread thread) throws InterruptedException {
        lock.lockInterruptibly();   //注意，如果需要正确中断等待锁的线程，必须将获取锁放在外面，然后将InterruptedException抛出
        try {
            System.out.println(thread.getName() + "得到了锁");
            long startTime = System.currentTimeMillis();
            for (; ; ) {
                if (System.currentTimeMillis() - startTime >= Integer.MAX_VALUE)
                    break;
                //插入数据
            }
        } finally {
            System.out.println(Thread.currentThread().getName() + "执行finally");
            lock.unlock();
            System.out.println(thread.getName() + "释放了锁");
        }
    }

    /**
     * 读操作,用读锁来锁定
     *
     * @param thread
     */
    public void get(Thread thread) {
        rwl.readLock().lock();
        try {
            long start = System.currentTimeMillis();

            while (System.currentTimeMillis() - start <= 1) {
                System.out.println(thread.getName() + "正在进行读操作");
            }
            System.out.println(thread.getName() + "读操作完毕");
        } finally {
            rwl.readLock().unlock();
        }
    }

    /**
     * 写操作，用写锁来锁定
     *
     * @param thread
     */
    public void write(Thread thread) {
        rwl.writeLock().lock();
        ;
        try {
            long start = System.currentTimeMillis();

            while (System.currentTimeMillis() - start <= 1) {
                System.out.println(thread.getName() + "正在进行写操作");
            }
            System.out.println(thread.getName() + "写操作完毕");
        } finally {
            rwl.writeLock().unlock();
        }
    }


}

class MyThread extends Thread {
    private LockServiceImpl test = null;

    public MyThread(LockServiceImpl test) {
        this.test = test;
    }

    @Override
    public void run() {

        try {
            test.insert(Thread.currentThread());
        } catch (Exception e) {
            System.out.println(Thread.currentThread().getName() + "被中断");
        }
    }

}