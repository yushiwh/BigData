package com.ys.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.ExecutionException;

/**
 * 线程池
 * <p>
 * Created by yushi on 2017/3/20.
 */
@RequestMapping("/pool")
@ResponseBody
public interface ThreadPoolService {


    //实现runnable
    @RequestMapping(value = "/base", method = RequestMethod.GET)
    public void base();

    //实现callable
    @RequestMapping(value = "/callable", method = RequestMethod.GET)
    public void callable() throws ExecutionException, InterruptedException;

    //实现callable,打印线程返回值
    @RequestMapping(value = "/callableback", method = RequestMethod.GET)
    public void callableback() throws ExecutionException, InterruptedException;

}
