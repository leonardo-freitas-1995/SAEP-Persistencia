package br.ufg.inf.saep.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Carrega em tempo de execução um arquivo e expoe suas propriedades para uso
 *
 * @see Properties
 */
public class LoadConfig {

	/**
	 * Carrega um arquivo de propriedades contendo configurações.
	 *
	 * @param config Nome do arquivo de configuração, que se encontra na pasta raiz do projeto.
	 * @return O {@code Properties} do arquivo na localização informada. Retorna null caso não encontre o arquivo.
	 */
	public static Properties loadFile(String config) {
		InputStream input;
		Properties prop = null;
		try {
			input = new FileInputStream("./" + config);
			prop = new Properties();
			prop.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return prop;
	}
}
