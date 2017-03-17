这是实现分布式应用的(主节点HA)及客户端动态更新主节点状态
某分布式系统中，主节点可以有多台，可以动态上下线
任意一台客户端都能实时感知到主节点服务器的上下线



通过修改application.properties中的server.port进行端口的修改启动

http://localhost:8081/zookeeper/server?hostName=001  上线服务器001

http://localhost:8082/zookeeper/server?hostName=002  上线服务器002

查看linux中的zookeeper可以看到上线的服务器数量
[zk: localhost:2181(CONNECTED) 38] ls /servers
[servers0000000006, servers0000000007]


http://localhost:8083/zookeeper/client   上线客户端
服务端和客户端均会打印出
[002, 001]
client start working


再上线一台服务器
http://localhost:8084/zookeeper/server?hostName=004  上线服务器004


此时控制台出现
已经触发了NodeChildrenChanged的事件------路径是：/servers
[002, 003, 001]

关闭掉1的服务器
打印出
已经触发了NodeChildrenChanged的事件------路径是：/servers
[002, 003]

这样就实现分布式应用的(主节点HA)及客户端动态更新主节点状态