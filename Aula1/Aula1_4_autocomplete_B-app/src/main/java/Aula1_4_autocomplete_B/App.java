package Aula1_4_autocomplete_B;

import redis.clients.jedis.Jedis; 
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class App 
{
    private Jedis jedis = new Jedis("localhost");

    public App(String args) throws IOException { 

        String searchTerm = args;

        System.out.println("b) --");
        
        /* Nao ha necessidade de estar sempre a dar override na database por isso fica comentado depois de ser corrido uma vez

        Path path = Paths.get("/home/flip/Área de Trabalho/CBD/Aula1/nomes-pt-2021.csv");
        List<String> names = Files.readAllLines(path);
        for (String name : names)
        {
            jedis.zadd("autofill_B", 0, name);
            // Como o ficheiro está ordenado por ocorrencia, nao precisamos de ir ver diretamente a contagem, podemos ver pela insercao na base de dados
            // Por isso nao usamos diretamente as ocurrencias, e vemos apenas o nome
        }
        */

        byte[] prefixByte = ("[" + searchTerm).getBytes();
        byte[] prefixByteExtended = Arrays.copyOf(prefixByte, prefixByte.length + 1);
        prefixByteExtended[prefixByte.length] = (byte) 0xFF;

        Set<String> autofill = jedis.zrangeByLex("autofill_B", "["+searchTerm, new String(prefixByteExtended));
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
