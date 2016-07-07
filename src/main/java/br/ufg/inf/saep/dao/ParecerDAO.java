package br.ufg.inf.saep.dao;

import br.ufg.inf.es.saep.sandbox.dominio.*;
import br.ufg.inf.saep.db.DBConnection;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.bson.Document;

import java.util.ArrayList;

/**
 * Created by Leonardo on 07/07/2016.
 */
public class ParecerDAO implements ParecerRepository {
	private static ParecerDAO instance = new ParecerDAO();
	private MongoDatabase db = DBConnection.getConnection().getDatabase();
	private MongoCollection<Document> radocCollection = db.getCollection("radocs");
	private MongoCollection<Document> parecerCollection = db.getCollection("pareceres");
	private Gson gson = new Gson();

	public static ParecerDAO getInstance() {
		return instance;
	}

	private ParecerDAO() {
	}

	public void adicionaNota(String id, Nota nota) {
		Document query = new Document("id", id);
		Document update = new Document("$push", new Document("notas", JSON.parse(gson.toJson(nota))));
		Document originalDoc = parecerCollection.findOneAndUpdate(query, update);
		if (originalDoc == null){
			throw new IdentificadorDesconhecido(id);
		}
	}

	public void removeNota(String id, Avaliavel original) {
		Document query = new Document("id", id);
		String avaliavelJSON = gson.toJson(original);
		ArrayList<Nota> newNotas = new ArrayList<Nota>();
		Document parecerDocument = parecerCollection.find(query).first();
		ArrayList<Document> notasDocument = (ArrayList<Document>) parecerDocument.get("notas");
		for (Document notaDoc : notasDocument){
			Nota nota = gson.fromJson(notaDoc.toJson(), Nota.class);
			if (!gson.toJson(nota.getItemOriginal()).equals(avaliavelJSON)){
				newNotas.add(nota);
			}
		}
		Document newNotasDocument = new Document("notas", JSON.parse(gson.toJson(newNotas)));
		parecerCollection.updateOne(query, new Document("$set", newNotasDocument));
	}

	public void persisteParecer(Parecer parecer) {
		long findings = parecerCollection.count(new Document("id", parecer.getId()));
		if (findings > 0){
			throw new IdentificadorExistente("id");
		}
		parecerCollection.insertOne((Document) JSON.parse(gson.toJson(parecer)));
	}

	public void atualizaFundamentacao(String parecer, String fundamentacao) {
		Document query = new Document("id", parecer);
		Document update = new Document("$set", new Document("fundamentacao", fundamentacao));
		Document originalDoc = parecerCollection.findOneAndUpdate(query, update);
		if (originalDoc == null){
			throw new IdentificadorDesconhecido(parecer);
		}
	}

	public Parecer byId(String id) {
		Document query = new Document("id", id);
		Document parecerDocument = parecerCollection.find(query).first();
		if (parecerDocument == null)
			return null;

		return gson.fromJson(parecerDocument.toJson(), Parecer.class);
	}

	public void removeParecer(String id) {
		parecerCollection.deleteOne(new Document("id", id));
	}

	public Radoc radocById(String identificador) {
		Document query = new Document("id", identificador);
		Document resolucaoDocument = radocCollection.find(query).first();
		if (resolucaoDocument == null)
			return null;
		return gson.fromJson(resolucaoDocument.toJson(), Radoc.class);
	}

	public String persisteRadoc(Radoc radoc) {
		Document query = new Document("id", radoc.getId());
		Document resolucaoDocument = radocCollection.find(query).first();
		if (resolucaoDocument != null){
			throw new IdentificadorExistente("id");
		}

		radocCollection.insertOne((Document) JSON.parse(gson.toJson(radoc)));
		return radoc.getId();
	}

	public void removeRadoc(String identificador) {
		Document query = new Document("radocs", identificador);
		long findings = parecerCollection.count(query);
		if (findings > 0){
			throw new RuntimeException();
		}
		radocCollection.deleteOne(new Document("id", identificador));
	}
}
