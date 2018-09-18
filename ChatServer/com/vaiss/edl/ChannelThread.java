package com.vaiss.edl.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ChannelThread implements Runnable {
	private String nickName;
	private BufferedReader in1;
	private PrintStream out1;
	private PrintStream out2;
	private final String QUIT_WORD = "quit";

	public ChannelThread(String nickName, BufferedReader in1, PrintStream out1, PrintStream out2) {
		this.nickName = nickName;
		this.in1 = in1;
		this.out1 = out1;
		this.out2 = out2;
	}

	@Override
	public void run() {
		String message = "";
		try {
			while ((message = in1.readLine()) != null) {
				if (message.toLowerCase().equals(QUIT_WORD)) {
					out1.println("Bye bye! Connection closed to " + InetAddress.getLocalHost().getHostName());
					out1.println(QUIT_WORD);
					out2.println(nickName + " has just quit the conversation!");
					break;
				}
				out2.println(nickName + ": " + message);
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
