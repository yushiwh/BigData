package com.ys.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by yushi on 2017/3/20.
 */
@RequestMapping("/thread")

@ResponseBody
public interface ThreadService {

    //继承Thread方法,start
    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public void getthread();

    //继承Thread方法,run
    @RequestMapping(value = "/run", method = RequestMethod.GET)
    public void getrin();

}
