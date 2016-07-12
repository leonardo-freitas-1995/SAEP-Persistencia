import br.ufg.inf.es.saep.sandbox.dominio.*;
import br.ufg.inf.saep.dao.ParecerDAO;
import br.ufg.inf.saep.dao.ResolucaoDAO;
import br.ufg.inf.saep.db.DBConnection;
import com.mongodb.client.MongoDatabase;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;


public class TestSuite {

	private static ResolucaoDAO resolucaoDao;
	private static ParecerDAO parecerDao;

	@BeforeClass
	public static void testeSetup(){
		MongoDatabase db = DBConnection.getConnection().getDatabase();

		// Limpando banco
		db.drop();

		// Inicializando DAOs
		resolucaoDao = new ResolucaoDAO();
		parecerDao = new ParecerDAO();
	}


	@Test
	public void testeResolucaoDAO(){
		Regra regra = new Regra("carga", 0, "Esta é uma regra de teste", 50, 20, null, null, null, "aula", 2, null);
		ArrayList<Regra> regras = new ArrayList<Regra>();
		regras.add(regra);
		Resolucao res1 = new Resolucao("RES1", "RESOLUÇÃO 2016/1", "Nova Resolução", new Date(), regras);
		Resolucao res2 = new Resolucao("RES2", "RESOLUÇÃO 2016/2", "Esta é uma resolucao diferente", new Date(), regras);
		Resolucao res3 = new Resolucao("RES1", "CONSUNI", "Esta é uma resolucao diferente, com ID igual", new Date(), regras);
		String retorno;
		Resolucao resolucao;

		retorno = resolucaoDao.persiste(res1);
		Assert.assertEquals(res1.getId(), retorno);

		retorno = resolucaoDao.persiste(res2);
		Assert.assertEquals(res2.getId(), retorno);

		try{
			resolucaoDao.persiste(res3);
			Assert.fail("expected IdentificadorExistente");
		} catch(IdentificadorExistente ignored){}

		ArrayList<String> resolucoes = (ArrayList<String>) resolucaoDao.resolucoes();
		Assert.assertEquals(2, resolucoes.size());

		resolucao = resolucaoDao.byId("RES1");
		Assert.assertEquals("RES1", resolucao.getId());

		resolucao = resolucaoDao.byId("RES3");
		Assert.assertEquals(null, resolucao);

		Assert.assertTrue(resolucaoDao.remove("RES2"));

		Assert.assertFalse(resolucaoDao.remove("RES3"));

		Atributo attr = new Atributo("carga", "Carga horária das aulas", 1);
		Set<Atributo> atributos = new HashSet<Atributo>();
		atributos.add(attr);

		Tipo tipo1 = new Tipo("aula", "Aulas", "Tipo de relato de aula", atributos);
		Tipo tipo2 = new Tipo("tipola", "Tipolas", "Tipo de relato de aula", atributos);
		Tipo tipo3 = new Tipo("aula", "Aulas1", "Tipo de relato de aula2", atributos);

		try{
			resolucaoDao.persisteTipo(tipo1);
		} catch(IdentificadorExistente e){
			Assert.fail("not expected IdentificadorExistente");
		}

		try{
			resolucaoDao.persisteTipo(tipo2);
		} catch(IdentificadorExistente e){
			Assert.fail("not expected IdentificadorExistente");
		}

		try{
			resolucaoDao.persisteTipo(tipo3);
			Assert.fail("expected IdentificadorExistente");
		} catch(IdentificadorExistente ignored){}

		Tipo tipo;
		List<Tipo> tipos;

		tipo = resolucaoDao.tipoPeloCodigo("aula");
		Assert.assertEquals("aula", tipo.getId());

		tipo = resolucaoDao.tipoPeloCodigo("fora");
		Assert.assertEquals(null, tipo);

		tipos = resolucaoDao.tiposPeloNome("la");
		Assert.assertEquals(2, tipos.size());

		try{
			resolucaoDao.removeTipo("aula");
			Assert.fail("expected ResolucaoUsaTipoException");
		}
		catch (ResolucaoUsaTipoException ignored){}

		try{
			resolucaoDao.removeTipo("tipola");
		} catch(IdentificadorExistente e){
			Assert.fail("not expected ResolucaoUsaTipoException");
		}
	}

