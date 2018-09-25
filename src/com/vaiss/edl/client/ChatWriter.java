package com.vaiss.edl.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

import com.vaiss.edl.propertiesholder.PropertiesHolder;

public class ChatWriter implements Runnable {
	private Socket socket;

	public ChatWriter(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {

		Properties properties = PropertiesHolder.getProperties();

		if (properties.isEmpty()) {
			System.out.println("Shutdown writer because of properties issue!");
			ChatClient.setDisconnectDemanded(true);
			return;
		}

		String nickName = properties.getProperty("nick");

		Thread reader = new Thread(new ChatReader(socket));
		reader.start();

		try (OutputStream out = socket.getOutputStream();) {

			KeyWriter keyWriter = new KeyWriter(out, ChatClient.getKeyTransmissionEndByteSequence());
			keyWriter.write(ChatClient.getCryptor().getMyPublicKey());

			System.out.println("Waiting for secure connection...");
			while (ChatClient.getCryptor().getPartnerPublicKey() == null) {

			}
			out.write(ChatClient.getCryptor().encrypt("Secure connection established! You can now chat securely..."));

			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

			String message = "";
			while ((message = stdIn.readLine()) != null) {

				if (message.toLowerCase().equals(ChatClient.QUIT_WORD)) {
					/*
					 * trying to terminate writer thread before reader thread caused closing both
					 * socket's outputStream and socket itself. Socket closing in it's turn caused
					 * exception in reader, because it was waiting for incoming message So decided
					 * to start reader thread from writer thread to be able to make writer wait
					 * (using join()) till reader thread is done
					 */
					if (socket.isClosed()) {
						break;
					}
					byte[] encryptedMessage = ChatClient.getCryptor().encrypt(message);
					out.write(encryptedMessage);// notify partner's reader that we intend to quit
					reader.join();
					break;
				}

				byte[] encryptedMessage = ChatClient.getCryptor().encrypt("\t" + nickName + ": " + message);
				out.write(encryptedMessage);
			}
		} catch (IOException e) {
			System.out.println("Failed to write to socket!");
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
