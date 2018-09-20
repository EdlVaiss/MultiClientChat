package com.vaiss.edl.client;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

public class ChatClient {
	private static volatile boolean disconnectDemanded;
	public static final String QUIT_WORD = "quit";

	public static void main(String[] args) {
		Properties properties = new Properties();
		try {
			properties.load(new FileReader("config.properties"));
		} catch (FileNotFoundException e1) {
			System.out.println("Can't find properties file!");
			return;
		} catch (IOException e1) {
			System.out.println("Something went wrong while reading properties");
			e1.printStackTrace();
			return;
		}
		int port = Integer.valueOf(properties.getProperty("port"));
		String IP = properties.getProperty("serverIP");
		try (Socket socket = new Socket(InetAddress.getByName(IP), port)) {
			/*
			 * tried to start both writer and reader from main thread but refused in favor
			 * of starting reader thread from writer see Chat writer class for details
			 */
			Thread writer = new Thread(new ChatWriter(socket));
			writer.start();
			while (!ChatClient.disconnectDemanded) {
			}
		} catch (UnknownHostException e) {
			System.out.println("Can't identify remote host!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Something went wrong with the socket!");
			e.printStackTrace();
		}
	}

	public static boolean isDisconnectDemanded() {
		return disconnectDemanded;
	}

	public static synchronized void setDisconnectDemanded(boolean disconnectDemanded) {
		ChatClient.disconnectDemanded = disconnectDemanded;
	}

}
