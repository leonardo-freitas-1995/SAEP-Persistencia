package br.ufg.inf.saep.dao;

import br.ufg.inf.es.saep.sandbox.dominio.Radoc;
import br.ufg.inf.saep.db.DBConnection;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.bson.Document;

/**
 * Created by Leonardo on 07/07/2016.
 */
public class RadocDAO {
	private static RadocDAO instance = new RadocDAO();
	private MongoDatabase db = DBConnection.getConnection().getDatabase();
	private MongoCollection<Document> radocCollection = db.getCollection("radocs");
	private Gson gson = new Gson();

	public static RadocDAO getInstance() {
		return instance;
	}

	private RadocDAO() {
	}

	Radoc byId(String identificador){
		Document query = new Document("id", identificador);
		Document resolucaoDocument = radocCollection.find(query).first();
		if (resolucaoDocument == null)
			return null;
		return gson.fromJson(resolucaoDocument.toJson(), Radoc.class);
	}

	String persistir(Radoc radoc){
		Document query = new Document("id", radoc.getId());
		Document resolucaoDocument = radocCollection.find(query).first();
		if (resolucaoDocument != null)
			return null;

		radocCollection.insertOne((Document) JSON.parse(gson.toJson(radoc)));
		return radoc.getId();
	}

	void remove(String identificador){
		Document query = new Document("id", identificador);
		radocCollection.deleteOne(query);
	}
}
