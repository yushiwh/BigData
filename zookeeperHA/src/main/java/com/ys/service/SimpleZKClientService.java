package com.ys.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * Created by yushi on 2017/3/17.
 */
@RequestMapping("/zookeeper")

@ResponseBody
public interface SimpleZKClientService {


    @RequestMapping(value = "/createNode", method = RequestMethod.GET)
    public void createNode() throws IOException;


    @RequestMapping(value = "/getChildren", method = RequestMethod.GET)
    public void getChildren() throws IOException;

    @RequestMapping(value = "/exits", method = RequestMethod.GET)
    public void exits() throws IOException;


    @RequestMapping(value = "/getdata", method = RequestMethod.GET)
    public void getData() throws IOException;

    @RequestMapping(value = "/updatedata", method = RequestMethod.GET)
    public void updateData() throws IOException;

    @RequestMapping(value = "/deldata", method = RequestMethod.GET)
    public void delData() throws IOException;
}
