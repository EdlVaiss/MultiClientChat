package com.vaiss.edl.propertiesholder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;


public class PropertiesHolder {
	private static Properties properties = new Properties();
	
	static {
		try {
			properties.load(new FileReader("config.properties"));
		} catch (FileNotFoundException e) {
			System.out.println("Can't find properties file!");
		} catch (IOException e) {
			System.out.println("Something went wrong while reading properties");
		}
	}
	
	public static Properties getProperties() {
		return properties;
	}
}
