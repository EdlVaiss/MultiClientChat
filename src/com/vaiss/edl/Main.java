package com.vaiss.edl;

import com.vaiss.edl.client.ChatClient;
import com.vaiss.edl.server.EmbededChatServer;

public class Main {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Choose start mode: server or client");
		} else {
			switch (args[0].toLowerCase()) {
			case "-s": {
				new EmbededChatServer().start();
				break;
			}
			case "-c": {
				new ChatClient().start();
				break;
			}
			default: {
				new ChatClient().start();
			}

			}
		}
	}
}
