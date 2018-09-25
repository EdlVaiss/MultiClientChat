package com.vaiss.edl.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import com.vaiss.edl.client.ChatWriter;
import com.vaiss.edl.propertiesholder.PropertiesHolder;

public class EmbededChatServer {

	public void start() {
		System.out.println("CremlinChatServer started.");
		Properties properties = PropertiesHolder.getProperties();
		if (properties.isEmpty()) {
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
			System.out.println("Failed to establish Serversocket!");
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
