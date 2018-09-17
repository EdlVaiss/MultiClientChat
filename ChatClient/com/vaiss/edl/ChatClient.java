package com.vaiss.edl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {
	private static volatile boolean disconnectDemanded;
	public static final String QUIT_WORD = "quit";

	public static void main(String[] args) {
		//TODO delete net line
		System.out.println("MainClient started");
		try (Socket socket = new Socket(InetAddress.getLocalHost(), 8020)) {
			/*
			 * tried to start both writer and reader from main thread but refused in favor
			 * of starting reader thread from writer see Chat writer class for details
			 */
			Thread writer = new Thread(new ChatWriter(socket));
			writer.start();
			while (!ChatClient.disconnectDemanded) {
			}
			//TODO delete net line
			System.out.println("MainClient finished");
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
