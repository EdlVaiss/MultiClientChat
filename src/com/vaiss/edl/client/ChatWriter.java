package com.vaiss.edl.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.vaiss.edl.exceptions.EncriptionException;
import com.vaiss.edl.propertiesholder.PropertiesHolder;

public class ChatWriter implements Runnable {
	private static Logger log = Logger.getLogger(ChatWriter.class);
	private Socket socket;

	public ChatWriter(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {

		Properties properties = PropertiesHolder.getProperties();

		if (properties.isEmpty()) {
			System.out.println("Shutdown writer because of properties issue!");
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

			String cmdEncoding = properties.getProperty("cmdEncoding");

			if (cmdEncoding == null || cmdEncoding.equals("")) {
				System.out.println("Can't find encoding in config file! Trying to use UTF-8...");
				cmdEncoding = "UTF-8";
			}

			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in, cmdEncoding));

			String message = "";
			while ((message = stdIn.readLine()) != null) {

				if (socket.isClosed()) {
					break;
				}

				if (message.toLowerCase().equals(ChatClient.QUIT_WORD)) {
					/*
					 * trying to terminate writer thread before reader thread caused closing both
					 * socket's outputStream and socket itself. Socket closing in it's turn caused
					 * exception in reader, because it was waiting for incoming message So decided
					 * to start reader thread from writer thread to be able to make writer wait
					 * (using join()) till reader thread is done
					 */

					byte[] encryptedMessage = ChatClient.getCryptor().encrypt(message);
					out.write(encryptedMessage);// notify partner's reader that we intend to quit
					reader.join();
					break;
				}

				byte[] encryptedMessage = ChatClient.getCryptor().encrypt("\t" + nickName + ": " + message);
				out.write(encryptedMessage);
			}
		} catch (EncriptionException e) {
			log.error("Encription process failed!");
			System.out.println("Encription process failed!");
		} catch (IOException e) {
			log.error("Failed to write to socket!");
			System.out.println("Failed to write to socket!");
		} catch (InterruptedException e) {
			log.fatal("Multithreading issue");
			System.out.println("Something went wrong!");
		}
	}

}
