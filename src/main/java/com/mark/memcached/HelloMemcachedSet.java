package com.mark.memcached;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;
import net.spy.memcached.MemcachedClient;

/**
 * set
 *
 */
public class HelloMemcachedSet {
  public static void main(String[] args) throws Exception {
    MemcachedClient mcc = null;
    try {
      // 连接本地的 Memcached 服务
      mcc = new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));
      System.out.println("Connection to server sucessful.");

      // 存储数据
      Future<?> fo = mcc.set("runoob", 900, "Free Education");

      // 查看存储状态
      System.out.println("set status:" + fo.get());

      // 输出值
      System.out.println("runoob value in cache - " + mcc.get("runoob"));
      
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    } finally {
      // 关闭连接
      mcc.shutdown();
    }
  }
}
