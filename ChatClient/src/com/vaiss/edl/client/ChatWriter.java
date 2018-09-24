package com.vaiss.edl.client;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.charset.Charset;

public class ChatWriter implements Runnable {
	private Socket socket;

	public ChatWriter(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		Thread reader = new Thread(new ChatReader(socket));
		reader.start();
		// TODO delete net line
		System.out.println("ChatWriter started");

		try (OutputStream out = socket.getOutputStream(); ByteArrayOutputStream bos = new ByteArrayOutputStream();) {

			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(ChatClient.getCryptor().getMyPublicKey());
			byte[] publicKeyBytes = bos.toByteArray();
			out.write(publicKeyBytes);
			out.write(ChatClient.getKeyTransmissionEndByteSequence());
			// TODO delete net line
			System.out.println("Key sent");

			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

			try {
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
						out.write(message.getBytes(Charset.forName("UTF-8")));//notify partner's reader that we intend to quit
						reader.join();
						break;
					}
					out.write(message.getBytes(Charset.forName("UTF-8")));
				}
				// TODO delete net line
				System.out.println("ChatWriter finished");
			} catch (IOException e) {
				System.out.println("Failed to write to socket!");
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			System.out.println("Failed to send key!");
			e.printStackTrace();
		}
	}

}
