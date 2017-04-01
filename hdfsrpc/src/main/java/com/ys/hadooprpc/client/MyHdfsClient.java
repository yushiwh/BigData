package com.ys.hadooprpc.client;

import com.ys.hadooprpc.protocol.ClientNamenodeProtocol;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import java.net.InetSocketAddress;


public class MyHdfsClient {

	public static void main(String[] args) throws Exception {
		ClientNamenodeProtocol namenode = RPC.getProxy(ClientNamenodeProtocol.class, 1L,
				new InetSocketAddress("localhost", 8888), new Configuration());
		String metaData = namenode.getMetaData("/angela.mygirl");
		System.out.println(metaData);
	}

}
