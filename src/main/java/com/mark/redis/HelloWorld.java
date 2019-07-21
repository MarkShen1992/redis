/**
 * 
 */
package com.mark.redis;

import redis.clients.jedis.Jedis;

/**
 * redis hello world
 * 
 * @author 18009
 *
 */
public class HelloWorld {

  /**
   * localhost
   */
  private static final String LOCALHOST = "localhost";

  /**
   * password
   */
  private static final String PASSWORD = "123456";

  public static void main(String[] args) {
    //连接本地的 Redis 服务
    Jedis jedis = new Jedis(LOCALHOST, 6379);
    jedis.auth(PASSWORD);
    System.out.println("连接成功");
    //查看服务是否运行
    System.out.println("服务正在运行: " + jedis.ping());
  }
}
