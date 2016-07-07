package br.ufg.inf.saep.dao;

import br.ufg.inf.es.saep.sandbox.dominio.*;
import br.ufg.inf.saep.db.DBConnection;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Leonardo on 06/07/2016.
 */
public class ResolucaoDAO implements ResolucaoRepository {
	private static ResolucaoDAO instance = new ResolucaoDAO();
	private MongoDatabase db = DBConnection.getConnection().getDatabase();
	private MongoCollection<Document> resolucaoCollection = db.getCollection("resolucao");
	private MongoCollection<Document> tipoCollection = db.getCollection("tipo");
	private Gson gson = new Gson();

	public static ResolucaoDAO getInstance() {
		return instance;
	}

	private ResolucaoDAO() {
	}

	public Resolucao byId(String id) {
		Document query = new Document("id", id);
		Document resolucaoDocument = resolucaoCollection.find(query).first();
		if (resolucaoDocument == null)
			return null;
		return gson.fromJson(resolucaoDocument.toJson(), Resolucao.class);
	}

	public String persiste(Resolucao resolucao) {
		if (resolucao.getId() == null)
			throw new CampoExigidoNaoFornecido("id");

		Document query = new Document("id", resolucao.getId());
		Document resolucaoDocument = resolucaoCollection.find(query).first();
		if (resolucaoDocument != null)
			return null;

		resolucaoCollection.insertOne((Document) JSON.parse(gson.toJson(resolucao)));
		return resolucao.getId();
	}

	public boolean remove(String identificador) {
		Document query = new Document("id", identificador);
		Document resolucaoDocument = resolucaoCollection.findOneAndDelete(query);
		if (resolucaoDocument == null)
			return false;

		return true;
	}

	public List<String> resolucoes() {
		ArrayList<String> ids = new ArrayList<String>();
		FindIterable<Document> search = resolucaoCollection.find();
		for (Document resolucao : search){
			ids.add(resolucao.getString("id"));
		}
		return ids;
	}

	public void persisteTipo(Tipo tipo) {
		tipoCollection.insertOne((Document) JSON.parse(gson.toJson(tipo)));
	}

	public void removeTipo(String codigo) {
		Document query = new Document("regras.tipoRelato", codigo);
		long findings = resolucaoCollection.count(query);
		if (findings > 0){
			throw new ResolucaoUsaTipoException(codigo);
		}
		tipoCollection.deleteOne(new Document("id", codigo));
	}

	public Tipo tipoPeloCodigo(String codigo) {
		Document query = new Document("id", codigo);
		Document tipoDocument = tipoCollection.find(query).first();
		if (tipoDocument == null)
			return null;
		return gson.fromJson(tipoDocument.toJson(), Tipo.class);
	}

	public List<Tipo> tiposPeloNome(String nome) {
		ArrayList<Tipo> tipos = new ArrayList<Tipo>();
		Document query = new Document("id", Pattern.compile(nome));
		FindIterable<Document> search = resolucaoCollection.find(query);
		for (Document tipoDocument : search){
			tipos.add(gson.fromJson(tipoDocument.toJson(), Tipo.class));
		}
		return tipos;
	}

}
