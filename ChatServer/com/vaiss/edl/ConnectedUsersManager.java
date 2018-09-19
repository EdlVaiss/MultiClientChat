package com.vaiss.edl.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectedUsersManager {
	private static ConnectedUsersManager instance;
	private volatile ConcurrentHashMap<String, ChatUser> usersConnected = new ConcurrentHashMap<>();
	private final String QUIT_WORD = "quit";
	private final String WAIT_WORD = "wait";

	private ConnectedUsersManager() {

	}

	public static synchronized ConnectedUsersManager getInstance() {
		if (instance == null) {
			return new ConnectedUsersManager();
		}
		return instance;
	}

	public void addUser(ChatUser user) {
		usersConnected.put(user.getNickName(), user);
	}

	public ChatUser getUser(String nickName) {
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

	public void processNewUser(Socket socket) {

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintStream out = new PrintStream(socket.getOutputStream());

			addUser(new ChatUser("Bob", new Socket()));
			addUser(new ChatUser("Laila", new Socket()));

			String nickName = "";
			while (nickName.equals("") || nickName.toLowerCase().equals(QUIT_WORD)
					|| nickName.toLowerCase().equals(WAIT_WORD) || (getUser(nickName) != null)) {
				// TODO make warnings to user if inappropriate nickName is chosen
				out.println("Please, enter your nickName:\r\n"
						+ "(take into consideration that words \"quit\" and \"wait\" "
						+ "are reserved and not allowed to use as nickname)");
				nickName = in.readLine();
			}
			out.println("Thanks, your nickName is \"" + nickName + "\"");

			String partnerNickName = "";
			while (partnerNickName.equals("")) {
				out.println("Pleas, choose a user to talk to\r\n or type \"" + QUIT_WORD + "\" to leave this chat\r\n"
						+ "or type \"" + WAIT_WORD + "\" to wait untill your partner connects to you");
				ArrayList<String> nickList = Collections.list(usersConnected.keys());
				out.println(nickList);
				if (partnerNickName.toLowerCase().equals(WAIT_WORD)) {
					addUser(new ChatUser(nickName, socket));
					return;
				}
				if (partnerNickName.toLowerCase().equals(QUIT_WORD)) {
					out.println(QUIT_WORD);
					in.close();
					out.close();
					return;
				}
				if (getUser(partnerNickName) == null) {
					out.println("Sorry, this user has disconnected:(");
					partnerNickName = "";
				}
			}
			addUser(new ChatUser(nickName, socket));
			out.println("Connecting to \"" + partnerNickName + "\"");

			Thread serverThread = new Thread(new ServerThread(getUser(nickName), getUser(partnerNickName)));
			serverThread.start();

		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
