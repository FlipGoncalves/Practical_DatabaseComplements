package Aula1_3.app;

import redis.clients.jedis.Jedis; 

public class App { 
  
    private Jedis jedis;  
    
    public App(){ 
      this.jedis = new Jedis("localhost"); 
      System.out.println(jedis.info() + "\n----------------------------------------\n");
      this.jedis.set("Filipe", "Andre");
      System.out.println(this.jedis.get("Filipe"));
      this.jedis.sadd("um", "dois", "tres", "quatro");
      System.out.println(this.jedis.smembers("um"));
      this.jedis.hset("hash", "nome", "Filipe Gonçalves");
      System.out.println(this.jedis.hget("hash", "nome"));
      this.jedis.lpush("lista1", "nome1", "nome2", "nome3", "nome4");
      System.out.println(this.jedis.lrange("lista1", 0, -1));
    } 
    
    public static void main(String[] args) { 
      // a)
      System.out.println("a) --\n");
      new App(); 
    } 
}