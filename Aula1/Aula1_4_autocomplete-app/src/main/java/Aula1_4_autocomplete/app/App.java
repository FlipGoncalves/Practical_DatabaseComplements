package Aula1_4_autocomplete.app;

import redis.clients.jedis.Jedis; 
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Scanner;


public class App 
{
    private Jedis jedis = new Jedis("localhost");

    public App(String args) throws IOException { 

        String searchTerm = args;

        System.out.println("a) --");

        /* Nao ha necessidade de estar sempre a dar override na database por isso fica comentado depois de ser corrido uma vez

        Path path = Paths.get("/home/flip/Área de Trabalho/CBD/Aula1/names.txt");
        List<String> names = Files.readAllLines(path);
        for (String name : names)
        {
            jedis.zadd("autofill",0,name);
        }
        */

        byte[] prefixByte = ("[" + searchTerm).getBytes();
        byte[] prefixByteExtended = Arrays.copyOf(prefixByte, prefixByte.length + 1);
        prefixByteExtended[prefixByte.length] = (byte) 0xFF;

        Set<String> autofill = jedis.zrangeByLex("autofill", "["+searchTerm, new String(prefixByteExtended));
        System.out.println("\nSearch for: " + searchTerm);
        Object[] array = autofill.toArray();

        for (int i = 0; i < 5 && array.length > i ; i++)
            System.out.println(array[i]);
        System.out.println();

    }
    public static void main( String[] args ) throws IOException
    {
        new App(args[0]);
    }
}
