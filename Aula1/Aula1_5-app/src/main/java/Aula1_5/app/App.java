package Aula1_5.app;

import redis.clients.jedis.Jedis; 
import java.util.Scanner;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class App 
{
    private Jedis jedis = new Jedis("localhost");
    private Scanner sc = new Scanner(System.in);

    public App() {
        String next;
        int exit = 0;
        do {
            System.out.println("Escolha uma opção:\n1- Novo user\n2- Associar Users\n3- Enviar Mensagem\n4- Ler Mensagens\n5- Ver Users\n6- Ver Associações\n7- Exit");
            next = sc.nextLine();
            exit = 0;
            switch(next) {
                case "1":
                    System.out.print("Nome do novo user: ");
                    String user = sc.nextLine();
                    novouser(user);
                    break;
                case "2":
                    System.out.print("User1: ");
                    String user1 = sc.nextLine();
                    System.out.print("User2: ");
                    String user2 = sc.nextLine();
                    List<String> allusers = getUsers();
                    int count = 0;
                    for (String str: allusers) {
                        if (str.equals(user1) || str.equals(user2))
                            count++;
                    }
                    if (count == 2)
                        associar(user1, user2);
                    else
                        System.out.println("Users Not Found!");
                    break;
                case "3":
                    System.out.println("User: ");
                    String user_1 = sc.nextLine();
                    List<String> users = getUsers();
                    int count_3 = 0;
                    for (String str: users) {
                        if (str.equals(user_1))
                        count_3++;
                    }
                    if (count_3 == 1) {
                        System.out.print("Message: ");
                        String message = sc.nextLine();
                        enviarmensagem(user_1, message);
                    } else
                        System.out.println("User Not Found!");
                    break;
                case "4":
                    System.out.println("User: ");
                    String user_2 = sc.nextLine();
                    List<String> all = getUsers();
                    int count_4 = 0;
                    for (String str: all) {
                        if (str.equals(user_2))
                        count_4++;
                    }
                    if (count_4 == 1) {
                        lermensagens(user_2);
                    } else
                        System.out.println("User Not Found!");

                    break;
                case "5": 
                    System.out.println(getUsers() + "\n");
                    break;
                case "6":
                    System.out.println(getassociations() + "\n");
                    break;
                case "7":
                    System.out.println("Exiting...");
                    exit = 1;
                    break;
                default:
                    System.out.println("Opção errada! Tente de novo!\n");
                    break;
            }
        }
        while(exit == 0);
    }

    public void novouser(String nome) {
        jedis.lpush("User", nome);
        System.out.println("User ID: User." + nome + "\n");
    }

    public void associar(String u1, String u2) {
        jedis.sadd("Associations." + u1, u2);
        System.out.println("Association Completed!\n");
    }

    public List<String> getUsers() {
        List<String> array = jedis.lrange("User", 0, -1);
        return array;
    }

    public void enviarmensagem(String user, String message) {
        jedis.sadd("Message."+user, message);
        System.out.println("Mensagem enviada!\n");
    }

    public void lermensagens(String user) {
        List<String> allusers = getUsers();
        for (String str: allusers) {
            if (jedis.smembers("Associations."+user).contains(str))
                System.out.println("Messages from " + str + ": " + jedis.smembers("Message."+str));
        }
        System.out.println("Messages from " + user + ": " + jedis.smembers("Message."+user));
    }

    public Map<String, Set<String>> getassociations() {
        List<String> allusers = getUsers();
        Map<String, Set<String>> associations = new HashMap<>();
        for (String str: allusers) {
            associations.put(str, jedis.smembers("Associations."+str));
        }
        return associations;
    }

    public static void main( String[] args )
    {
        new App();
    }
}