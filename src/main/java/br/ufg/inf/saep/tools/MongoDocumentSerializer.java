package br.ufg.inf.saep.tools;

import br.ufg.inf.es.saep.sandbox.dominio.Avaliavel;
import br.ufg.inf.es.saep.sandbox.dominio.Nota;
import br.ufg.inf.es.saep.sandbox.dominio.Parecer;
import br.ufg.inf.es.saep.sandbox.dominio.Pontuacao;
import com.google.gson.Gson;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;
import org.bson.Document;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Leonardo on 07/07/2016.
 */
public class MongoDocumentSerializer {
	private final Gson gson = new Gson();

	public MongoDocumentSerializer() {
		this.getClass().getName();
	}

	private Document toDocumentDefault(Object o){
		return Document.parse(gson.toJson(o));
	}

	private <T> T fromDocumentDefault(Document document, Class<T> classOfT){
		return gson.fromJson(document.toJson(), classOfT);
	}

	private Document toDocumentNota(Object o){
		Nota nota = (Nota) o;
		Document originalDoc = Document.parse(gson.toJson(nota.getItemOriginal()))
				.append("Class", nota.getItemOriginal().getClass().getName());
		Document novoDoc = Document.parse(gson.toJson(nota.getItemNovo()))
				.append("Class", nota.getItemNovo().getClass().getName());
		return new Document("original", originalDoc)
				.append("novo", novoDoc)
				.append("justificativa", nota.getJustificativa());
	}

	private <T> T fromDocumentNota(Document document, Class<T> classOfT){
		String justificativa = document.getString("justificativa");
		Document originalDoc = (Document) document.get("original");
		Document novoDoc = (Document) document.get("novo");
		try {
			Avaliavel original = (Avaliavel) gson.fromJson(originalDoc.toJson(), Class.forName(originalDoc.getString("Class")));
			Avaliavel novo = (Avaliavel) gson.fromJson(novoDoc.toJson(), Class.forName(novoDoc.getString("Class")));
			return Primitives.wrap(classOfT).cast(new Nota(original, novo, justificativa));
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	private Document toDocumentParecer(Object o){
		Parecer parecer = (Parecer) o;
		Document parecerDoc = new Document("id", parecer.getId())
									.append("resolucao", parecer.getResolucao())
									.append("radocs", this.toDocumentDefault(parecer.getRadocs()))
									.append("pontuacoes", this.toDocumentDefault(parecer.getPontuacoes()))
									.append("fundamentacao", parecer.getFundamentacao());
		String notas = "[";
		int i = 0;
		for (Nota nota : parecer.getNotas()){
			if (i > 0)
				notas += ", ";
			notas += this.toDocumentNota(nota).toJson();
			i++;
		}
		notas += "]";
		parecerDoc.append("notas", Document.parse(notas));
		return parecerDoc;
	}

	private <T> T fromDocumentParecer(Document document, Class<T> classOfT){
		Type stringListType = new TypeToken<ArrayList<String>>(){}.getType();
		Type pontuacaoListType = new TypeToken<ArrayList<Pontuacao>>(){}.getType();
		String id = document.getString("id");
		String resolucao = document.getString("resolucao");
		String fundamentacao = document.getString("fundamentacao");
		Document radocsDoc = (Document) document.get("radocs");
		ArrayList<String> radocs = gson.fromJson(radocsDoc.toJson(), stringListType);
		Document pontuacoesDoc = (Document) document.get("pontuacoes");
		ArrayList<Pontuacao> pontuacoes = gson.fromJson(pontuacoesDoc.toJson(), pontuacaoListType);
		ArrayList<Document> notasDocs = (ArrayList<Document>) document.get("notas");
		ArrayList<Nota> notas = new ArrayList<Nota>();
		for (Document notaDoc : notasDocs){
			Nota nota = this.fromDocumentNota(notaDoc, Nota.class);
			notas.add(nota);
		}
		Parecer parecer = new Parecer(id, resolucao, radocs, pontuacoes, fundamentacao, notas);
		return Primitives.wrap(classOfT).cast(parecer);
	}

	public Document toDocument(Object o, String className){
		Method[] allMethods = this.getClass().getDeclaredMethods();
		for (Method m : allMethods) {
			if (m.getName().equals("toDocument" + className)){
				try {
					return (Document) m.invoke(this, o);
				} catch (IllegalAccessException e) {
					return null;
				} catch (InvocationTargetException e) {
					return null;
				}
			}
		}
		return toDocumentDefault(o);
	}

	public <T> T fromDocument(Document document, Class<T> classOfT){
		Method[] allMethods = this.getClass().getDeclaredMethods();
		for (Method m : allMethods) {
			if (m.getName().equals("fromDocument" + classOfT.getSimpleName())){
				try {
					return Primitives.wrap(classOfT).cast(m.invoke(this, document, classOfT));
				} catch (IllegalAccessException e) {
					return null;
				} catch (InvocationTargetException e) {
					return null;
				}
			}
		}
		return fromDocumentDefault(document, classOfT);
	}
}
