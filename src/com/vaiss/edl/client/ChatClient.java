package com.vaiss.edl.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.vaiss.edl.crypto.Cryptor;
import com.vaiss.edl.crypto.RSACryptor;
import com.vaiss.edl.propertiesholder.PropertiesHolder;

public class ChatClient {
	private static Logger log = Logger.getLogger(ChatClient.class);
	private static Cryptor cryptor = RSACryptor.getInstance();
	private static final byte[] keyTransmissionEndByteSequence = new byte[] { -1, -2, -3, -4 };
	public static final String QUIT_WORD = "quit";

	public void start() {
		System.out.println("CremlinChatClient started.");
		
		Properties properties = PropertiesHolder.getProperties();

		if (properties.isEmpty()) {
			log.error("Can't find or read properties file");
			System.out.println("Shutdown client because of properties issue!");
			return;
		}

		int port = Integer.valueOf(properties.getProperty("port"));
		String IP = properties.getProperty("serverIP");
		
		System.out.println("Trying to connect to CremlinChatServer...");
		
		try (Socket socket = new Socket(InetAddress.getByName(IP), port)) {
			/*
			 * tried to start both writer and reader from main thread but refused in favor
			 * of starting reader thread from writer. see Chat writer class for details
			 */
			Thread writer = new Thread(new ChatWriter(socket));
			writer.start();
			writer.join();

		} catch (UnknownHostException e) {
			log.error("Can't identify remote host!");
			System.out.println("Can't identify remote host!");
		} catch (IOException e) {
			log.error("Something went wrong with the socket!");
			System.out.println("Something went wrong with the socket!");
		} catch (InterruptedException e) {
			log.error("Multithreading issue");
			System.out.println("Something went really wrong!");
		}
	}

	public static Cryptor getCryptor() {
		return cryptor;
	}

	public static byte[] getKeyTransmissionEndByteSequence() {
		return keyTransmissionEndByteSequence;
	}
}
