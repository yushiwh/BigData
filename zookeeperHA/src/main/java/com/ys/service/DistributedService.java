package com.ys.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 服务器端的接口
 * Created by yushi on 2017/3/17.
 */
@RequestMapping("/zookeeper")

@ResponseBody
public interface DistributedService {

    /**
     * 注册服务
     */
    @RequestMapping(value = "/registerServer", method = RequestMethod.GET)
    public void registerServer(String hostName);


    /**
     * 业务功能
     */
    @RequestMapping(value = "/handleBussiness", method = RequestMethod.GET)
    public void handleBussiness(String hostName);


    /**
     * 统一调用的功能
     */
    @RequestMapping(value = "/server", method = RequestMethod.GET)
    public void server(String hostName);



}
