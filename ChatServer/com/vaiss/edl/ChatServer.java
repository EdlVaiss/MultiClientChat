package com.vaiss.edl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {

	public static void main(String[] args) {
		try (ServerSocket ss = new ServerSocket(8020)) {
			while (true) {
				Socket socket = ss.accept();
				System.out.println(socket.getInetAddress().getHostName() + " connected");
				Thread serverThread = new Thread(new ServerThread(socket));
				serverThread.start();
			}
		} catch (IOException e) {
			System.out.println("Failed to establish Serversocket!");
			e.printStackTrace();
		}

	}

}

class ServerThread implements Runnable {
	private Socket socket;
	private final String QUIT_WORD = "quit";

	public ServerThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintStream out = new PrintStream(socket.getOutputStream())) {

			out.println("Hello! You're connected to " + InetAddress.getLocalHost().getHostName());

			String message = "";
			while ((message = in.readLine()) != null) {
				if (message.toLowerCase().equals(QUIT_WORD)) {
					out.println("Bye bye! Connection closed to " + InetAddress.getLocalHost().getHostName());
					out.println(QUIT_WORD);
					break;
				}
				out.println("server echo: " + message);
			}
			out.println("Bye bye! Connection closed to " + InetAddress.getLocalHost().getHostName());
			System.out.println(socket.getInetAddress().getHostName() + " disconnected");
		} catch (IOException e) {
			System.out.println("Failed to read or write on server!");
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Failed to close socket!");
				e.printStackTrace();
			}
		}
	}

}
