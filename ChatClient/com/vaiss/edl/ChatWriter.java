package com.vaiss.edl.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ChatWriter implements Runnable {
	private Socket socket;

	public ChatWriter(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		Thread reader = new Thread(new ChatReader(socket));
		reader.start();
//TODO delete net line
		System.out.println("ChatWriter started");
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		try (PrintStream out = new PrintStream(socket.getOutputStream())) {
			String message = "";
			while ((message = stdIn.readLine()) != null) {
				if (message.toLowerCase().equals(ChatClient.QUIT_WORD)) {
					/*
					 * trying to terminate writer thread before reader thread caused closing both
					 * socket's printStream and socket itself. Socket closing in it's turn caused
					 * exception in reader, because it was waiting for incoming message So decided
					 * to start reader thread from writer thread to be able to make writer wait
					 * (using join()) till reader thread is done
					 */
					out.println(message);
					reader.join();
					break;
				}
				out.println(message);
			}
//TODO delete net line
			System.out.println("ChatWriter finished");
		} catch (IOException e) {
			System.out.println("Failed to write to socket!");
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
