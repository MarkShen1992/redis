# Redis学习笔记

## Redis起源

Redis是存东西的。那么在计算机的发展过程中，一开始是使用文件存储数据，那么这时候在文件中查询数据该怎么做呢？有很多种方式，像 Linux 中的 grep, awk; Java语言中io流来读文件。再补充常识内容，文件存储在硬盘上，硬盘的常识：第一，**寻址**(ms)；第二，**带宽**(吞吐量：一秒钟可以从硬盘读多少数据到内存，机械硬盘跟接口，协议)。内存寻址时间(ns)。当程序调数据到CPU中计算的时候，磁盘 -> 内存 -> CPU. 

案例：系统日志，当日志文件越来越大的时候，我们如果使用Java来读取文件的时候，读完整个文件所花费的时间也会越来越长，为什么会是这个样子呢？因为程序会发起对日志文件**全量IO：受硬件影响**的行为。

为了解决这个问题，数据库技术推出。用数据库查询数据速度要读单独读文件要快，为什么？除了数据库中一些经典的数据结构和算法，索引等使得查询变快。还有数据中用到了 datapage:4KB 这个内容，数据库中会有很多很多大小为 4KB的 datapage 用来存储数据。**分治思想**。固态硬盘，4k对齐。数据库建表必须给出schema. 数据类型表示所占内存空间小。**建索引**。索引也是数据，也有datapage。数据库中的操作有两类操作。读(Select),写(insert, update, delete)。在数据库写操作的时候，写操作所花时间一定会随文件的变大而变长。读操作还可以，但是在高并发和复杂查询的时候，会有问题。

SAP HANA基于内存的关系型数据库。但是会花费很昂贵的成本。为了解决当数据量太大时，数据库读取数据太慢和基于内存的关系型数据库太贵的问题。我们可以采用把比较热的数据放到比较廉价，数据访问比较快的内存数据库中, Redis就是这样一个产品，还有memcache内存数据库。并没有放全量数据。内存数据库为什么时K,V型的？

