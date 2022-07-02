package cbd_cassandra;

import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import org.json.JSONObject;


import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;

import java.io.StringWriter;
import java.util.*;

public class App
{

    private CassandraConnector connector;

    public App(CassandraConnector connector) {
        this.connector = connector;
    }

    private void searchInUsers() {
        String table = "users";
        String col = "username";
        String val = "filipe";


        try {
            ResultSet results = this.connector.select(table, col, val);
            results.forEach(x ->  System.out.println("{username:" + x.getString("username") + ", name:" + x.getString("name") + ", email:" + x.getString("email") + ", registerMoment: " + x.getInstant("registerMoment") + "}"));
        }
        catch (InvalidQueryException e) {
            System.out.println("\nError! That query is not valid!\n");
            System.out.println(e.toString());
        }
    }

    private void insertIntoUsers() {
        String table = "users";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "mini");
        jsonObject.put("name", "Pedro");
        jsonObject.put("email", "mini@gmail.com");
        jsonObject.put("date_register", "2021-03-28T10:46:32.000+0000");
        StringWriter out = new StringWriter();
        jsonObject.write(out);
        String json = out.toString();
        try {
            this.connector.insert(table, json);
            System.out.println("\nThe json provided was inserted into the table: users");
        } catch(Exception e) {
            System.out.println("\nError! It was not possible to insert the json given: " + e.toString());
        }
    }

    private void query1() {
        String user = "filipe";

        Set<Relation> where = new HashSet<>();
        where.add(Relation.column("autor").isEqualTo(literal(user)));

        ResultSet results = this.connector.select("videos", where, null, 0, false);
        Iterator<Row> iterator = results.iterator();
        while(iterator.hasNext()){
            Row row = iterator.next();
            String res = "";
            res = res + "{autor:" + row.getString("autor");
            res = res + ", date_register:" + row.getInstant("date_register");
            res = res + ", video_id:" + row.getInt("video_id");
            res = res + ", description:" + row.getString("description");
            res = res + ", title: " + row.getString("title");
            res = res + ", tags:";
            Set<String> tags = row.getSet("tags", String.class);
            res = res + tags.toString();
            res = res + "}";
            System.out.println(res);
        }

    }

    private void query2() {
        int id = 1450;

        Set<Relation> where = new HashSet<>();
        where.add(Relation.column("video_id").isEqualTo(literal(id)));

        ResultSet results = this.connector.select("comment_username", where, null, 3, false);
        Iterator<Row> iterator = results.iterator();
        while(iterator.hasNext()){
            Row row = iterator.next();
            String res = "";
            res = res + ", autor:" + row.getString("autor");
            res = res + ", video_id:" + row.getInt("video_id");
            res = res + ", date_comment:" + row.getInstant("date_comment");
            res = res + ", comment: " + row.getString("comment");
            res = res + ", user_mail: " + row.getString("user_mail");
            res = res + ", user_username: " + row.getString("user_username");
            res = res + "}";
            System.out.println(res);
        }
    }

    private void query3() {
        String tag = "Jogos";

        Set<Relation> where = new HashSet<>();
        where.add(Relation.column("video_tags").contains(literal(tag)));

        Set<Selector> selector = new HashSet<>();
        selector.add(Selector.column("video_id"));
        selector.add(Selector.column("tags"));

        ResultSet results = this.connector.select("videos", where, selector, 0, true);
        Iterator<Row> iterator = results.iterator();
        while(iterator.hasNext()){
            Row row = iterator.next();
            String res = "";
            res = res + "{tags:";
            Set<String> tags = row.getSet("tags", String.class);
            res = res + tags.toString();
            res = res + ", video_id:" + row.getInt("video_id");
            res = res + "}";
            System.out.println(res);
        }
    }

    private void query4() {
        int id = 3858;
        String user = "bot2";

        Set<Relation> where = new HashSet<>();
        where.add(Relation.column("user_mail").isEqualTo(literal(user)));
        where.add(Relation.column("video_id").isEqualTo(literal(id)));

        ResultSet results = this.connector.select("event", where, null, 5, false);
        Iterator<Row> iterator = results.iterator();
        while(iterator.hasNext()){
            Row row = iterator.next();
            String res = "";
            res = res + "{video_id:" + row.getInt("video_id");
            res = res + ", user_mail:" + row.getString("user_mail");
            res = res + ", date_event:" + row.getInstant("date_event");
            res = res + ", tipo: " + row.getString("tipo");
            res = res + ", date_video:" + row.getInt("date_video");
            res = res + "}";
            System.out.println(res);
        }
    }

    private void run() {
        // Show menu
        System.out.println("\nAlinea a)\n");
        System.out.printf("\nSearch for user with username = \"filipe\" from table: users\n");
        this.searchInUsers(); 
        System.out.printf("\nInsert data to a table\n");
        this.insertIntoUsers();
        System.out.println("\nAlinea b)\n");
        System.out.printf("\nSearch for all videos of a given author\n");
        this.query1();
        System.out.printf("\nSearch for the last 3 comments at a given video\n");
        this.query2();
        System.out.printf("\nSearch for all videos with a given tag\n");
        this.query3();
        System.out.printf("\nSearch for the last 5 events of an user at a given video\n");
        this.query4();
    }

    public static void main( String[] args )
    {
        // Connect to DB and start VideoShare instance
        System.out.println("Connecting to Cassandra...");
        CassandraConnector connector = new CassandraConnector("127.0.0.1", 9042, "datacenter1", "videoshare");
        System.out.println(connector.getSession());

        App cassandra = new App(connector);
        cassandra.run();
        System.exit(0);
    }
}
