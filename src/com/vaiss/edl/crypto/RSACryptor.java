package com.vaiss.edl.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class RSACryptor implements Cryptor {
	private static RSACryptor instance;
	private PrivateKey myPrivateKey;
	private PublicKey myPublicKey;
	private PublicKey partnerPublicKey;
	private static final String ALGORITHM = "RSA";

	private RSACryptor() {
		KeyGen keyGen = new KeyGen();
		myPrivateKey = keyGen.getPrivateKey();
		myPublicKey = keyGen.getPublicKey();
	}

	public static synchronized RSACryptor getInstance() {
		if (instance == null) {
			return new RSACryptor();
		}
		return instance;
	}

	@Override
	public PublicKey getPartnerPublicKey() {
		return partnerPublicKey;
	}

	@Override
	public void setPartnerPublicKey(PublicKey partnerPublicKey) {
		this.partnerPublicKey = partnerPublicKey;
	}

	@Override
	public PrivateKey getMyPrivateKey() {
		return myPrivateKey;
	}

	@Override
	public PublicKey getMyPublicKey() {
		return myPublicKey;
	}

	@Override
	public byte[] encrypt(String message) {
		byte[] cipherMessage = null;
		try {
			final Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, partnerPublicKey);
			cipherMessage = cipher.doFinal(message.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cipherMessage;
	}

	@Override
	public String decript(byte[] message) {
		byte[] dectyptedMessage = null;
		try {
			final Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, myPrivateKey);
			dectyptedMessage = cipher.doFinal(message);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new String(dectyptedMessage);
	}
}
