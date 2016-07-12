import br.ufg.inf.es.saep.sandbox.dominio.*;
import br.ufg.inf.saep.dao.ParecerDAO;
import br.ufg.inf.saep.dao.ResolucaoDAO;
import br.ufg.inf.saep.db.DBConnection;
import com.mongodb.client.MongoDatabase;

import java.io.IOException;
import java.util.*;

@Deprecated
public class ManualTestSuite {

	static Scanner sc = new Scanner(System.in);
	static MongoDatabase db = DBConnection.getConnection().getDatabase();
	static ParecerDAO parecerDao = new ParecerDAO();
	static ResolucaoDAO resolucaoDao = new ResolucaoDAO();

	public static void persisteResolucao() throws IOException{

		Regra regra = new Regra("aula", 0, "Esta é uma regra de teste", 50, 20, "carga", null, null, null, 2, null);
		ArrayList<Regra> regras = new ArrayList<Regra>();
		regras.add(regra);
		String retorno = "";

		System.out.println("\n\nPressione enter para começar o teste de persistir resolucao.");
		System.in.read();
		retorno = resolucaoDao.persiste(new Resolucao("RES1", "RESOLUÇÃO 2016/1", "Nova Resolução", new Date(), regras));
		System.out.println("Retornou: " + retorno);

		System.out.println("\n\nPressione enter para começar o teste de persistir uma segunda resolução.");
		System.in.read();
		retorno = resolucaoDao.persiste(new Resolucao("RES2", "RESOLUÇÃO 2016/2", "Esta é uma resolucao diferente", new Date(), regras));
		System.out.println("Retornou: " + retorno);

		System.out.println("\n\nPressione enter para começar o teste de persistir uma resolucao com id existente.");
		System.in.read();
		retorno =resolucaoDao.persiste(new Resolucao("RES1", "CONSUNI", "Esta é uma resolucao diferente, com ID igual", new Date(), regras));
		System.out.println("Retornou: " + retorno);

		System.out.println("Teste concluido");

	}

	public static void buscaResolucao() throws IOException {
		Resolucao resolucao;

		System.out.println("\n\nPressione enter para começar o teste buscar lista de resolucoes.");
		System.in.read();

		ArrayList<String> resolucoes = (ArrayList<String>) resolucaoDao.resolucoes();
		System.out.println("Retornou: " + String.join(", ", resolucoes));

		System.out.println("\n\nPressione enter para começar o teste de buscar uma resolucao 'RES1'.");
		System.in.read();
		resolucao = resolucaoDao.byId("RES1");
		System.out.println("Retornou: " + resolucao);

		System.out.println("\n\nPressione enter para começar o teste de buscar uma resolucao 'RES3'.");
		System.in.read();
		resolucao = resolucaoDao.byId("RES3");
		System.out.println("Retornou: " + resolucao);
	}

	public static void removeResolucao() throws IOException {
		boolean b;
		System.out.println("\n\nPressione enter para começar o teste de remover uma resolucao 'RES2'.");
		System.in.read();
		b = resolucaoDao.remove("RES2");
		System.out.println("Retornou: " + b);

		System.out.println("\n\nPressione enter para começar o teste de remover uma resolucao 'RES3'.");
		System.in.read();
		b = resolucaoDao.remove("RES3");
		System.out.println("Retornou: " + b);
	}

	public static void manipulaTipos() throws IOException{
		Atributo attr = new Atributo("carga", "Carga horária das aulas", 1);
		Set<Atributo> atributos = new HashSet<Atributo>();
		atributos.add(attr);

		System.out.println("\n\nPressione enter para começar o teste de adicionar o tipo 'aula'.");
		System.in.read();
		resolucaoDao.persisteTipo(new Tipo("aula", "Aulas", "Tipo de relato de aula", atributos));

		System.out.println("\n\nPressione enter para começar o teste de adicionar o tipo 'tipola'.");
		System.in.read();
		resolucaoDao.persisteTipo(new Tipo("tipola", "Tipolas", "Tipo de relato de aula", atributos));

		System.out.println("\n\nPressione enter para começar o teste que busca o tipo de id 'aula'.");
		System.in.read();
		System.out.println("Retornou: " + resolucaoDao.tipoPeloCodigo("aula"));

		System.out.println("\n\nPressione enter para começar o teste que busca o tipo de id 'fora'.");
		System.in.read();
		System.out.println("Retornou: " + resolucaoDao.tipoPeloCodigo("fora"));

		System.out.println("\n\nPressione enter para começar o teste que busca o tipo por nomes que contem 'la'.");
		System.in.read();
		System.out.println("Retornou n resultados: " + resolucaoDao.tiposPeloNome("la").size());

		System.out.println("\n\nPressione enter para começar o teste que remove o tipo 'aula'.");
		System.in.read();
		try{
			resolucaoDao.removeTipo("aula");
			System.out.println("Retornou true");
		}
		catch (ResolucaoUsaTipoException e){
			System.out.println("Retornou false");
		}

		System.out.println("\n\nPressione enter para começar o teste que remove o tipo 'tipola'.");
		System.in.read();
		try{
			resolucaoDao.removeTipo("tipola");
			System.out.println("Retornou true");
		}
		catch (ResolucaoUsaTipoException e){
			System.out.println("Retornou false");
		}
	}

