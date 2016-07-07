import br.ufg.inf.es.saep.sandbox.dominio.*;
import br.ufg.inf.saep.dao.ParecerDAO;
import br.ufg.inf.saep.dao.ResolucaoDAO;
import br.ufg.inf.saep.db.DBConnection;
import com.mongodb.client.MongoDatabase;

import java.io.IOException;
import java.util.*;

public class TestSuite {

	static Scanner sc = new Scanner(System.in);
	static MongoDatabase db = DBConnection.getConnection().getDatabase();
	static ParecerDAO parecerDao = ParecerDAO.getInstance();
	static ResolucaoDAO resolucaoDao = ResolucaoDAO.getInstance();

	public static void persistirResolucao() throws IOException{

		Regra regra = new Regra(0, "Esta é uma regra de teste", 50, 20, "carga", null, null, null, "aula", 2, null);
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

	public static void buscarResolucao() throws IOException {
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

	public static void removerResolucao() throws IOException {
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

	public static void main(String[] args) throws IOException {
		System.out.println("Iniciando a suite de teste da API de persistência do SAEP, implementando pelo aluno Leonardo Freitas dos Santos");
		System.out.println("Antes de inicar cada teste, é preciso apertar enter, deste modo, é possível pausar entre cada teste e verificar o estado do banco (Usando MongoShell, Robomongo, etc)");
		db.drop();
		System.out.println("Drop na database realizado. Host: localhost:27017 - Banco: saep");
		persistirResolucao();
		buscarResolucao();
		removerResolucao();
		manipulaTipos();
		System.out.println("Fim dos testes, aperte enter para finalizar");
		System.in.read();
	}
}
