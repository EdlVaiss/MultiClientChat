package com.vaiss.edl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatReader implements Runnable {
	private Socket socket;

	public ChatReader(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		//TODO delete net line
		System.out.println("ChatReader started");
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			String message = "";
			while ((message = in.readLine()) != null) {
				if (message.toLowerCase().equals(ChatClient.QUIT_WORD)) {
					break;
				}
				System.out.println(message);
			}
			ChatClient.setDisconnectDemanded(true);
			//TODO delete net line
			System.out.println("ChatReader finished");
		} catch (IOException e) {
			System.out.println("Failed to read from socket!");
			e.printStackTrace();
		}
	}

}
