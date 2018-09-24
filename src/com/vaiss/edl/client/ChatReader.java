package com.vaiss.edl.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Arrays;

public class ChatReader implements Runnable {
	private Socket socket;

	public ChatReader(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		// TODO delete net line
		System.out.println("ChatReader started");

		try (InputStream in = socket.getInputStream(); 
				ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
			if (ChatClient.getCryptor().getPartnerPublicKey() == null) {

				byte[] content = new byte[2048];
				byte[] endByteSequence = ChatClient.getKeyTransmissionEndByteSequence();
				int bytesRead = 0;
				int totalBytesRead = 0;
				while ((bytesRead = in.read(content, totalBytesRead, content.length - totalBytesRead)) != -1) {
					// above used read(content, totalBytesRead, content.length - totalBytesRead)
					// method guarantees
					// that service byte sent from partners writer and marking the end of partner
					// public key transmission
					// will be the last affected byte in read buffer

					totalBytesRead += bytesRead;

					baos.write(content, totalBytesRead - bytesRead, bytesRead);

					if (Arrays.equals(//check last read bytes to be end byte sequence
							Arrays.copyOfRange(content, totalBytesRead - endByteSequence.length, totalBytesRead),
							endByteSequence)) {
						break;
					}

				}
				ByteArrayInputStream bais = new ByteArrayInputStream(
						Arrays.copyOfRange(baos.toByteArray(), 0, baos.size() - endByteSequence.length));
				ObjectInputStream ois = new ObjectInputStream(bais);
				PublicKey partnerPublicKey = (PublicKey) ois.readObject();
				ChatClient.getCryptor().setPartnerPublicKey(partnerPublicKey);

				try {
					content = new byte[2048];
					bytesRead = 0;
					String message = "";
					while ((bytesRead = in.read(content)) != -1) {
						baos.reset();
						baos.write(content, 0, bytesRead);
						message = ChatClient.getCryptor().decript(baos.toByteArray());
						if (message.toLowerCase().equals(ChatClient.QUIT_WORD)) {
							System.out.println("Your partner has just leaved the conversation. Type \"quit\" to exit");
							break;
						}
						System.out.println(message);
					}
					ChatClient.setDisconnectDemanded(true);
					// TODO delete net line
					System.out.println("ChatReader finished");
				} catch (IOException e) {
					System.out.println("Failed to read from socket!");
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
