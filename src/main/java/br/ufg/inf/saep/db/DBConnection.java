package br.ufg.inf.saep.db;


import br.ufg.inf.saep.config.LoadConfig;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.Properties;

public class DBConnection {
	private MongoClient mongoClient;
	private MongoDatabase database;
	private static DBConnection connectionInstance = new DBConnection();
	private Properties databaseConfig = LoadConfig.loadFile("database.properties");

	public static DBConnection getConnection() {
		return connectionInstance;
	}

	private DBConnection() {
		this.mongoClient = new MongoClient(databaseConfig.getProperty("MONGO_HOST"), Integer.parseInt(databaseConfig.getProperty("MONGO_PORT")));
		this.database = mongoClient.getDatabase(databaseConfig.getProperty("MONGO_DATABASE"));
	}

	public Properties getConfig(){
		return this.databaseConfig;
	}

	public MongoDatabase getDatabase(){
		return this.database;
	}

}
