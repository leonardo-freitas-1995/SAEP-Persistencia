package br.ufg.inf.saep.dao;

import br.ufg.inf.es.saep.sandbox.dominio.*;
import br.ufg.inf.saep.config.DBConfig;
import br.ufg.inf.saep.db.DBConnection;
import br.ufg.inf.saep.tools.MongoDocumentSerializer;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class ResolucaoDAO implements ResolucaoRepository {
	private MongoDatabase db = DBConnection.getConnection().getDatabase();
	private MongoCollection<Document> resolucaoCollection = db.getCollection(DBConfig.RESOLUCAO_COLLECTION);
	private MongoCollection<Document> tipoCollection = db.getCollection(DBConfig.TIPO_COLLECTION);
	private MongoDocumentSerializer mds = new MongoDocumentSerializer();

	public ResolucaoDAO(){

	}

	public Resolucao byId(String id) {
		Document query = new Document("id", id);
		Document resolucaoDocument = resolucaoCollection.find(query).first();
		if (resolucaoDocument == null)
			return null;
		return mds.fromDocument(resolucaoDocument, Resolucao.class);
	}

	public String persiste(Resolucao resolucao) {
		if (resolucao.getId() == null)
			throw new CampoExigidoNaoFornecido("id");

		Document query = new Document("id", resolucao.getId());
		Document resolucaoDocument = resolucaoCollection.find(query).first();
		if (resolucaoDocument != null)
			throw new IdentificadorExistente("id");

		resolucaoCollection.insertOne(mds.toDocument(resolucao, "Resolucao"));
		return resolucao.getId();
	}

	public boolean remove(String identificador) {
		Document query = new Document("id", identificador);
		Document resolucaoDocument = resolucaoCollection.findOneAndDelete(query);
		return resolucaoDocument != null;

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
		Document query = new Document("id", tipo.getId());
		Document tipoDocument = tipoCollection.find(query).first();
		if (tipoDocument != null)
			throw new IdentificadorExistente("id");

		tipoCollection.insertOne(mds.toDocument(tipo, "Tipo"));
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
		return mds.fromDocument(tipoDocument, Tipo.class);
	}

	public List<Tipo> tiposPeloNome(String nome) {
		ArrayList<Tipo> tipos = new ArrayList<Tipo>();
		Document query = new Document("nome", Pattern.compile(nome));
		FindIterable<Document> search = tipoCollection.find(query);
		for (Document tipoDocument : search){
			tipos.add(mds.fromDocument(tipoDocument, Tipo.class));
		}
		return tipos;
	}

}
