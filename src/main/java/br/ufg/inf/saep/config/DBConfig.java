package br.ufg.inf.saep.config;

/**
 * Created by Leonardo on 12/07/2016.
 */
public class DBConfig {

	// Mongo Database Connection Config
	public final static String MONGO_HOST = "localhost";
	public final static int MONTO_PORT = 27017;
	public final static String MONGO_DATABASE = "saep";

	// Mongo Database collections Config
	public final static String RADOC_COLLECTION = "radocs";
	public final static String PARECER_COLLECTION = "pareceres";
	public final static String TIPO_COLLECTION = "tipos";
	public final static String RESOLUCAO_COLLECTION = "resolucoes";
}
