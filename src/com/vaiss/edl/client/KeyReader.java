package com.vaiss.edl.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.PublicKey;
import java.util.Arrays;

public class KeyReader {
	private InputStream in;
	private byte[] endByteSequence;

	public KeyReader(InputStream in, byte[] endByteSequence) {
		this.in = in;
		this.endByteSequence = endByteSequence;
	}

	public PublicKey read() throws IOException, ClassNotFoundException {
		PublicKey partnerPublicKey = null;
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {

			byte[] content = new byte[2048];

			int bytesRead = 0;
			int totalBytesRead = 0;
			while ((bytesRead = in.read(content)) != -1) {
				
				totalBytesRead += bytesRead;

				baos.write(content, 0, bytesRead);

				if (Arrays.equals(// check last read bytes to be end byte sequence
						Arrays.copyOfRange(baos.toByteArray(), totalBytesRead - endByteSequence.length, totalBytesRead),
						endByteSequence)) {
					break;
				}

			}

			try (ByteArrayInputStream bais = new ByteArrayInputStream(
					Arrays.copyOfRange(baos.toByteArray(), 0, baos.size() - endByteSequence.length));//get everything except end byte sequence 
					ObjectInputStream ois = new ObjectInputStream(bais);) {
				partnerPublicKey = (PublicKey) ois.readObject();
			}
		}
		return partnerPublicKey;
	}
}
