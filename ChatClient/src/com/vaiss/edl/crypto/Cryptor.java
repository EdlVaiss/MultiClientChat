package com.vaiss.edl.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface Cryptor {
public byte[] encrypt(String message);
public String decript(byte[] message);
public PublicKey getPartnerPublicKey();
public void setPartnerPublicKey(PublicKey partnerPublicKey);
public PrivateKey getMyPrivateKey();
public PublicKey getMyPublicKey();
}
