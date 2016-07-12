package br.ufg.inf.saep.db;


import br.ufg.inf.saep.config.DBConfig;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class DBConnection {
	private MongoClient mongoClient;
	private MongoDatabase database;
	private static DBConnection connectionInstance = new DBConnection();

	public static DBConnection getConnection() {
		return connectionInstance;
	}

	private DBConnection() {
		this.mongoClient = new MongoClient(DBConfig.MONGO_HOST, DBConfig.MONTO_PORT);
		this.database = mongoClient.getDatabase(DBConfig.MONGO_DATABASE);
	}

	public MongoDatabase getDatabase(){
		return this.database;
	}

}
