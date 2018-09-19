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
				if (getUsersQuantity() == 0) {

					out.println("You are the only user in this chat\r\n" + 
					"Type \"" + QUIT_WORD + "\" if you don't want to wait incoming connection");
					switch (in.readLine().toLowerCase()) {
					case QUIT_WORD:
						partnerNickName = QUIT_WORD;
						break;
					default:
						partnerNickName = WAIT_WORD;
					}
				} else {
					out.println("Pleas, choose a user to talk to\r\n" + "or type \"" + QUIT_WORD
							+ "\" to leave this chat\r\n" + "or type \"" + WAIT_WORD
							+ "\" to wait untill your partner connects to you");
					ArrayList<String> nickList = Collections.list(usersConnected.keys());
					out.println(nickList);

					partnerNickName = in.readLine();
				}

				if (partnerNickName.toLowerCase().equals(WAIT_WORD)) {
					addUser(new ChatUser(nickName, socket));
					out.println("Please wait for incoming connections...");
					System.out.println("User \"" + nickName + "\" is in waiting mode");
					return;
				}
				if (partnerNickName.toLowerCase().equals(QUIT_WORD)) {
					// use the next sequence of commands:
					// 1. close inputstream,
					// 2. send QUIT_WORD to user,
					// 3.close outputstream
					// else you got reset connection error on server
					in.close();
					out.println(QUIT_WORD);
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
			System.out.println(nickName + " and " + partnerNickName + " started private chat");

		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
