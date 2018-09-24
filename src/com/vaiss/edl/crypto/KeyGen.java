package com.vaiss.edl.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class KeyGen {
	private KeyPair keyPair;

	public KeyGen() {
		try {
			// Get an instance of the RSA key generator
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			//Initialize the generator for a certain keysize 
			kpg.initialize(2048);
			// Generate the keys — might take sometime on slow computers
			keyPair = kpg.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public PrivateKey getPrivateKey() {
		return keyPair.getPrivate();
	}
	
	public PublicKey getPublicKey() {
		return keyPair.getPublic();
	}

}
