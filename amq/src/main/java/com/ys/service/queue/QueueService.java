package com.ys.service.queue;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by yushi on 2017/3/21.
 */
@RequestMapping("/queue")

@ResponseBody
public interface QueueService {


    @RequestMapping(value = "/product", method = RequestMethod.GET)
    public void product() throws Exception;


    @RequestMapping(value = "/consumer", method = RequestMethod.GET)
    public void consumer() throws InterruptedException;

}
