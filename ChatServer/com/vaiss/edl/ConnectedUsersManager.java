package com.vaiss.edl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectedUsersManager {
	private static volatile ConcurrentHashMap<String, Socket> usersConnected = new ConcurrentHashMap<>();
	private Socket socket;

	public ConnectedUsersManager(Socket socket) {
		this.socket = socket;
	}

	public void addUser(String nickName) {
		usersConnected.put(nickName, socket);
	}

	public void removeUser(String nickName) {
		usersConnected.remove(nickName);
	}

	public void processNewUser() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintStream out = new PrintStream(socket.getOutputStream())) {
			String nickName = "";
			while (nickName == "" || (usersConnected.get(nickName) != null)) {
				out.println("Please, enter your nickName:");
				nickName = in.readLine();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
