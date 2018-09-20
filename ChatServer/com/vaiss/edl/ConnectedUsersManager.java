package com.vaiss.edl.server;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectedUsersManager {
	private static ConnectedUsersManager instance;
	private volatile ConcurrentHashMap<String, ChatUser> usersConnected = new ConcurrentHashMap<>();

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

}
