package com.vaiss.edl.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.vaiss.edl.client.ChatWriter;
import com.vaiss.edl.propertiesholder.PropertiesHolder;

public class EmbededChatServer {
	private static Logger log = Logger.getLogger(EmbededChatServer.class);
	
	public void start() {
		System.out.println("CremlinChatServer started.");
		Properties properties = PropertiesHolder.getProperties();
		if (properties.isEmpty()) {
			log.error("Can't find or read properties file");
			System.out.println("Shutdown server because of properties issue!");
			return;
		}

		try (ServerSocket serverSocket = new ServerSocket(Integer.valueOf(properties.getProperty("port")))) {
			System.out.println("Waiting for incoming connection...");
			Socket socket = serverSocket.accept();
			Thread writer = new Thread(new ChatWriter(socket));
			writer.start();
			writer.join();
		} catch (IOException e) {
			log.fatal("Failed to establish Serversocket!");
			System.out.println("Failed to establish Serversocket!");
		} catch (InterruptedException e) {
			log.fatal("Multithreading issue");
		}

	}

}
