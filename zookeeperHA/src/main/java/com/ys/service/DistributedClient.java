package com.ys.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 这是客户端
 * Created by yushi on 2017/3/17.
 */
@RequestMapping("/zookeeper")

@ResponseBody
public interface DistributedClient {
    /**
     * 获取servers的子节点信息，从中获取服务器信息列表
     */

    @RequestMapping(value = "/getServerLists", method = RequestMethod.GET)
    public void getServerLists();

    @RequestMapping(value = "/client", method = RequestMethod.GET)
    public void client();
}
