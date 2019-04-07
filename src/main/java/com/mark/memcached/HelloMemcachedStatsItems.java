package com.mark.memcached;

import java.net.InetSocketAddress;
import net.spy.memcached.MemcachedClient;

/**
 * stats items
 * stats slabs
 * stats sizes
 * flush all
 */
public class HelloMemcachedStatsItems {
  public static void main(String[] args) throws Exception {
    MemcachedClient mcc = null;
    try {
      // 连接本地的 Memcached 服务
      mcc = new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));
      System.out.println("Connection to server sucessful.");

      // stats items
      
      // stats slabs
      
      // stats sizes
      int size = mcc.getStats().size();
      System.out.println(size);
      // flush all
      mcc.flush();
      System.out.println(mcc.get("runoob"));
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    } finally {
      // 关闭连接
      mcc.shutdown();
    }
  }
}
