这是mapreducer的例子，主要通过的是一个统计单词的例子进行演示

如果出现这样的异常：
org.apache.hadoop.security.AccessControlException
暂时在/home/hadoop/apps/hadoop-2.7.3/etc/hadoop/hdfs-site.xml修改增加一段话

<property>
<name>dfs.replication</name>
<value>2</value>

<name>dfs.permissions</name>
    <value>false</value>
    <description>
     去掉权限的验证
    </description>

</property>


运行成功后会生成一个文件，查看这个文件
[hadoop@mini130 ~]$ hadoop fs -cat /wordcount/output8/part-r-00000
asdasfqewewqeewqewqew	1
asdfghjkl	1
fsafegerjhrjrytte	1
qwertyuiop	1
zxcvbnm,..	1
[hadoop@mini130 ~]$




----自定义Partitioner中需要统计电话号码的区段的log文件
1360000000	1360000000	dadadas1	127.0.0.1	15	91	1918	8881	2001
1370000000	1370000000	dadadas2	127.0.0.2	25	92	2918	8882	2002
1380000000	1380000000	dadadas3	127.0.0.3	35	93	3918	8883	2003
1390000000	1390000000	dadadas4	127.0.0.4	45	94	4918	8884	2004
1530000000	1530000000	dadadas5	127.0.0.5	55	95	5918	8885	2005
1320000000	1320000000	dadadas6	127.0.0.6	65	96	6918	8886	2006
1310000000	1310000000	dadadas7	127.0.0.7	75	97	7918	8887	2007






----有一个两表连接查询的例子
order
1001	20150710	P0001	2
1002	20150710	P0001	3
1002	20150710	P0002	3
1003	20150710	P0003	3

product
P0001	小米5	1001	2
P0002	锤子T1	1000	3
P0003	锤子	1002	4


-------------分词倒序排列
hello world
hello tom
hello yushi


hello qwer
hello sdfg
hello xcvb


hello uiop
hello s
hello cc

----寻找共同的QQ好友
社交粉丝数据分析
以下是qq的好友列表数据，冒号前是一个用，冒号后是该用户的所有好友（数据中的好友关系是单向的）
A:B,C,D,F,E,O
B:A,C,E,K
C:F,A,D,I
D:A,E,F,L
E:B,C,D,M,L
F:A,B,C,D,E,O,M
G:A,C,D,E,F
H:A,C,D,E,O
I:A,O
J:B,O
K:A,C,D
L:D,E,F
M:E,F,G
O:A,H,I,J

求出哪些人两两之间有共同好友，及他俩的共同好友都有谁？



-----清洗日志文件


----计算最大值的商品  需要自定义groupingcomparator
订单id	       商品id	成交金额
Order_0000001	Pdt_01	222.8
Order_0000001	Pdt_05	25.8
Order_0000002	Pdt_03	522.8
Order_0000002	Pdt_04	122.4
Order_0000002	Pdt_05	722.4
Order_0000003	Pdt_01	222.8

现在需要求出每一个订单中成交金额最大的一笔交易


--注：所需要的例子文件都在file中


--------一个痛苦的解决问题的过程--------------------------------------
--------------搞定一个问题----
windows里面的eclipse访问linux的yarn集群会出现的错误
1、权限问题
   没有linux的权限，需要在启动的配置参数中加上指定的用户，如在debug的配置中：HADOOP_USER_NAME=hadoop

2、/bin/bash: line 0: fg: no job control一般解决方法
   这个问题是hadoop的bug
   具体可见http://blog.csdn.net/fansy1990/article/details/27526167
   需要重新写YARNRunner.java,具体的本项目中已有

3、出现Stack trace: ExitCodeException exitCode=1:
http://www.cnblogs.com/gaoxing/p/4466924.html
   要把配置的四个文件放到src目录下面
   core-site.xml
   hdfs-site.xml
   mapred-site.xml
   yarn-site.xml
   注意如果要使用如 mini130 这样的，需要在hosts中进行配置
   yarn.application.classpath
   mapreduce.application.classpath都需要编写绝对地址


