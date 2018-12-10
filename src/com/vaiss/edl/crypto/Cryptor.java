package com.vaiss.edl.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;

import com.vaiss.edl.exceptions.DecriptionException;
import com.vaiss.edl.exceptions.EncriptionException;

public interface Cryptor {
public byte[] encrypt(String message) throws EncriptionException;
public String decript(byte[] message) throws DecriptionException;
public PublicKey getPartnerPublicKey();
public void setPartnerPublicKey(PublicKey partnerPublicKey);
public PrivateKey getMyPrivateKey();
public PublicKey getMyPublicKey();
}