[DB Engine official site](https://db-engines.com/en/)

## Redis安装

step 1: 在 [redis官网](https://redis.io/)上下载

```
wget http://download.redis.io/releases/redis-5.0.7.tar.gz
```

step 2: 解压软件 

```
tar xf redis-5.0.7.tar.gz
[root@localhost redis-5.0.7]# ll
总用量 272
-rw-rw-r--.  1 root root 115100 11月 20 01:05 00-RELEASENOTES
-rw-rw-r--.  1 root root     53 11月 20 01:05 BUGS
-rw-rw-r--.  1 root root   2381 11月 20 01:05 CONTRIBUTING
-rw-rw-r--.  1 root root   1487 11月 20 01:05 COPYING
drwxrwxr-x.  6 root root    124 11月 20 01:05 deps
-rw-rw-r--.  1 root root     11 11月 20 01:05 INSTALL
-rw-rw-r--.  1 root root    151 11月 20 01:05 Makefile
-rw-rw-r--.  1 root root   6888 11月 20 01:05 MANIFESTO
-rw-rw-r--.  1 root root  20555 11月 20 01:05 README.md
-rw-rw-r--.  1 root root  61797 11月 20 01:05 redis.conf
-rwxrwxr-x.  1 root root    275 11月 20 01:05 runtest
-rwxrwxr-x.  1 root root    280 11月 20 01:05 runtest-cluster
-rwxrwxr-x.  1 root root    373 11月 20 01:05 runtest-moduleapi
-rwxrwxr-x.  1 root root    281 11月 20 01:05 runtest-sentinel
-rw-rw-r--.  1 root root   9710 11月 20 01:05 sentinel.conf
drwxrwxr-x.  3 root root   4096 11月 20 01:05 src
drwxrwxr-x. 11 root root    182 11月 20 01:05 tests
drwxrwxr-x.  8 root root   4096 11月 20 01:05 utils
```

step 3: 在开源软件中有 README.md 文件，这里面会介绍如何安装 redis。

```
./configure -> Makefile -> exe
               make        make install
make
```

step 4:  将 redis 安装到指定路径

```
make PREFIX=/usr/local/redis5 install
```

step 5: 配置环境变量在 `/etc/profile` 

```
export REDIS_HOME=/usr/local/redis5/bin
export PATH=$REDIS_HOME:$PATH
```

step 6: 精灵进程安装

```
cd utils
./install_server.sh
Welcome to the redis service installer
This script will help you easily set up a running redis server

Please select the redis port for this instance: [6379]
Selecting default: 6379
Please select the redis config file name [/etc/redis/6379.conf]
Selected default - /etc/redis/6379.conf
Please select the redis log file name [/var/log/redis_6379.log]
Selected default - /var/log/redis_6379.log
Please select the data directory for this instance [/var/lib/redis/6379]
Selected default - /var/lib/redis/6379
Please select the redis executable path [/usr/local/redis5/bin/redis-server]
Selected config:
Port           : 6379
Config file    : /etc/redis/6379.conf
Log file       : /var/log/redis_6379.log
Data dir       : /var/lib/redis/6379
Executable     : /usr/local/redis5/bin/redis-server
Cli Executable : /usr/local/redis5/bin/redis-cli
Is this ok? Then press ENTER to go on or Ctrl-C to abort.
Copied /tmp/6379.conf => /etc/init.d/redis_6379  // /etc/init.d/ 后台服务脚本目录
Installing service...
Successfully added to chkconfig!  // chkconfig -> 设置开机启动的
Successfully added to runlevels 345!  // 启动级别 345
Starting Redis server...
Installation successful!

cd /etc/init.d && ls
functions  netconsole  network  README  redis_6379

redis_6379 是服务名称
service redis_6379 status // 可以看看 redis_6379 这个 shell 脚本的内容
```

## Redis线程模型(业务处理时是单线程)

```shell
# strace 命令抓取 进程运行时 对底层调用的日志
yum install strace
man strace 查看命令手册

strace -ff -o ~/stracedir/ooxx ./redis-server
# Linux 内核提供了一种通过 /proc 文件系统，在运行时访问内核内部数据结构、改变内核设置的机制。proc文件系统是一个伪文件系统，它只存在内存当中，而不占用外存空间。它以文件系统的方式为访问系统内核数据的操作提供接口。
cd /proc/
```

## IO发展历史

BIO -> NIO -> **多路复用**IO -> [epoll]()

```
yum install man man-pages
man man
```

用户态 - 内核态 切换

## Linux 系统调用分类

## [微服务架构四大设计原则](https://www.cnblogs.com/guanghe/p/10978349.html)

## 二进制安全

redis, zookeeper, kafka, HBase

```
Redis is an open source (BSD licensed), in-memory data structure store, used as a database, cache and message broker. It supports data structures such as strings, hashes, lists, sets, sorted sets with range queries, bitmaps, hyperloglogs, geospatial indexes with radius queries and streams. Redis has built-in replication, Lua scripting, LRU eviction, transactions and different levels of on-disk persistence, and provides high availability via Redis Sentinel and automatic partitioning with Redis Cluster. 
```

## 五大数据结构

```
利用 help
redis-cli 5.0.7
To get help about Redis commands type:
      "help @<group>" to get a list of commands in <group>
      "help <command>" for help on <command>
      "help <tab>" to get a list of possible help topics
      "quit" to exit

To set redis-cli preferences:
      ":set hints" enable online hints
      ":set nohints" disable online hints
Set your preferences in ~/.redisclirc

# 清屏
clear
help @ + tab

# string 类型方法
127.0.0.1:6379> help @string

  APPEND key value
  summary: Append a value to a key
  since: 2.0.0

  BITCOUNT key [start end]
  summary: Count set bits in a string
  since: 2.6.0

  BITFIELD key [GET type offset] [SET type offset value] [INCRBY type offset increment] [OVERFLOW WRAP|SAT|FAIL]
  summary: Perform arbitrary bitfield integer operations on strings
  since: 3.2.0

  BITOP operation destkey key [key ...]
  summary: Perform bitwise operations between strings
  since: 2.6.0

  BITPOS key bit [start] [end]
  summary: Find first bit set or clear in a string
  since: 2.8.7

  DECR key
  summary: Decrement the integer value of a key by one
  since: 1.0.0

  DECRBY key decrement
  summary: Decrement the integer value of a key by the given number
  since: 1.0.0

  GET key
  summary: Get the value of a key
  since: 1.0.0

  GETBIT key offset
  summary: Returns the bit value at offset in the string value stored at key
  since: 2.2.0

  GETRANGE key start end
  summary: Get a substring of the string stored at a key
  since: 2.4.0

  GETSET key value
  summary: Set the string value of a key and return its old value
  since: 1.0.0

  INCR key
  summary: Increment the integer value of a key by one
  since: 1.0.0

  INCRBY key increment
  summary: Increment the integer value of a key by the given amount
  since: 1.0.0

  INCRBYFLOAT key increment
  summary: Increment the float value of a key by the given amount
  since: 2.6.0

  MGET key [key ...]
  summary: Get the values of all the given keys
  since: 1.0.0

  MSET key value [key value ...]
  summary: Set multiple keys to multiple values
  since: 1.0.1

  MSETNX key value [key value ...]
  summary: Set multiple keys to multiple values, only if none of the keys exist
  since: 1.0.1

  PSETEX key milliseconds value
  summary: Set the value and expiration in milliseconds of a key
  since: 2.6.0

  SET key value [expiration EX seconds|PX milliseconds] [NX|XX]
  summary: Set the string value of a key
  since: 1.0.0

  SETBIT key offset value
  summary: Sets or clears the bit at offset in the string value stored at key
  since: 2.2.0

  SETEX key seconds value
  summary: Set the value and expiration of a key
  since: 2.0.0

  SETNX key value
  summary: Set the value of a key, only if the key does not exist
  since: 1.0.0

  SETRANGE key offset value
  summary: Overwrite part of a string at key starting at the specified offset
  since: 2.2.0

  STRLEN key
  summary: Get the length of the value stored in a key
  since: 2.2.0

# list
127.0.0.1:6379> help @list

  BLPOP key [key ...] timeout
  summary: Remove and get the first element in a list, or block until one is available
  since: 2.0.0

  BRPOP key [key ...] timeout
  summary: Remove and get the last element in a list, or block until one is available
  since: 2.0.0

  BRPOPLPUSH source destination timeout
  summary: Pop a value from a list, push it to another list and return it; or block until one is available
  since: 2.2.0

  LINDEX key index
  summary: Get an element from a list by its index
  since: 1.0.0

  LINSERT key BEFORE|AFTER pivot value
  summary: Insert an element before or after another element in a list
  since: 2.2.0

  LLEN key
  summary: Get the length of a list
  since: 1.0.0

  LPOP key
  summary: Remove and get the first element in a list
  since: 1.0.0

  LPUSH key value [value ...]
  summary: Prepend one or multiple values to a list
  since: 1.0.0

  LPUSHX key value
  summary: Prepend a value to a list, only if the list exists
  since: 2.2.0

  LRANGE key start stop
  summary: Get a range of elements from a list
  since: 1.0.0

  LREM key count value
  summary: Remove elements from a list
  since: 1.0.0

  LSET key index value
  summary: Set the value of an element in a list by its index
  since: 1.0.0

  LTRIM key start stop
  summary: Trim a list to the specified range
  since: 1.0.0

  RPOP key
  summary: Remove and get the last element in a list
  since: 1.0.0

  RPOPLPUSH source destination
  summary: Remove the last element in a list, prepend it to another list and return it
  since: 1.2.0

  RPUSH key value [value ...]
  summary: Append one or multiple values to a list
  since: 1.0.0

  RPUSHX key value
  summary: Append a value to a list, only if the list exists
  since: 2.2.0

# set
127.0.0.1:6379> help @set

  SADD key member [member ...]
  summary: Add one or more members to a set
  since: 1.0.0

  SCARD key
  summary: Get the number of members in a set
  since: 1.0.0

  SDIFF key [key ...]
  summary: Subtract multiple sets
  since: 1.0.0

  SDIFFSTORE destination key [key ...]
  summary: Subtract multiple sets and store the resulting set in a key
  since: 1.0.0

  SINTER key [key ...]
  summary: Intersect multiple sets
  since: 1.0.0

  SINTERSTORE destination key [key ...]
  summary: Intersect multiple sets and store the resulting set in a key
  since: 1.0.0

  SISMEMBER key member
  summary: Determine if a given value is a member of a set
  since: 1.0.0

  SMEMBERS key
  summary: Get all the members in a set
  since: 1.0.0

  SMOVE source destination member
  summary: Move a member from one set to another
  since: 1.0.0

  SPOP key [count]
  summary: Remove and return one or multiple random members from a set
  since: 1.0.0

  SRANDMEMBER key [count]
  summary: Get one or multiple random members from a set
  since: 1.0.0

  SREM key member [member ...]
  summary: Remove one or more members from a set
  since: 1.0.0

  SSCAN key cursor [MATCH pattern] [COUNT count]
  summary: Incrementally iterate Set elements
  since: 2.8.0

  SUNION key [key ...]
  summary: Add multiple sets
  since: 1.0.0

  SUNIONSTORE destination key [key ...]
  summary: Add multiple sets and store the resulting set in a key
  since: 1.0.0

# hash
127.0.0.1:6379> help @hash

  HDEL key field [field ...]
  summary: Delete one or more hash fields
  since: 2.0.0

  HEXISTS key field
  summary: Determine if a hash field exists
  since: 2.0.0

  HGET key field
  summary: Get the value of a hash field
  since: 2.0.0

  HGETALL key
  summary: Get all the fields and values in a hash
  since: 2.0.0

  HINCRBY key field increment
  summary: Increment the integer value of a hash field by the given number
  since: 2.0.0

  HINCRBYFLOAT key field increment
  summary: Increment the float value of a hash field by the given amount
  since: 2.6.0

  HKEYS key
  summary: Get all the fields in a hash
  since: 2.0.0

  HLEN key
  summary: Get the number of fields in a hash
  since: 2.0.0

  HMGET key field [field ...]
  summary: Get the values of all the given hash fields
  since: 2.0.0

  HMSET key field value [field value ...]
  summary: Set multiple hash fields to multiple values
  since: 2.0.0

  HSCAN key cursor [MATCH pattern] [COUNT count]
  summary: Incrementally iterate hash fields and associated values
  since: 2.8.0

  HSET key field value
  summary: Set the string value of a hash field
  since: 2.0.0

  HSETNX key field value
  summary: Set the value of a hash field, only if the field does not exist
  since: 2.0.0

  HSTRLEN key field
  summary: Get the length of the value of a hash field
  since: 3.2.0

  HVALS key
  summary: Get all the values in a hash
  since: 2.0.0

# sortedset
127.0.0.1:6379> help @sorted_set

  BZPOPMAX key [key ...] timeout
  summary: Remove and return the member with the highest score from one or more sorted sets, or block until one is available
  since: 5.0.0

  BZPOPMIN key [key ...] timeout
  summary: Remove and return the member with the lowest score from one or more sorted sets, or block until one is available
  since: 5.0.0

  ZADD key [NX|XX] [CH] [INCR] score member [score member ...]
  summary: Add one or more members to a sorted set, or update its score if it already exists
  since: 1.2.0

  ZCARD key
  summary: Get the number of members in a sorted set
  since: 1.2.0

  ZCOUNT key min max
  summary: Count the members in a sorted set with scores within the given values
  since: 2.0.0

  ZINCRBY key increment member
  summary: Increment the score of a member in a sorted set
  since: 1.2.0

  ZINTERSTORE destination numkeys key [key ...] [WEIGHTS weight] [AGGREGATE SUM|MIN|MAX]
  summary: Intersect multiple sorted sets and store the resulting sorted set in a new key
  since: 2.0.0

  ZLEXCOUNT key min max
  summary: Count the number of members in a sorted set between a given lexicographical range
  since: 2.8.9

  ZPOPMAX key [count]
  summary: Remove and return members with the highest scores in a sorted set
  since: 5.0.0

  ZPOPMIN key [count]
  summary: Remove and return members with the lowest scores in a sorted set
  since: 5.0.0

  ZRANGE key start stop [WITHSCORES]
  summary: Return a range of members in a sorted set, by index
  since: 1.2.0

  ZRANGEBYLEX key min max [LIMIT offset count]
  summary: Return a range of members in a sorted set, by lexicographical range
  since: 2.8.9

  ZRANGEBYSCORE key min max [WITHSCORES] [LIMIT offset count]
  summary: Return a range of members in a sorted set, by score
  since: 1.0.5

  ZRANK key member
  summary: Determine the index of a member in a sorted set
  since: 2.0.0

  ZREM key member [member ...]
  summary: Remove one or more members from a sorted set
  since: 1.2.0

  ZREMRANGEBYLEX key min max
  summary: Remove all members in a sorted set between the given lexicographical range
  since: 2.8.9

  ZREMRANGEBYRANK key start stop
  summary: Remove all members in a sorted set within the given indexes
  since: 2.0.0

  ZREMRANGEBYSCORE key min max
  summary: Remove all members in a sorted set within the given scores
  since: 1.2.0

  ZREVRANGE key start stop [WITHSCORES]
  summary: Return a range of members in a sorted set, by index, with scores ordered from high to low
  since: 1.2.0

  ZREVRANGEBYLEX key max min [LIMIT offset count]
  summary: Return a range of members in a sorted set, by lexicographical range, ordered from higher to lower strings.
  since: 2.8.9

  ZREVRANGEBYSCORE key max min [WITHSCORES] [LIMIT offset count]
  summary: Return a range of members in a sorted set, by score, with scores ordered from high to low
  since: 2.2.0

  ZREVRANK key member
  summary: Determine the index of a member in a sorted set, with scores ordered from high to low
  since: 2.0.0

  ZSCAN key cursor [MATCH pattern] [COUNT count]
  summary: Incrementally iterate sorted sets elements and associated scores
  since: 2.8.0

  ZSCORE key member
  summary: Get the score associated with the given member in a sorted set
  since: 1.2.0

  ZUNIONSTORE destination numkeys key [key ...] [WEIGHTS weight] [AGGREGATE SUM|MIN|MAX]
  summary: Add multiple sorted sets and store the resulting sorted set in a new key
  since: 2.0.0
   
# OBJECT
127.0.0.1:6379> OBJECT HELP
1) OBJECT <subcommand> arg arg ... arg. Subcommands are:
2) ENCODING <key> -- Return the kind of internal representation used in order to store the value associated with a key.
3) FREQ <key> -- Return the access frequency index of the key. The returned integer is proportional to the logarithm of the recent access frequency of the key.
4) IDLETIME <key> -- Return the idle time of the key, that is the approximated number of seconds elapsed since the last access to the key.
5) REFCOUNT <key> -- Return the number of references of the value associated with the specified key.

127.0.0.1:6379> set k1 qerq
OK
127.0.0.1:6379> set k2 1
OK
127.0.0.1:6379> object encoding k1
"embstr"
127.0.0.1:6379> object encoding k2
"int"
127.0.0.1:6379> keys *
1) "k2"
2) "k1"
3) "k3"
127.0.0.1:6379> FLUSHALL
OK
127.0.0.1:6379> keys *
(empty list or set)
```

### string

- 字符串

  - set, get, append, strlen

  ```
  127.0.0.1:6379> set k1 aaa
  OK
  127.0.0.1:6379> get k1
  "aaa"
  127.0.0.1:6379> type k1
  string
  127.0.0.1:6379> APPEND k1 puma
  (integer) 7
  127.0.0.1:6379> get k1
  "aaapuma"
  127.0.0.1:6379> FLUSHALL (删库跑路操作命令，在公司千万不要用)
  OK
  
  # 要记住某个命令是哪个 group 里的
  127.0.0.1:6379> set k1 a
  OK
  127.0.0.1:6379> strlen k1
  (integer) 1
  127.0.0.1:6379> set k2 1
  OK
  127.0.0.1:6379> strlen k2
  (integer) 1
  127.0.0.1:6379> INCRBY k2 100000
  (integer) 100001
  127.0.0.1:6379> strlen k2
  (integer) 6
  127.0.0.1:6379> set k3 a人
  OK
  127.0.0.1:6379> get k3
  "a\xe4\xba\xba"
  127.0.0.1:6379> strlen k3
  (integer) 4
  [root@localhost ~]# redis-cli --raw
  127.0.0.1:6379> get k3
  a人
  ```

- 数值

  ```
  127.0.0.1:6379> incr k2
  (integer) 2
  127.0.0.1:6379> decr k2
  (integer) 1
  127.0.0.1:6379> incrby k2 10
  (integer) 11
  127.0.0.1:6379> decrby k2 10
  (integer) 1
  ```

- bitmap

  ```
  # bit operation
  127.0.0.1:6379> SETBIT k1 1 1
  (integer) 0
  127.0.0.1:6379> get k1
  "@"
  127.0.0.1:6379> SETBIT k1 7 1
  (integer) 0
  127.0.0.1:6379> STRLEN k1
  (integer) 1
  127.0.0.1:6379> get k1
  "A"
  127.0.0.1:6379> SETBIT k1 7 0
  (integer) 1
  127.0.0.1:6379> SETBIT k1 6 1
  (integer) 0
  127.0.0.1:6379> get k1
  "B"
  127.0.0.1:6379> SETBIT k1 9 1
  (integer) 0
  127.0.0.1:6379> get k1
  "B@"
  127.0.0.1:6379> strlen k1
  (integer) 2
  127.0.0.1:6379> BITCOUNT k1 0 1 # 第零个字节一共有多少个1
  (integer) 3
  127.0.0.1:6379> BITCOUNT k1 1 1 # 第一个字节一共有多少个1
  (integer) 1
  
  ```

### hash



### list



### set



### sorted set