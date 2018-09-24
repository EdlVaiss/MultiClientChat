package com.vaiss.edl.propertiesholder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.vaiss.edl.exceptions.PropertiesException;

public class PropertiesHolder {
	private Properties properties = new Properties();
	
	public PropertiesHolder() throws PropertiesException {
		try {
			properties.load(new FileReader("config.properties"));
		} catch (FileNotFoundException e) {
			System.out.println("Can't find properties file!");
			throw new PropertiesException();
		} catch (IOException e) {
			System.out.println("Something went wrong while reading properties");
			throw new PropertiesException();
		}
	}
	
	public Properties getProperties() {
		return properties;
	}
}
