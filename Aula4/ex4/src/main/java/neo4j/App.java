package neo4j;

import java.io.FileWriter;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

public class App implements AutoCloseable
{
    private static final Driver driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "cbd_ex4" ) );

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

    public static void main( String... args ) throws Exception
    {   
        FileWriter writer = new FileWriter("CBD_L44c_output.txt");

        try ( Session session = driver.session() )
        {
            // NODES AND RELATIONS CREATION 

            String loadEntities = session.writeTransaction( tx-> {
                Result result = tx.run("LOAD CSV WITH HEADERS FROM \"file:///vgsales.csv\" AS line "
                                        + " MERGE (game:Game {rank: line.Rank})"
                                        + " SET game.Name = line.Name, game.Year = line.Year, game.Genre = line.Genre"
                                        + " MERGE (p:Platform {plat: line.Platform})");
                return result.toString();
            });
            System.out.println( loadEntities );
            String loadEntities2 = session.writeTransaction( tx-> {
                Result result = tx.run("LOAD CSV WITH HEADERS FROM \"file:///vgperson.csv\" AS line "
                                        + " MERGE (person:Person {id: line.id})"
                                        + " SET person.name = line.name");
                return result.toString();
            });
            System.out.println( loadEntities2 );
            String loadRelations = session.writeTransaction( tx-> {
                Result result = tx.run("LOAD CSV WITH HEADERS FROM \"file:///vgsales.csv\" AS line "
                                        + " MATCH (game:Game {rank: line.Rank}), (p:Platform {plat: line.Platform})"
                                        + " CREATE (game)-[:PLAYED_ON]->(p)");
                return result.toString();
            });
            System.out.println( loadRelations );
            String loadRelations2 = session.writeTransaction( tx-> {
                Result result = tx.run("LOAD CSV WITH HEADERS FROM \"file:///videogame.csv\" AS line "
                                        + " MATCH (game:Game {Name: line.Name}), (person:Person {name: line.Bought})"
                                        + " CREATE (person)-[:BOUGHT]->(game)");
                return result.toString();
            });
            System.out.println( loadRelations2 );
            String loadRelations3 = session.writeTransaction( tx-> {
                Result result = tx.run("LOAD CSV WITH HEADERS FROM \"file:///videogame.csv\" AS line "
                                        + " MATCH (game:Game {Name: line.Name}), (person:Person {name: line.Played})"
                                        + " CREATE (person)-[:PLAYED]->(game)");
                return result.toString();
            });
            System.out.println( loadRelations3 );


            // QUERIES
            // Têm LIMIT de 25 para não demorar muito tempo nem ter demasiado output
            // O output funciona mas fica com Stringdo tipo 'org.neo4j.driver.internal.InternalResult@3120e201' e por isso foi feito pelo Neo4j Desktop
            // 1- Liste todos os Jogos
            String query1 = session.writeTransaction( tx-> {
                Result result = tx.run("MATCH (n:Game) RETURN n LIMIT 25");
                return result.toString();
            });
            System.out.println( query1 );
            writer.write("#1- Liste todos os Jogos\n");
            writer.write(query1.toString()+"\n");

            // 2- Liste todas as pessoas
            String query2 = session.writeTransaction( tx-> {
                Result result = tx.run("MATCH (n:Person) RETURN n LIMIT 25");
                return result.toString();
            });
            System.out.println( query2 );
            writer.write("\n#2- Liste todas as pessoas\n");
            writer.write(query2.toString()+"\n");

            // 3- Liste todos os jogos com o Genero "Racing"
            String query3 = session.writeTransaction( tx-> {
                Result result = tx.run("MATCH (n:Game {Genre: \"Racing\"}) RETURN n LIMIT 25");
                return result.toString();
            });
            System.out.println( query3 );
            writer.write("\n#3- Liste todos os jogos com o Genero 'Racing'\n");
            writer.write(query3.toString()+"\n");

            // 4- Liste todos os jogos jogados em "Wii"
            String query4 = session.writeTransaction( tx-> {
                Result result = tx.run("MATCH (n:Game)-[:PLAYED_ON]->(m:Platform {plat: \"Wii\"}) RETURN n LIMIT 25");
                return result.toString();
            });
            System.out.println( query4 );
            writer.write("\n#4- Liste todos os jogos jogados em 'Wii'\n");
            writer.write(query4.toString()+"\n");
            
            // 5- Liste todos os jogos jogados em "NES" que estrearam no ano de 1990
            String query5 = session.writeTransaction( tx-> {
                Result result = tx.run("MATCH (n:Game)-[:PLAYED_ON]->(m:Platform) WHERE m.plat = \"NES\" and n.Year = \"1990\" RETURN n LIMIT 25");
                return result.toString();
            });
            System.out.println( query5 );
            writer.write("\n#5- Liste todos os jogos jogados em 'NES' que estrearam antes de 1990\n");
            writer.write(query5.toString()+"\n");

            // 6- Liste todos os jogos jogados pelo "Filipe"
            String query6 = session.writeTransaction( tx-> {
                Result result = tx.run("MATCH (n:Game)<-[:PLAYED]-(p:Person {name: \"Filipe\"}) RETURN n LIMIT 25");
                return result.toString();
            });
            System.out.println( query6 );
            writer.write("\n#6- Liste todos os jogos jogados pelo 'Filipe'\n");
            writer.write(query6.toString()+"\n");

            // 7- Liste todos os jogos comprados pelo "Joao"
            String query7 = session.writeTransaction( tx-> {
                Result result = tx.run("MATCH (n:Game)<-[:BOUGHT]-(p:Person {name: \"Joao\"}) RETURN n LIMIT 25");
                return result.toString();
            });
            System.out.println( query7 );
            writer.write("\n#Liste todos os jogos comprados pelo 'Joao'\n");
            writer.write(query7.toString()+"\n");

            // 8- Liste todos os jogos comprados e jogados pelo "Pedro"
            String query8 = session.writeTransaction( tx-> {
                Result result = tx.run("MATCH (p1:person {name: \"Pedro\"})-[:BOUGHT]->(n:Game)<-[:PLAYED]-(p:Person {name: \"Pedro\"}) RETURN n LIMIT 25");
                return result.toString();
            });
            System.out.println( query8 );
            writer.write("\n#8- Liste todos os jogos comprados e jogados pelo 'Pedro'\n");
            writer.write(query8.toString()+"\n");

            // 9- Liste o rank dos 25 jogos mais antigos
            String query9 = session.writeTransaction( tx-> {
                Result result = tx.run("MATCH (n:Game) RETURN n ORDER BY n.Year ASC LIMIT 25");
                return result.toString();
            });
            System.out.println( query9 );
            writer.write("\n#9- Liste o rank dos 25 jogos mais antigos\n");
            writer.write(query9.toString()+"\n");

            // 10- Liste o rank de todos os jogos jogados pelo "Goncalo"
            String query10 = session.writeTransaction( tx-> {
                Result result = tx.run("MATCH (n:Game)<-[:PLAYED]-(p:Person {name: \"Goncalo\"}) RETURN n.rank");
                return result.toString();
            });
            System.out.println( query10 );
            writer.write("\n#10- Liste o rank de todos os jogos jogados pelo 'Goncalo'\n");
            writer.write(query10.toString()+"\n");

            writer.close();
            driver.close();
        }
    }
}