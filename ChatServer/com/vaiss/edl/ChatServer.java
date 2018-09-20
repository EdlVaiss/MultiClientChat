package com.vaiss.edl.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
	public static volatile ConnectedUsersManager manager = ConnectedUsersManager.getInstance();

	public static void main(String[] args) {
		PrintStream out = null;
		try (ServerSocket ss = new ServerSocket(8020)) {
			
			while (true) {
				Socket socket = ss.accept();
				System.out.println(socket.getInetAddress().getHostName() + " connected");

				out = new PrintStream(socket.getOutputStream());
				
				if (manager.getUsersQuantity() == 0) {
					manager.addUser(new ChatUser("first", socket));
					out.println(
							"Hi! You're connected successfully to private chat server!\r\nPlease wait for your partner to connect...");
				} else if (manager.getUsersQuantity() == 1) {
					if (manager.isUserPresent("first")) {
						manager.addUser(new ChatUser("second", socket));
					} else {
						manager.addUser(new ChatUser("first", socket));
					}

					Thread serverThread = new Thread(
							new ServerThread(manager.getUser("first"), manager.getUser("second")));
					serverThread.start();
				} else {

					out.println("Sorry! It's a private chat! You're a third wheel!\r\nPlease quit!");
				}

			}
		} catch (IOException e) {
			System.out.println("Failed to establish Serversocket!");
			e.printStackTrace();
		} finally {
			out.close();
		}

	}

}
