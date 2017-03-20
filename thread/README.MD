这是thread线程的例子
1、继承Thread的方法开启线程
     thread1.start();正确的开启方法   主线程执行完成不影响线程的执行  交替执行
     thread1.run();  则是普通方法的调用，没有开启多线程



2、实现runnable方法
    同上
    注意线程new是Thread t1 = new Thread(new ThreadServiceImpl(), "thread1");

3、synchronized锁
    1、拿到所得线程发生异常，立即释放锁
    2、如果争抢同意吧所，线程一没有释放，线程二必须等待。不是同一把锁，则没有相互关联

4、Lock    可以不用一直等待锁
    1、synchronized锁不用手动释放，lock要手动释放


Lock和synchronized的选择
　　
　　1）Lock是一个接口，而synchronized是Java中的关键字，synchronized是内置的语言实现；
　　2）synchronized在发生异常时，会自动释放线程占有的锁，因此不会导致死锁现象发生；
      而Lock在发生异常时，如果没有主动通过unLock()去释放锁，则很可能造成死锁现象，因此使用Lock时需要在finally块中释放锁；
　　3）Lock可以让等待锁的线程响应中断，而synchronized却不行，使用synchronized时，等待的线程会一直等待下去，不能够响应中断；
　　4）通过Lock可以知道有没有成功获取锁，而synchronized却无法办到。
　　5）Lock可以提高多个线程进行读操作的效率。
　　在性能上来说，如果竞争资源不激烈，两者的性能是差不多的，而当竞争资源非常激烈时（即有大量线程同时竞争），此时Lock的性能要远远优于synchronized。所以说，在具体使用时要根据适当情况选择。