	@Test
	public void testParecerDAO(){
		ArrayList<String> radocs = new ArrayList<String>();
		radocs.add("radoc1");
		Pontuacao pont = new Pontuacao("carga", new Valor(40));
		ArrayList<Pontuacao> pontuacoes = new ArrayList<Pontuacao>();
		pontuacoes.add(pont);
		Nota nota = new Nota(new Pontuacao("2-1", new Valor(40)), new Pontuacao("2-2", new Valor(40)), "Nota da seção 2");
		ArrayList<Nota> notas = new ArrayList<Nota>();
		notas.add(nota);
		Parecer parecer;
		Parecer parecer1 = new Parecer("parecer1", "RES1", radocs, pontuacoes, "Uma fundamentação qualquer", notas);
		Parecer parecer2 = new Parecer("parecer2", "RES1", radocs, pontuacoes, "Uma fundamentação qualquer diferente", notas);
		Parecer parecer3 = new Parecer("parecer1", "RES1", radocs, pontuacoes, "Uma fundamentação qualquer diferente", notas);
		Nota nota1 = new Nota(new Pontuacao("3-1", new Valor(40)), new Pontuacao("3-2", new Valor(40)), "Nota da seção 3");
		Nota nota2 = new Nota(new Pontuacao("3-1", new Valor(40)), new Pontuacao("3-2", new Valor(40)), "Nota da seção 3");
		Avaliavel avaliavel = new Pontuacao("2-1", new Valor(40));
		String retorno;

		try{
			parecerDao.persisteParecer(parecer1);
		}
		catch(IdentificadorExistente e){
			Assert.fail("not expected IdentificadorExistente");
		}

		try{
			parecerDao.persisteParecer(parecer2);
		}
		catch(IdentificadorExistente e){
			Assert.fail("not expected IdentificadorExistente");
		}

		try{
			parecerDao.persisteParecer(parecer3);
			Assert.fail("expected IdentificadorExistente");
		}
		catch(IdentificadorExistente ignored){}

		try{
			parecerDao.adicionaNota("parecer1", nota1);
		}
		catch(IdentificadorDesconhecido e){
			Assert.fail("not expected IdentificadorExistente");
		}

		try{
			parecerDao.adicionaNota("parecer3", nota2);
			Assert.fail("expected IdentificadorExistente");
		}
		catch(IdentificadorDesconhecido ignored){}

		parecer = parecerDao.byId("parecer1");
		Assert.assertEquals(2, parecer.getNotas().size());

		parecerDao.removeNota("parecer1", avaliavel);
		parecer = parecerDao.byId("parecer1");
		Assert.assertEquals(1, parecer.getNotas().size());

		HashMap<String, Valor> valores = new HashMap<String, Valor>();
		valores.put("carga", new Valor(20));
		Relato relato1 = new Relato("aula", valores);
		ArrayList<Relato> relatos = new ArrayList<Relato>();
		relatos.add(relato1);
		Radoc radoc1 = new Radoc("radoc1", 2014, relatos);
		Radoc radoc2 = new Radoc("radoc3", 2015, relatos);
		Radoc radoc3 = new Radoc("radoc1", 2015, relatos);
		Radoc radoc;

		try{
			parecerDao.persisteRadoc(radoc1);
		}
		catch(IdentificadorExistente e){
			Assert.fail("not expected IdentificadorExistente");
		}

		try{
			parecerDao.persisteRadoc(radoc2);
		}
		catch(IdentificadorExistente e){
			Assert.fail("not expected IdentificadorExistente");
		}

		try{
			parecerDao.persisteRadoc(radoc3);
			Assert.fail("expected IdentificadorExistente");
		}
		catch(IdentificadorExistente ignored){}

		radoc = parecerDao.radocById("radoc1");
		Assert.assertEquals("radoc1", radoc.getId());

		radoc = parecerDao.radocById("radoc5");
		Assert.assertEquals(null, radoc);

		try{
			parecerDao.removeRadoc("radoc1");
			Assert.fail("expected ExisteParecerReferenciandoRadoc");
		}
		catch(ExisteParecerReferenciandoRadoc ignored){}

		try{
			parecerDao.removeRadoc("radoc3");
		}
		catch(ExisteParecerReferenciandoRadoc e){
			Assert.fail("not expected ExisteParecerReferenciandoRadoc");
		}

		try{
			parecerDao.atualizaFundamentacao("parecer1", "Esta é uma fundamentação editada");
		}
		catch(IdentificadorDesconhecido e){
			Assert.fail("not expected IdentificadorDesconhecido");
		}

		try{
			parecerDao.atualizaFundamentacao("parecer5", "Esta é uma fundamentação editada");
			Assert.fail("expected IdentificadorDesconhecido");
		}
		catch(IdentificadorDesconhecido ignored){}

		parecerDao.removeParecer("parecer2");

		parecer = parecerDao.byId("parecer1");
		Assert.assertEquals("parecer1", parecer.getId());

		parecer = parecerDao.byId("parecer2");
		Assert.assertEquals(null, parecer);
	}

}
