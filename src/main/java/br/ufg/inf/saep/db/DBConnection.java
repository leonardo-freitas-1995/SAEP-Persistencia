package br.ufg.inf.saep.db;


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
		this.mongoClient = new MongoClient( "localhost" , 27017 );
		this.database = mongoClient.getDatabase("saep");
	}

	public MongoDatabase getDatabase(){
		return this.database;
	}

}
