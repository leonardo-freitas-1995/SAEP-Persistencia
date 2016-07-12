package br.ufg.inf.saep.db;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class DBConnection {

	private final String MONGO_HOST = "localhost";
	private final int MONTO_PORT = 27017;
	private final String MONGO_DATABASE = "saep";

	private MongoClient mongoClient;
	private MongoDatabase database;
	private static DBConnection connectionInstance = new DBConnection();

	public static DBConnection getConnection() {
		return connectionInstance;
	}

	private DBConnection() {
		this.mongoClient = new MongoClient(this.MONGO_HOST, this.MONTO_PORT);
		this.database = mongoClient.getDatabase(this.MONGO_DATABASE);
	}

	public MongoDatabase getDatabase(){
		return this.database;
	}

}
