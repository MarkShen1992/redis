package com.mark.memcached;

import java.net.InetSocketAddress;
import net.spy.memcached.MemcachedClient;

/**
 * Hello world!
 *
 */
public class HelloMemcached {
  public static void main(String[] args) throws Exception {
    // 本地连接 Memcached 服务
    MemcachedClient mcc = new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));
    System.out.println("Connection to server sucessful.");
    Thread.sleep(5000);
    // 关闭连接
    mcc.shutdown();
  }
}
