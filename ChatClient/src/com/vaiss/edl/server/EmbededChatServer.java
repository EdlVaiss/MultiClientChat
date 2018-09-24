package com.vaiss.edl.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.vaiss.edl.client.ChatWriter;

public class EmbededChatServer {

	public static void main(String[] args) {
		try (ServerSocket serverSocket = new ServerSocket(8020)) {
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
