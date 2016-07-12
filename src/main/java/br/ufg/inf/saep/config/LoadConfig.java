package br.ufg.inf.saep.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Leonardo on 12/07/2016.
 */
public class LoadConfig {

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
