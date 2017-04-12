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




