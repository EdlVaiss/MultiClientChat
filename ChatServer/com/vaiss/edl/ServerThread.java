package com.vaiss.edl.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ServerThread implements Runnable {
	private ChatUser user1;
	private ChatUser user2;

	public ServerThread(ChatUser user1, ChatUser user2) {
		this.user1 = user1;
		this.user2 = user2;
	}

	@Override
	public void run() {
		Socket socket1 = user1.getSocket();
		Socket socket2 = user2.getSocket();
		// TODO delete next line
		System.out.println("ServerThread started");
		try (BufferedReader in1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
				PrintStream out1 = new PrintStream(socket1.getOutputStream());
				BufferedReader in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
				PrintStream out2 = new PrintStream(socket2.getOutputStream());) {

			broadcast("Hello! You're welcome in our private chat! ", out1, out2);
			Thread channel1 = new Thread(new ChannelThread(user1.getNickName(), in1, out1, out2));
			Thread channel2 = new Thread(new ChannelThread(user2.getNickName(), in2, out2, out1));
			channel1.start();
			channel2.start();

			while (channel1.isAlive() || channel2.isAlive()) {

			}
			ChatServer.manager.removeUser(user1.getNickName());
			ChatServer.manager.removeUser(user2.getNickName());
			// TODO delete next line
			System.out.println("ServerThread finished");
		} catch (IOException e) {
			System.out.println("Failed to read or write on server!");
			e.printStackTrace();
		} finally {
			try {
				socket1.close();
				socket2.close();
			} catch (IOException e) {
				System.out.println("Failed to close socket!");
				e.printStackTrace();
			}
		}
	}

	private void broadcast(String message, PrintStream out1, PrintStream out2) {
		out1.println(message);
		out2.println(message);
	}
}
