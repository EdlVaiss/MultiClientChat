package com.vaiss.edl.server;

import java.net.Socket;

public class ChatUser {
	private String nickName;
	private Socket socket;

	public ChatUser(String nickName, Socket socket) {
		this.nickName = nickName;
		this.socket = socket;
	}

	public String getNickName() {
		return nickName;
	}

	public Socket getSocket() {
		return socket;
	}

}
