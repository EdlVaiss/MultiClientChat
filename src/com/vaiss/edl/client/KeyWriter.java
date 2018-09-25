package com.vaiss.edl.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.PublicKey;

public class KeyWriter {
	private OutputStream out;
	private byte[] endByteSequence;

	public KeyWriter(OutputStream out, byte[] endByteSequence) {
		this.out = out;
		this.endByteSequence = endByteSequence;
	}
	
	public void write(PublicKey publicKey) throws IOException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos);) {

			oos.writeObject(publicKey);
			byte[] publicKeyBytes = bos.toByteArray();
			out.write(publicKeyBytes);
			out.write(endByteSequence);
		}
	}
}
