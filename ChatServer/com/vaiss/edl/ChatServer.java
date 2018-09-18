package com.vaiss.edl.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
	private static ConnectedUsersManager manager = ConnectedUsersManager.getInstance();

	public static void main(String[] args) {
		try (ServerSocket ss = new ServerSocket(8020)) {
			while (true) {
				Socket socket = ss.accept();
				System.out.println(socket.getInetAddress().getHostName() + " connected");
				// PrintStream out = new PrintStream(socket.getOutputStream());
				// out.println("Hi! You are successfully connected to ChatServer");
				if (manager.getUsersQuantity() == 0) {
					manager.addUser("first", socket);
				} else if (manager.getUsersQuantity() == 1) {
					if (manager.isUserPresent("first")) {
						manager.addUser("second", socket);
					} else {
						manager.addUser("first", socket);
					}

					Thread serverThread = new Thread(
							new ServerThread(manager.getUserSocket("first"), manager.getUserSocket("second")));
					serverThread.start();
				} else {
					try (PrintStream out = new PrintStream(socket.getOutputStream())) {
						out.println("Sorry! It's a private chat! You're a third wheel!");
					}
				}

			}
		} catch (IOException e) {
			System.out.println("Failed to establish Serversocket!");
			e.printStackTrace();
		}

	}

}

class ServerThread implements Runnable {
	private Socket socket1;
	private Socket socket2;
	private final String QUIT_WORD = "quit";

	public ServerThread(Socket socket1, Socket socket2) {
		this.socket1 = socket1;
		this.socket2 = socket2;
	}

	@Override
	public void run() {
		System.out.println("ServerThread started");
		try (BufferedReader in1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
				PrintStream out1 = new PrintStream(socket1.getOutputStream());
				BufferedReader in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
				PrintStream out2 = new PrintStream(socket2.getOutputStream());) {

			broadcast("Hello! You're welcome in our private chat! ", out1, out2);
			Thread channel1 = new Thread(new ChannelThread(in1, out1, out2));
			Thread channel2 = new Thread(new ChannelThread(in2, out2, out1));
			channel1.start();
			channel2.start();
			
			while (channel1.isAlive() || channel2.isAlive()) {

			}

			// out1.println("Bye bye! Connection closed to " +
			// InetAddress.getLocalHost().getHostName());
			// System.out.println(socket1.getInetAddress().getHostName() + " disconnected");
			System.out.println("ServerThread finished");
		} catch (IOException e) {
			System.out.println("Failed to read or write on server!");
			e.printStackTrace();
		} finally {
			try {
				socket1.close();
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

class ChannelThread implements Runnable {
	private BufferedReader in1;
	private PrintStream out1;
	private PrintStream out2;
	private final String QUIT_WORD = "quit";

	public ChannelThread(BufferedReader in1, PrintStream out1, PrintStream out2) {
		this.in1 = in1;
		this.out1 = out1;
		this.out2 = out2;
	}

	@Override
	public void run() {
		String message = "";
		try {
			while ((message = in1.readLine()) != null) {
				if (message.toLowerCase().equals(QUIT_WORD)) {
					out1.println("Bye bye! Connection closed to " + InetAddress.getLocalHost().getHostName());
					out1.println(QUIT_WORD);
					break;
				}
				out2.println(message);
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
