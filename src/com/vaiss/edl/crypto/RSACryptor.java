package com.vaiss.edl.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

import org.apache.log4j.Logger;

import com.vaiss.edl.crypto.utils.ByteArraySplitter;
import com.vaiss.edl.crypto.utils.HungryByteArrayEater;
import com.vaiss.edl.exceptions.DecriptionException;
import com.vaiss.edl.exceptions.EncriptionException;

public class RSACryptor implements Cryptor {
	private static Logger log = Logger.getLogger(RSACryptor.class);
	private static RSACryptor instance;
	private PrivateKey myPrivateKey;
	private PublicKey myPublicKey;
	private PublicKey partnerPublicKey;
	private static final String ALGORITHM = "RSA";
	private static final int MAX_ENC_MESSAGE_LEN = 245;// in bytes, for encription
	private static final int MAX_DEC_MESSAGE_LEN = 256;// in bytes, for decription

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
	public byte[] encrypt(String message) throws EncriptionException {
		byte[] cipherMessage = null;
		byte[] initialMessageBytes = message.getBytes();
		try {
			final Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, partnerPublicKey);
			if (initialMessageBytes.length <= MAX_ENC_MESSAGE_LEN) {
				cipherMessage = cipher.doFinal(initialMessageBytes);
			} else {
				ByteArraySplitter splitter = new ByteArraySplitter();
				splitter.split(initialMessageBytes, MAX_ENC_MESSAGE_LEN);
				HungryByteArrayEater eater = new HungryByteArrayEater();
				while (splitter.hasNext()) {
					byte[] encodedPart = cipher.doFinal(splitter.next());
					eater.feed(encodedPart);
				}
				cipherMessage = eater.scare();
			}
		} catch (Exception e) {
			log.fatal("Encription process failed");
			throw new EncriptionException();
		}
		return cipherMessage;
	}

	@Override
	public String decript(byte[] message) throws DecriptionException {
		byte[] decryptedMessage = null;
		try {
			final Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, myPrivateKey);

			if (message.length <= MAX_DEC_MESSAGE_LEN) {
				decryptedMessage = cipher.doFinal(message);
			} else {
				ByteArraySplitter splitter = new ByteArraySplitter();
				splitter.split(message, MAX_DEC_MESSAGE_LEN);
				HungryByteArrayEater eater = new HungryByteArrayEater();
				while (splitter.hasNext()) {
					byte[] encodedPart = cipher.doFinal(splitter.next());
					eater.feed(encodedPart);
				}
				decryptedMessage = eater.scare();
			}

		} catch (Exception ex) {
			log.fatal("Decription process failed");
			throw new DecriptionException();
		}
		return new String(decryptedMessage);
	}
}
