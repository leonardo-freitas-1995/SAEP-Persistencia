package br.ufg.inf.saep.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Leonardo on 12/07/2016.
 */
public class LoadConfig {

	private final Properties prop = new Properties();

	public LoadConfig(String config) {
		InputStream input;
		try {
			input = new FileInputStream("./" + config);
			this.prop.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public String getConfig(String config){
		return this.prop.getProperty(config);
	}
}
