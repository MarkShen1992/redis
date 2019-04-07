package com.mark.memcached;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import net.spy.memcached.MemcachedClient;

/**
 * stats
 *
 */
public class HelloMemcachedStats {
  public static void main(String[] args) throws Exception {
    MemcachedClient mcc = null;
    try {
      // 连接本地的 Memcached 服务
      mcc = new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));
      System.out.println("Connection to server sucessful.");

      Map<SocketAddress, Map<String, String>> stats = mcc.getStats();
      
      //      stats
      //      STAT pid 1162                      memcache服务器进程ID
      //      STAT uptime 5022                   服务器已运行秒数
      //      STAT time 1415208270               服务器当前Unix时间戳
      //      STAT version 1.4.14                memcache版本
      //      STAT libevent 2.0.19-stable
      //      STAT pointer_size 64               操作系统指针大小
      //      STAT rusage_user 0.096006          进程累计用户时间
      //      STAT rusage_system 0.152009        进程累计系统时间
      //      STAT curr_connections 5            当前连接数量
      //      STAT total_connections 6           Memcached运行以来连接总数
      //      STAT connection_structures 6       Memcached分配的连接结构数量
      //      STAT reserved_fds 20
      //      STAT cmd_get 6                     get命令请求次数
      //      STAT cmd_set 4                     set命令请求次数
      //      STAT cmd_flush 0                   flush命令请求次数
      //      STAT cmd_touch 0
      //      STAT get_hits 4                    get命令命中次数
      //      STAT get_misses 2                  get命令未命中次数
      //      STAT delete_misses 1               delete命令未命中次数
      //      STAT delete_hits 1                 delete命令命中次数
      //      STAT incr_misses 2                 incr命令未命中次数
      //      STAT incr_hits 1                   incr命令命中次数
      //      STAT decr_misses 0                 decr命令未命中次数
      //      STAT decr_hits 1                   decr命令命中次数
      //      STAT cas_misses 0                  cas命令未命中次数
      //      STAT cas_hits 0                    cas命令命中次数
      //      STAT cas_badval 0                  使用擦拭次数
      //      STAT touch_hits 0
      //      STAT touch_misses 0
      //      STAT auth_cmds 0                   认证命令处理的次数
      //      STAT auth_errors 0                 认证失败数目
      //      STAT bytes_read 262                读取总字节数
      //      STAT bytes_written 313             发送总字节数
      //      STAT limit_maxbytes 67108864       分配的内存总大小（字节）
      //      STAT accepting_conns 1             服务器是否达到过最大连接（0/1）
      //      STAT listen_disabled_num 0         失效的监听数
      //      STAT threads 4                     当前线程数
      //      STAT conn_yields 0                 连接操作主动放弃数目
      //      STAT hash_power_level 16
      //      STAT hash_bytes 524288
      //      STAT hash_is_expanding 0
      //      STAT expired_unfetched 1
      //      STAT evicted_unfetched 0
      //      STAT bytes 142                     当前存储占用的字节数
      //      STAT curr_items 2                  当前存储的数据总数
      //      STAT total_items 6                 启动以来存储的数据总数
      //      STAT evictions 0                   LRU释放的对象数目
      //      STAT reclaimed 1                   已过期的数据条目来存储新数据的数目
      //      END
      System.out.println(stats.toString());
      
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    } finally {
      // 关闭连接
      mcc.shutdown();
    }
  }
}
