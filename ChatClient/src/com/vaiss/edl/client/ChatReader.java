package com.vaiss.edl.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.charset.Charset;
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

		try (InputStream in = socket.getInputStream(); ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
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

					// TODO delete net line
					System.out.println("Bytes read: " + bytesRead);
					totalBytesRead += bytesRead;
					System.out.println("totalBytesRead: " + totalBytesRead);
					System.out.println("iterate");
					for (int i = 0; i < totalBytesRead; i++) {
						System.out.println(content[i]);
					}

					baos.write(content, totalBytesRead - bytesRead, bytesRead);

					if (Arrays.equals(
							Arrays.copyOfRange(content, totalBytesRead - endByteSequence.length, totalBytesRead),
							endByteSequence)) {
						break;
					}

				}
				// TODO delete net line
				System.out.println("Key ready");
				//ByteArrayInputStream bais = new ByteArrayInputStream(
						//Arrays.copyOfRange(baos.toByteArray(), baos.size() - endByteSequence.length, baos.size()));
				ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
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
						message = new String(baos.toByteArray(), Charset.forName("UTF-8"));

						System.out.println(message.toLowerCase().equals(ChatClient.QUIT_WORD));
						if (message.toLowerCase().equals(ChatClient.QUIT_WORD)) {
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
