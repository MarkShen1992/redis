package com.mark.memcached;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;
import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;
import net.spy.memcached.MemcachedClient;

/**
 * CAS
 *
 */
public class HelloMemcachedCAS {
  public static void main(String[] args) throws Exception {
    MemcachedClient mcc = null;
    try {

      // 连接本地的 Memcached 服务
      mcc = new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));
      System.out.println("Connection to server sucessful.");

      // 添加第一个 key=》value 对
      Future<?> fo = mcc.set("runoob", 900, "Free Education");

      // 输出执行 set 方法后的状态
      System.out.println("set status:" + fo.get());
         
      // 使用 get 方法获取数据
      System.out.println("runoob value in cache - " + mcc.get("runoob"));

      // 通过 gets 方法获取 CAS token（令牌）
      CASValue<?> casValue = mcc.gets("runoob");

      // 输出 CAS token（令牌） 值
      System.out.println("CAS token - " + casValue);

      // 尝试使用cas方法来更新数据
      CASResponse casresp = mcc.cas("runoob", casValue.getCas(), 900, "Largest Tutorials-Library");
      
      // 输出 CAS 响应信息
      System.out.println("CAS Response - " + casresp);

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
