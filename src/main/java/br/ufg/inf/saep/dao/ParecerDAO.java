package br.ufg.inf.saep.dao;

import br.ufg.inf.es.saep.sandbox.dominio.*;
import br.ufg.inf.saep.config.DBConfig;
import br.ufg.inf.saep.db.DBConnection;
import br.ufg.inf.saep.tools.MongoDocumentSerializer;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.BasicBSONList;

import java.util.ArrayList;


public class ParecerDAO implements ParecerRepository {
	private static ParecerDAO instance = new ParecerDAO();
	private MongoDatabase db = DBConnection.getConnection().getDatabase();
	private MongoCollection<Document> radocCollection = db.getCollection(DBConfig.RADOC_COLLECTION);
	private MongoCollection<Document> parecerCollection = db.getCollection(DBConfig.PARECER_COLLECTION);
	private MongoDocumentSerializer mds = new MongoDocumentSerializer();

	public static ParecerDAO getInstance() {
		return instance;
	}

	private ParecerDAO() {
	}

	public void adicionaNota(String id, Nota nota) {
		Document query = new Document("id", id);
		Document notaDocument = new Document("notas", mds.toDocument(nota, "Nota"));
		Document avaliavelJSON = mds.toDocument(nota.getItemOriginal(), "Avaliavel");
		Document parecerDocument = parecerCollection.find(query).first();
		if (parecerDocument == null){
			throw new IdentificadorDesconhecido(id);
		}
		BasicBSONList newNotas = new BasicBSONList();
		ArrayList<Document> notasDocument = (ArrayList<Document>) parecerDocument.get("notas");
		for (Document notaDoc : notasDocument){
			Nota notaParecer = mds.fromDocument(notaDoc, Nota.class);
			Document originalJSON = mds.toDocument(notaParecer.getItemOriginal(), "Avaliavel");
			if (!originalJSON.toJson().equals(avaliavelJSON.toJson())){
				newNotas.add(mds.toDocument(notaParecer, "Nota"));
			}
			else{
				newNotas.add(notaDocument);
			}
		}
		Document newNotasDocument = new Document("notas", newNotas);
		parecerCollection.updateOne(query, new Document("$set", newNotasDocument));

	}

	public void removeNota(String id, Avaliavel original) {
		Document query = new Document("id", id);
		Document avaliavelJSON = mds.toDocument(original, "Avaliavel");
		BasicBSONList newNotas = new BasicBSONList();
		Document parecerDocument = parecerCollection.find(query).first();
		ArrayList<Document> notasDocument = (ArrayList<Document>) parecerDocument.get("notas");
		for (Document notaDoc : notasDocument){
			Nota nota = mds.fromDocument(notaDoc, Nota.class);
			Document originalJSON = mds.toDocument(nota.getItemOriginal(), "Avaliavel");
			if (!originalJSON.toJson().equals(avaliavelJSON.toJson())){
				newNotas.add(mds.toDocument(nota, "Nota"));
			}
		}
		Document newNotasDocument = new Document("notas", newNotas);
		parecerCollection.updateOne(query, new Document("$set", newNotasDocument));
	}

	public void persisteParecer(Parecer parecer) {
		long findings = parecerCollection.count(new Document("id", parecer.getId()));
		if (findings > 0){
			throw new IdentificadorExistente("id");
		}
		parecerCollection.insertOne(mds.toDocument(parecer, "Parecer"));
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

		return mds.fromDocument(parecerDocument, Parecer.class);
	}

	public void removeParecer(String id) {
		parecerCollection.deleteOne(new Document("id", id));
	}

	public Radoc radocById(String identificador) {
		Document query = new Document("id", identificador);
		Document resolucaoDocument = radocCollection.find(query).first();
		if (resolucaoDocument == null)
			return null;
		return mds.fromDocument(resolucaoDocument, Radoc.class);
	}

	public String persisteRadoc(Radoc radoc) throws IdentificadorExistente{
		Document query = new Document("id", radoc.getId());
		Document resolucaoDocument = radocCollection.find(query).first();
		if (resolucaoDocument != null){
			throw new IdentificadorExistente("id");
		}

		radocCollection.insertOne(mds.toDocument(radoc, "Radoc"));
		return radoc.getId();
	}

	public void removeRadoc(String identificador) throws RuntimeException {
		Document query = new Document("id", identificador);
		long findings = radocCollection.count(query);
		if (findings == 0){
			throw new RuntimeException();
		}
		radocCollection.deleteOne(new Document("id", identificador));
	}
}