	public static void insereRadoc() throws IOException {
		HashMap<String, Valor> valores = new HashMap<String, Valor>();
		valores.put("carga", new Valor(20));
		Relato relato1 = new Relato("aula", valores);
		ArrayList<Relato> relatos = new ArrayList<Relato>();
		relatos.add(relato1);
		String retorno;

		System.out.println("\n\nPressione enter para começar o teste de persistir radoc.");
		System.in.read();
		try{
			parecerDao.persisteRadoc(new Radoc("radoc1", 2014, relatos));
			System.out.println("Retornou: true");
		}
		catch(IdentificadorExistente e){
			e.printStackTrace();
			System.out.println("Retornou: false");
		}

		System.out.println("\n\nPressione enter para começar o teste de persistir outro radoc.");
		System.in.read();
		try{
			parecerDao.persisteRadoc(new Radoc("radoc3", 2015, relatos));
			System.out.println("Retornou: true");
		}
		catch(IdentificadorExistente e){
			System.out.println("Retornou: false");
		}

		System.out.println("\n\nPressione enter para começar o teste de persistir outro radoc de mesma id.");
		System.in.read();
		try{
			parecerDao.persisteRadoc(new Radoc("radoc1", 2015, relatos));
			System.out.println("Retornou: true");
		}
		catch(IdentificadorExistente e){
			System.out.println("Retornou: false");
		}



	}

	public static void buscaRadoc() throws IOException {
		Radoc radoc;

		System.out.println("\n\nPressione enter para começar o teste de buscar um radoc 'radoc1'.");
		System.in.read();
		radoc = parecerDao.radocById("radoc1");
		System.out.println("Retornou: " + radoc);

		System.out.println("\n\nPressione enter para começar o teste de buscar um radoc 'radoc5'.");
		System.in.read();
		radoc = parecerDao.radocById("radoc5");
		System.out.println("Retornou: " + radoc);
	}

	public static void deletaRadoc() throws IOException{
		System.out.println("\n\nPressione enter para começar o teste de deletar o radoc 'radoc3'.");
		System.in.read();
		try{
			parecerDao.removeRadoc("radoc3");
			System.out.println("Retornou: true");
		}
		catch(RuntimeException e){
			System.out.println("Retornou: false");
		}

		System.out.println("\n\nPressione enter para começar o teste de deletar o radoc 'radoc5'.");
		System.in.read();
		try{
			parecerDao.removeRadoc("radoc5");
			System.out.println("Retornou: true");
		}
		catch(RuntimeException e){
			System.out.println("Retornou: false");
		}
	}

