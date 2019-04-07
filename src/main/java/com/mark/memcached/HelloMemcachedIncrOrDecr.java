package com.mark.memcached;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;
import net.spy.memcached.MemcachedClient;

/**
 * incr/decr
 *
 */
public class HelloMemcachedIncrOrDecr {
  public static void main(String[] args) throws Exception {
    MemcachedClient mcc = null;
    try {
      // 连接本地的 Memcached 服务
      mcc = new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));
      System.out.println("Connection to server sucessful.");

      // 添加数字值
      Future<?> fo = mcc.set("number", 900, "1000");

      // 输出执行 set 方法后的状态
      System.out.println("set status:" + fo.get());

      // 获取键对应的值
      System.out.println("value in cache - " + mcc.get("number"));

      // 自增并输出
      System.out.println("value in cache after increment - " + mcc.incr("number", 111));

      // 自减并输出
      System.out.println("value in cache after decrement - " + mcc.decr("number", 112));
      
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    } finally {
      // 关闭连接
      mcc.shutdown();
    }
  }
}
