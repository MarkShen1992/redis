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

