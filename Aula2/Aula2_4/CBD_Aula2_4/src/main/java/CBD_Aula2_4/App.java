package CBD_Aula2_4;

import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.BasicDBObject;
import com.mongodb.ExplainVerbosity;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import static com.mongodb.client.model.Filters.*;
import java.util.Arrays;
import java.util.Date;

public class App {
    public static void main(String[] args) {

        // ALINEA A)
        System.out.println("--------a)");
        // Connection
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://127.0.0.1:27017"));
        MongoDatabase database = mongoClient.getDatabase("cbd");
        MongoCollection<Document> collection = database.getCollection("restaurants");

        // Insert
        Document doc = new Document("address", 
            new Document("building", "0000").append("coord", Arrays.asList(-50, 50)).append("rua", "FilipeFilipe").append("zipcode", "3800-510"))
            .append("localidade", "Aveiro").append("gastronomia", "Portuguese").append("grades", Arrays.asList(new Document("date", new Date()).append("garde", "SS+").append("score", 69)))
            .append("nome", "Filipe Gonçalves").append("restaurant_id", 12345678);

        collection.insertOne(doc);

        // Find
        Document myDoc = collection.find(eq("nome", "Filipe Gonçalves")).first();
        System.out.println(myDoc.toJson());

        // Update
        collection.updateOne(eq("nome", "Filipe Gonçalves"), new Document("$set", new Document("nome", "Filipe não Gonçalves")));


        // ALINEA B)
        System.out.println("\n--------b)");
        collection.createIndex(new Document("localidade", 1));
        collection.createIndex(new Document("gastronomia", 1));
        collection.createIndex(new Document("nome", 1));

        MongoCursor<Document> cursor = collection.find(eq("localidade", "Aveiro")).iterator();
        System.out.println("Localidade: Staten Island");
        try {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }

        System.out.println("\nGastronomia: Portuguese");
        MongoCursor<Document> cursor2 = collection.find(eq("gastronomia", "Portuguese")).iterator();
        try {
            while (cursor2.hasNext()) {
                System.out.println(cursor2.next().toJson());
            }
        } finally {
            cursor2.close();
        }

        System.out.println("\nNome: Filipe não Gonçalves");
        MongoCursor<Document> cursor3 = collection.find(eq("nome", "Filipe não Gonçalves")).iterator();
        try {
            while (cursor3.hasNext()) {
                System.out.println(cursor3.next().toJson());
            }
        } finally {
            cursor3.close();
        }


        // ALINEA C)
        System.out.println("\n--------c)");

        // 1.
        System.out.println("Query 1: Liste todos os documentos da coleção.");
        MongoCursor<Document> myDoc1 = collection.find().iterator();
        try {
            while (myDoc1.hasNext()) {
                System.out.println(myDoc1.next().toJson());
            }
        } finally {
            myDoc1.close();
        }

        // 5
        System.out.println("Query 5: Apresente os primeiros 15 restaurantes localizados no Bronx, ordenados por ordem crescente de nome.");
        MongoCursor<Document> myDoc2 = collection.find(eq("localidade", "Bronx")).sort(new BasicDBObject("nome",-1)).limit(5).iterator();
        try {
            while (myDoc2.hasNext()) {
                System.out.println(myDoc2.next().toJson());
            }
        } finally {
            myDoc2.close();
        }

        // 4
        System.out.println("Query 4: Indique o total de restaurantes localizados no Bronx.");
        Long myDoc3 = collection.count(eq("localidade", "Bronx"));
        System.out.println(myDoc3);

        // 8
        System.out.println("Query 8: Indique os restaurantes com latitude inferior a -95,7.");
        MongoCursor<Document> myDoc4 = collection.find(lt("address.coord.0", -95.7)).iterator();
        try {
            while (myDoc4.hasNext()) {
                System.out.println(myDoc4.next().toJson());
            }
        } finally {
            myDoc4.close();
        }

        // 29
        System.out.println("Query 29: Indique os restaurantes com latitude superior a -80.");
        MongoCursor<Document> myDoc5 = collection.find(gt("address.coord.0", -80)).iterator();
        try {
            while (myDoc5.hasNext()) {
                System.out.println(myDoc5.next().toJson());
            }
        } finally {
            myDoc5.close();
        }


        // ALINEA D)
        System.out.println("\n--------d)");

        MongoCursor<Document> aggr = collection.aggregate(
            Arrays.asList(
                    Aggregates.group("$localidade", Accumulators.sum("count", 1))
            )
        ).iterator();
        int count = 0;
        try {
            while (aggr.hasNext()) {
                String a = aggr.next().toJson();
                count++;
            }
        } finally {
            aggr.close();
        }
        System.out.println("Numero de localidades distintas: " + count);

        MongoCursor<Document> aggr2 = collection.aggregate(
            Arrays.asList(
                    Aggregates.group("$localidade", Accumulators.sum("count", 1))
            )
        ).iterator();
        System.out.println("Numero de restaurantes por localidade: ");
        try {
            while (aggr2.hasNext()) {
                System.out.println(aggr2.next().toJson());
            }
        } finally {
            aggr2.close();
        }

	
        BasicDBObject regexQuery = new BasicDBObject();
        regexQuery.put("nome", new BasicDBObject("$regex", "^Park.*").append("$options", "i"));

        MongoCursor<Document> aggr3 = collection.find(regexQuery).iterator();
        count = 0;
        try {
            while (aggr3.hasNext()) {
                String a = aggr3.next().toJson();
                count++;
            }
        } finally {
            aggr3.close();
        }
        System.out.println("Numero de restaurantes contendo 'Park' no nome: " + count);
        System.out.println();
        mongoClient.close();
    }
}