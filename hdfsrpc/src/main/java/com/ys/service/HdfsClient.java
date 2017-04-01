package com.ys.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by yushi on 2017/3/27.
 */
@RequestMapping("/hdfs")

@ResponseBody
public interface HdfsClient {
    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public void init() throws IOException, URISyntaxException, InterruptedException;


    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public void upload() throws IOException;

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void download() throws Exception;

    @RequestMapping(value = "/conf", method = RequestMethod.GET)
    public void conf();


    @RequestMapping(value = "/makdir", method = RequestMethod.GET)
    public void makdir() throws Exception;

    /**
     * 删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public void delete() throws Exception;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public void list() throws Exception;

}
