package br.ufg.inf.saep.tools;

import br.ufg.inf.es.saep.sandbox.dominio.Avaliavel;
import br.ufg.inf.es.saep.sandbox.dominio.Nota;
import br.ufg.inf.es.saep.sandbox.dominio.Parecer;
import br.ufg.inf.es.saep.sandbox.dominio.Pontuacao;
import com.google.gson.Gson;
import com.google.gson.internal.Primitives;
import org.bson.Document;
import org.bson.types.BasicBSONList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.lang.String;

/**
 * Classe de parse e unparse entre Objects e Documents JSON.
 * Utiliza Gson para transformar os objetos.
 *
 * @see Gson
 * @see BasicBSONList
 * @see Document
 */
public class MongoDocumentSerializer {

	/**
	 * Instancia do Gson para transformar objetos em String JSON.
	 */
	private final Gson gson = new Gson();

	/**
	 * Construtor padrão de MongoDocumentSerializer
	 */
	public MongoDocumentSerializer() {}

	/**
	 * Parser de Array de Objets para uma Array de Documents
	 * @param arr ArrayList de Objects a serem pareseados. Este objeto não é modificado.
	 * @return A ArrayList de {@code Document} no padrão JSON.
	 */
	private ArrayList<Document> parseArray(ArrayList<Object> arr){
		ArrayList<Document> newArr = new ArrayList<Document>();
		for (Object o : arr){
			newArr.add(this.toDocumentDefault(o));
		}

		return newArr;
	}

	/**
	 * Serializador default de Objects para Document
	 * @param o Objeto a ser serializado.
	 * @return O {@code Document} gerado do objeto de parametro.
	 */
	private Document toDocumentDefault(Object o){
		return Document.parse(gson.toJson(o));
	}

	/**
	 * Deserializador default de Document para Objects
	 * @param document Document a ser deserializado.
	 * @param classOfT Classe do TypeToken.
	 * @param <T> TypeToken da Classe do Objeto que será instanciado com o JSON fornecido.
	 * @return O Objeto deserializado de acordo com o {@code Document} JSON e o TypeToken informado.
	 */
	private <T> T fromDocumentDefault(Document document, Class<T> classOfT){
		return gson.fromJson(document.toJson(), classOfT);
	}

	/**
	 * Estratégia especifica de serialização para {@code Nota}
	 * @param o O objeto Nota
	 * @return O {@code Document} gerado na serialização.
	 */
	private Document toDocumentNota(Object o){
		Nota nota = (Nota) o;
		Document originalDoc = Document.parse(gson.toJson(nota.getItemOriginal()))
				.append("javaClass", nota.getItemOriginal().getClass().getName());
		Document novoDoc = Document.parse(gson.toJson(nota.getItemNovo()))
				.append("javaClass", nota.getItemNovo().getClass().getName());
		return new Document("original", originalDoc)
				.append("novo", novoDoc)
				.append("justificativa", nota.getJustificativa());
	}

	/**
	 * Estratégia especifica de deserialização para {@code Nota}
	 * @param document Document com JSON da Nota.
	 *  @param classOfT Classe de Nota
	 * @param <T> TypeToken da Classe Nota
	 * @return O objeto Nota (Sem cast explicito) resultado da deserialização.
	 */
	private <T> T fromDocumentNota(Document document, Class<T> classOfT){
		String justificativa = document.getString("justificativa");
		Document originalDoc = (Document) document.get("original");
		Document novoDoc = (Document) document.get("novo");
		try {
			Avaliavel original = (Avaliavel) gson.fromJson(originalDoc.toJson(), Class.forName(originalDoc.getString("javaClass")));
			Avaliavel novo = (Avaliavel) gson.fromJson(novoDoc.toJson(), Class.forName(novoDoc.getString("javaClass")));
			return Primitives.wrap(classOfT).cast(new Nota(original, novo, justificativa));
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * Estratégia especifica de serialização para {@code Parecer}
	 * @param o O objeto Parecer
	 * @return O {@code Document} gerado na serialização.
	 */
	private Document toDocumentParecer(Object o){
		Parecer parecer = (Parecer) o;
		BasicBSONList radocs = new BasicBSONList();
		for (String radoc : parecer.getRadocs()){
			radocs.add(radoc);
		}

		BasicBSONList pontuacoes = new BasicBSONList();
		for (Pontuacao pontuacao : parecer.getPontuacoes()){
			pontuacoes.add(this.toDocumentDefault(pontuacao));
		}

		Document parecerDoc = new Document("id", parecer.getId())
									.append("resolucao", parecer.getResolucao())
									.append("radocs", radocs)
									.append("pontuacoes", pontuacoes)
									.append("fundamentacao", parecer.getFundamentacao());
		BasicBSONList notas = new BasicBSONList();
		for (Nota nota : parecer.getNotas()){
			notas.add(this.toDocumentNota(nota));
		}

		parecerDoc.append("notas", notas);
		return parecerDoc;
	}

	/**
	 * Estratégia especifica de deserialização para {@code Parecer}
	 * @param document Document com JSON da Parecer.
	 *  @param classOfT Classe de Parecer
	 * @param <T> TypeToken da Classe Parecer
	 * @return O objeto Parecer (Sem cast explicito) resultado da deserialização.
	 */
	private <T> T fromDocumentParecer(Document document, Class<T> classOfT){
		String id = document.getString("id");
		String resolucao = document.getString("resolucao");
		String fundamentacao = document.getString("fundamentacao");
		ArrayList<String> radocs = (ArrayList<String>) document.get("radocs");
		ArrayList<Document> pontuacoesDoc = (ArrayList<Document>) document.get("pontuacoes");
		ArrayList<Pontuacao> pontuacoes = new ArrayList<Pontuacao>();
		for (Document pontuacaoDoc : pontuacoesDoc){
			pontuacoes.add(this.fromDocumentDefault(pontuacaoDoc, Pontuacao.class));
		}

		ArrayList<Document> notasDoc = (ArrayList<Document>) document.get("notas");
		ArrayList<Nota> notas = new ArrayList<Nota>();
		for (Document notaDoc : notasDoc){
			notas.add(this.fromDocumentNota(notaDoc, Nota.class));
		}

		return Primitives.wrap(classOfT).cast(new Parecer(id, resolucao, radocs, pontuacoes, fundamentacao, notas));
	}

	/**
	 * Estratégia publica de serialização, que irá esolher entre a estratégia default ou uma estratégia específica.
	 * @param o O objeto a ser serializado.
	 * @return O {@code Document} gerado na serialização.
	 */
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

	/**
	 * Estratégia publica de deserialização, que irá esolher entre a estratégia default ou uma estratégia específica.
	 * @param document Document a ser deserializado.
	 * @param classOfT Classe do TypeToken.
	 * @param <T> TypeToken da Classe do Objeto que será instanciado com o JSON fornecido.
	 * @return O Objeto deserializado de acordo com o {@code Document} JSON e o TypeToken informado.
	 */
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
