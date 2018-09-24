package com.vaiss.edl.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import com.vaiss.edl.client.ChatWriter;
import com.vaiss.edl.exceptions.PropertiesException;
import com.vaiss.edl.propertiesholder.PropertiesHolder;

public class EmbededChatServer {

	// public static void main(String[] args) {
	public void start() {
		Properties properties;
		try {
			properties = new PropertiesHolder().getProperties();
		} catch (PropertiesException e1) {
			return;
		}
		try (ServerSocket serverSocket = new ServerSocket(Integer.valueOf(properties.getProperty("port")))) {
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
