package com.vaiss.edl.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectedUsersManager {
	private static ConnectedUsersManager instance;
	private volatile ConcurrentHashMap<String, Socket> usersConnected = new ConcurrentHashMap<>();

	private ConnectedUsersManager() {

	}

	public static synchronized ConnectedUsersManager getInstance() {
		if (instance == null) {
			return new ConnectedUsersManager();
		}
		return instance;
	}

	public void addUser(String nickName, Socket socket) {
		usersConnected.put(nickName, socket);
	}

	public Socket getUserSocket(String nickName) {
		return usersConnected.get(nickName);
	}
	
	public boolean isUserPresent(String nickName) {
		return usersConnected.containsKey(nickName);
	}

	public void removeUser(String nickName) {
		usersConnected.remove(nickName);
	}

	public int getUsersQuantity() {
		return usersConnected.size();
	}

	/*
	 * public void processNewUser() { try (BufferedReader in = new
	 * BufferedReader(new InputStreamReader(socket.getInputStream())); PrintStream
	 * out = new PrintStream(socket.getOutputStream())) { String nickName = "";
	 * while (nickName == "" || (usersConnected.get(nickName) != null)) {
	 * out.println("Please, enter your nickName:"); nickName = in.readLine(); }
	 * 
	 * } catch (IOException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 */

}
