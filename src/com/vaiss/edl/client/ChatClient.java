package com.vaiss.edl.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

import com.vaiss.edl.crypto.Cryptor;
import com.vaiss.edl.crypto.RSACryptor;
import com.vaiss.edl.exceptions.PropertiesException;
import com.vaiss.edl.propertiesholder.PropertiesHolder;

public class ChatClient {
	private static volatile boolean disconnectDemanded;
	private static volatile Cryptor cryptor = RSACryptor.getInstance();
	private static final byte[] keyTransmissionEndByteSequence = new byte[] { -1, -2, -3, -4 };
	public static volatile boolean channelReady;
	public static final String QUIT_WORD = "quit";

	//public static void main(String[] args) {
	public void start() {
		// TODO delete next line
		System.out.println("Client started");
		Properties properties;
		try {
			properties = new PropertiesHolder().getProperties();
		} catch (PropertiesException e1) {
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
		// TODO delete next line
		System.out.println("Client finished");
	}

	public static boolean isDisconnectDemanded() {
		return disconnectDemanded;
	}

	public static synchronized void setDisconnectDemanded(boolean disconnectDemanded) {
		ChatClient.disconnectDemanded = disconnectDemanded;
	}

	public static Cryptor getCryptor() {
		return cryptor;
	}

	public static byte[] getKeyTransmissionEndByteSequence() {
		return keyTransmissionEndByteSequence;
	}

}
