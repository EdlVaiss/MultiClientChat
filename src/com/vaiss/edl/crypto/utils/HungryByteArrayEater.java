package com.vaiss.edl.crypto.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

public class HungryByteArrayEater {
	private static Logger log = Logger.getLogger(HungryByteArrayEater.class);
	private ByteArrayOutputStream bos;

	public HungryByteArrayEater() {
		bos = new ByteArrayOutputStream();
	}

	public void feed(byte[] piece) throws IOException {
		try {
			bos.write(piece);
		} catch (IOException e) {
			log.error("HungryByteArrayEater choked on a piece of bytearray!");
			bos.close();
		}
	}
	
	public byte[] scare() throws IOException {
		try {
			return bos.toByteArray();
		}finally {
			bos.close();
		}
	}

}