	public static void persisteParecer() throws IOException {
		ArrayList<String> radocs = new ArrayList<String>();
		radocs.add("radoc1");
		Pontuacao pont = new Pontuacao("carga", new Valor(40));
		ArrayList<Pontuacao> pontuacoes = new ArrayList<Pontuacao>();
		pontuacoes.add(pont);
		Nota nota = new Nota(new Pontuacao("2-1", new Valor(40)), new Pontuacao("2-2", new Valor(40)), "Nota da seção 2");
		ArrayList<Nota> notas = new ArrayList<Nota>();
		notas.add(nota);
		String retorno;

		System.out.println("\n\nPressione enter para começar o teste de persistir parecer.");
		System.in.read();
		try{
			parecerDao.persisteParecer(new Parecer("parecer1", "RES1", radocs, pontuacoes, "Uma fundamentação qualquer", notas));
			System.out.println("Retornou: true");
		}
		catch(IdentificadorExistente e){
			System.out.println("Retornou: false");
		}

		System.out.println("\n\nPressione enter para começar o teste de persistir outro parecer.");
		System.in.read();
		try{
			parecerDao.persisteParecer(new Parecer("parecer2", "RES1", radocs, pontuacoes, "Uma fundamentação qualquer diferente", notas));
			System.out.println("Retornou: true");
		}
		catch(IdentificadorExistente e){
			System.out.println("Retornou: false");
		}

		System.out.println("\n\nPressione enter para começar o teste de persistir outro parecer com id igual.");
		System.in.read();
		try{
			parecerDao.persisteParecer(new Parecer("parecer1", "RES1", radocs, pontuacoes, "Uma fundamentação qualquer diferente", notas));
			System.out.println("Retornou: true");
		}
		catch(IdentificadorExistente e){
			System.out.println("Retornou: false");
		}

		System.out.println("\n\nPressione enter para começar o teste de persistir uma nota no parecer 'parecer1'.");
		System.in.read();
		try{
			parecerDao.adicionaNota("parecer1", new Nota(new Pontuacao("3-1", new Valor(40)), new Pontuacao("3-2", new Valor(40)), "Nota da seção 3"));
			System.out.println("Retornou: true");
		}
		catch(IdentificadorDesconhecido e){
			System.out.println("Retornou: false");
		}

		System.out.println("\n\nPressione enter para começar o teste de persistir uma nota no parecer 'parecer3'.");
		System.in.read();
		try{
			parecerDao.adicionaNota("parecer3", new Nota(new Pontuacao("3-1", new Valor(40)), new Pontuacao("3-2", new Valor(40)), "Nota da seção 3"));
			System.out.println("Retornou: true");
		}
		catch(IdentificadorDesconhecido e){
			System.out.println("Retornou: false");
		}

		System.out.println("\n\nPressione enter para começar o teste de remover uma nota no parecer 'parecer1'.");
		System.in.read();
		parecerDao.removeNota("parecer1", new Pontuacao("2-1", new Valor(40)));
		System.out.println("Retornou: true");
	}

	public static void modificarParecer() throws IOException{
		System.out.println("\n\nPressione enter para começar o teste de modificar fundamentação do 'parecer1'.");
		System.in.read();
		try{
			parecerDao.atualizaFundamentacao("parecer1", "Esta é uma fundamentação editada");
			System.out.println("Retornou: true");
		}
		catch(IdentificadorDesconhecido e){
			System.out.println("Retornou: false");
		}

		System.out.println("\n\nPressione enter para começar o teste de modificar fundamentação do 'parecer5'.");
		System.in.read();
		try{
			parecerDao.atualizaFundamentacao("parecer5", "Esta é uma fundamentação editada");
			System.out.println("Retornou: true");
		}
		catch(IdentificadorDesconhecido e){
			System.out.println("Retornou: false");
		}

		System.out.println("\n\nPressione enter para começar o teste de remover o parecer2.");
		System.in.read();
		parecerDao.removeParecer("parecer2");
		System.out.println("Retornou: true");
	}

	public static void buscaParecer() throws IOException {
		Parecer parecer;

		System.out.println("\n\nPressione enter para começar o teste de buscar um parecer 'parecer1'.");
		System.in.read();
		parecer = parecerDao.byId("parecer1");
		System.out.println("Retornou: " + parecer);

		System.out.println("\n\nPressione enter para começar o teste de buscar um parecer 'parecer2'.");
		System.in.read();
		parecer = parecerDao.byId("parecer2");
		System.out.println("Retornou: " + parecer);
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Iniciando a suite de teste d ecampo da API de persistência do SAEP, implementando pelo aluno Leonardo Freitas dos Santos");
		System.out.println("Antes de iniciar cada teste, é preciso apertar enter, deste modo, é possível pausar entre cada teste e verificar o estado do banco (Usando MongoShell, Robomongo, etc)");
		db.drop();
		System.out.println("Drop na database realizado. Host: localhost:27017 - Banco: saep");
		persisteResolucao();
		buscaResolucao();
		removeResolucao();
		manipulaTipos();
		insereRadoc();
		buscaRadoc();
		deletaRadoc();
		persisteParecer();
		modificarParecer();
		buscaParecer();
		System.out.println("Fim dos testes, aperte enter para finalizar");
		System.in.read();
	}
}

