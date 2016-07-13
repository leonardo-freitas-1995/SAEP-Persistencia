package br.ufg.inf.saep.db;


import br.ufg.inf.saep.config.LoadConfig;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.Properties;


/**
 * Classe Singleton de conexão com o MongoDB.
 * Oferece um ponto unico de conexão na execução da API.
 *
 * @see MongoDatabase
 */
public class DBConnection {

	/**
	 * Cliente de conexão do MongoDB, instanciado na criação do Singleton.
	 */
	private MongoClient mongoClient;

	/**
	 * Objeto de conexão do MongoDB, instanciado na criação do Singleton.
	 */
	private MongoDatabase database;

	/**
	 * Instancia unica desta classe, caracterizando um Singleton.
	 */
	private static DBConnection connectionInstance = new DBConnection();

	/**
	 * Propriedades do banco extraidas do arquivo 'database.properties'.
	 */
	private Properties databaseConfig = LoadConfig.loadFile("database.properties");

	/**
	 * Cria se não existir uma conexão com o banco.
	 * @return A instancia Singleton de conexão com o MongoDB
	 */
	public static DBConnection getConnection() {
		return connectionInstance;
	}

	/**
	 * Construdor privado do Singleton, chamado apenas quando não existe uma instancia ativa.
	 */
	private DBConnection() {
		this.mongoClient = new MongoClient(databaseConfig.getProperty("MONGO_HOST"), Integer.parseInt(databaseConfig.getProperty("MONGO_PORT")));
		this.database = mongoClient.getDatabase(databaseConfig.getProperty("MONGO_DATABASE"));
	}

	/**
	 * Pega a instancia de {@code Properties} com configurações do banco.
	 * @return Objeto {@code Properties} com as configurações.
	 */
	public Properties getConfig(){
		return this.databaseConfig;
	}

	/**
	 * Pega a conexão com o MongoDB criada na instancia do Singleton
	 * @return O {@code MongoDatabase} com a conexão com o banco especificadas no arquivo de configuração.
	 */
	public MongoDatabase getDatabase(){
		return this.database;
	}

}
