package com.mark.memcached;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;
import net.spy.memcached.CASValue;
import net.spy.memcached.MemcachedClient;

/**
 * gets
 *
 */
public class HelloMemcachedGets {
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

      // 从缓存中获取键为 runoob 的值
      System.out.println("runoob value in cache - " + mcc.get("runoob"));

      // 通过 gets 方法获取 CAS token（令牌）
      CASValue<?> casValue = mcc.gets("runoob");

      // 输出 CAS token（令牌） 值
      System.out.println("CAS value in cache - " + casValue);
      
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    } finally {
      // 关闭连接
      mcc.shutdown();
    }
  }
}
