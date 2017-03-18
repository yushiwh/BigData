package com.ys.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 分布式共享锁
 * Created by yushi on 2017/3/18.
 */
@RequestMapping("/zookeeper")

@ResponseBody
public interface DistributedClientLockService {

    @RequestMapping(value = "/getLocks", method = RequestMethod.GET)
    public void getLocks();


}
