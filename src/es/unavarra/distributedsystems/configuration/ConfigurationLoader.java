package es.unavarra.distributedsystems.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import es.unavarra.distributedsystems.common.Logger;

public class ConfigurationLoader {
	
	private Properties load() throws IOException {
		return this.load("cfg/config.txt");
	}
	
	private Properties load(String filename) throws IOException {
		Properties defaultProperties = new Properties();
		FileInputStream in = new FileInputStream(filename);
		defaultProperties.load(in);
		in.close();
		Logger.log("Loaded configuration parameters from '" + filename + "': " + defaultProperties.toString());
		return defaultProperties;
	}
	
	public Properties loadOrUseDefault(String filename) throws IOException {
		Properties properties;
		try {
			properties = this.load(filename);
		}
		catch (Exception e) {
			properties = this.load();
		}
		return properties;
	}
}
