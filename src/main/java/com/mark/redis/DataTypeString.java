/**
 * 
 */
package com.mark.redis;

import redis.clients.jedis.Jedis;

/**
 * data type String
 * 
 * @author 18009
 *
 */
public class DataTypeString {

  /**
   * localhost
   */
  private static final String LOCALHOST = "localhost";

  /**
   * password
   */
  private static final String PASSWORD = "123456";

  public static void main(String[] args) {
    // connect the local redis server
    Jedis jedis = new Jedis(LOCALHOST, 6379);
    jedis.auth(PASSWORD);
    
    // String practice
    // simple CRUD operation
    jedis.set("a", "a");
    System.out.println(jedis.get("a"));
    jedis.set("a", "hello world");
    System.out.println(jedis.get("a"));
    jedis.del("a");
    System.out.println(jedis.get("a"));
    
    jedis.set("test", "123456");
    System.out.println(jedis.get("test"));
    // substring
    System.out.println(jedis.getrange("test", 0, 2));
    
    System.out.println(jedis.getSet("test", "puma"));
    System.out.println(jedis.get("test"));
    
    // index start from 1
    System.out.println(jedis.getbit("test", 1));
    System.out.println(jedis.getbit("test", 4));
    System.out.println(jedis.getbit("test", 5));
    
    // get multiple result according to multiple key
    System.out.println(jedis.mget("a", "test"));
    
    System.out.println(jedis.setex("expireKey", 60, "family"));
    System.out.println(jedis.ttl("expireKey"));
    System.out.println(jedis.persist("expireKey"));
    
    System.out.println(jedis.strlen("test"));
    System.out.println(jedis.append("test", " , animal"));
    
    System.out.println(jedis.exists("test"));
    
    // if the key exists no operation
    System.out.println(jedis.setnx("test", "afjlafj"));
    System.out.println(jedis.type("test"));
    
    // number operation
    jedis.set("index", "0");
    System.out.println(jedis.get("index"));
    System.out.println(jedis.type("index"));
    System.out.println(jedis.incr("index"));
    System.out.println(jedis.decr("index"));
    System.out.println(jedis.incrBy("index", 2));
    System.out.println(jedis.decrBy("index", 2));
  }
}
