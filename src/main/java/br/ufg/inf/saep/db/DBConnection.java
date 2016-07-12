package br.ufg.inf.saep.db;


import br.ufg.inf.saep.config.LoadConfig;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class DBConnection {
	private MongoClient mongoClient;
	private MongoDatabase database;
	private static DBConnection connectionInstance = new DBConnection();
	LoadConfig databaseConfig = new LoadConfig("database.properties");

	public static DBConnection getConnection() {
		return connectionInstance;
	}

	private DBConnection() {
		this.mongoClient = new MongoClient(databaseConfig.getConfig("MONGO_HOST"), Integer.parseInt(databaseConfig.getConfig("MONGO_PORT")));
		this.database = mongoClient.getDatabase(databaseConfig.getConfig("MONGO_DATABASE"));
	}

	public MongoDatabase getDatabase(){
		return this.database;
	}

}
