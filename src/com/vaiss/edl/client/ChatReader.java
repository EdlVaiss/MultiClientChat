package com.vaiss.edl.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.vaiss.edl.exceptions.DecriptionException;

public class ChatReader implements Runnable {
	private static Logger log = Logger.getLogger(ChatReader.class);
	private Socket socket;

	public ChatReader(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {

		try (InputStream in = socket.getInputStream();) {

			if (ChatClient.getCryptor().getPartnerPublicKey() == null) {
				KeyReader keyReader = new KeyReader(in, ChatClient.getKeyTransmissionEndByteSequence());
				PublicKey partnerPublicKey = keyReader.read();
				ChatClient.getCryptor().setPartnerPublicKey(partnerPublicKey);
			}

			byte[] content = new byte[2048];
			int bytesRead = 0;
			String message = "";

			while ((bytesRead = in.read(content)) != -1) {
				message = ChatClient.getCryptor().decript(Arrays.copyOfRange(content, 0, bytesRead));
				if (message.toLowerCase().equals(ChatClient.QUIT_WORD)) {
					System.out.println("Your partner has just left the conversation. Press \"Enter\" to exit");
					break;
				}
				System.out.println(message);
			}

			ChatClient.setDisconnectDemanded(true);

		} catch (DecriptionException e) {
			System.out.println("Decription process failed");
			ChatClient.setDisconnectDemanded(true);
		} catch (ClassNotFoundException e) {
			log.error("Failed to manage partner's public key!");
			System.out.println("Failed to manage partner's public key!");
			ChatClient.setDisconnectDemanded(true);

		} catch (IOException e) {
			log.error("Failed to manage partner's public key!");
			System.out.println("Failed to read from socket!");
			ChatClient.setDisconnectDemanded(true);
			e.printStackTrace();
		}
	}
}
