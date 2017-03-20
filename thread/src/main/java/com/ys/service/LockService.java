package com.ys.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by yushi on 2017/3/20.
 */
@RequestMapping("/lock")

@ResponseBody
public interface LockService {

    //Lock的基本用法
    @RequestMapping(value = "/base", method = RequestMethod.GET)
    public void getBase();


    //TryLock的用法
    @RequestMapping(value = "/trylock", method = RequestMethod.GET)
    public void getTryLock();

    //Interruptibly的用法
    @RequestMapping(value = "/interruptibly", method = RequestMethod.GET)
    public void interruptibly();

    //读写锁的用法
    @RequestMapping(value = "/reentrantReadWriteLock", method = RequestMethod.GET)
    public void reentrantReadWriteLock();



}
