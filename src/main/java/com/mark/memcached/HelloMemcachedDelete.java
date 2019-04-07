package com.mark.memcached;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;
import net.spy.memcached.MemcachedClient;

/**
 * delete
 *
 */
public class HelloMemcachedDelete {
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

      // 对存在的key进行数据添加操作
      fo = mcc.delete("runoob");

      // 输出执行 delete 方法后的状态
      System.out.println("delete status:" + fo.get());

      // 获取键对应的值
      System.out.println("runoob value in cache - " + mcc.get("runoob"));

    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    } finally {
      // 关闭连接
      mcc.shutdown();
    }
  }
}
