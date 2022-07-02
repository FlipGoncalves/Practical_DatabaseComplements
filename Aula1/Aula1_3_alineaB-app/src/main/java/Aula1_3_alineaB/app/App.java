package Aula1_3_alineaB.app;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import redis.clients.jedis.Jedis;

public class App 
{
    private Jedis jedis;
	public static String USERS = "users"; // Key set for users' name
    public static String USERS_2 = "lista_exB";
    public static String USERS_3 = "hash_exB";
	
	public App() {
		this.jedis = new Jedis("localhost");
	}
 
	public void saveUser(String username) {
		jedis.sadd(USERS, username);
	}
	public Set<String> getUser() {
		return jedis.smembers(USERS);
	}
	
	public Set<String> getAllKeys() {
		return jedis.keys("*");
	}

    public void saveinList(String username) {
        jedis.lpush(USERS_2, username);
    }
    public List<String> getallfromList() {
        return jedis.lrange(USERS_2, 0, -1);
    }

    public void saveinHash(String key, String username) {
        Map<String, String> mp = new HashMap<>();
        mp.put(key, username);
        jedis.hmset(USERS_3, mp);
    }
    public List<String> getfromHash(String field) {
        return jedis.hmget(USERS_3, field);
    }
 
	public static void main(String[] args) {
		App board = new App();
		// set some users
		String[] users = { "Ana", "Pedro", "Maria", "Luis" };
		for (String user: users) 
			board.saveUser(user);
		System.out.println(board.getAllKeys());
		System.out.println(board.getUser());

        System.out.println("\n----------------------\n");

        for (String user: users) 
			board.saveinList(user);
		System.out.println(board.getallfromList());

        System.out.println("\n----------------------\n");

        String[] code = { "11", "12", "13", "14" };
        int i = 0;
        for (String user: users) 
			board.saveinHash(code[i++], user);
        for (String co: code) 
		    System.out.print(co + " -> " + board.getfromHash(co) + "\n");
	}
}
