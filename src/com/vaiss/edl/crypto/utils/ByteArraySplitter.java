package com.vaiss.edl.crypto.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class ByteArraySplitter implements Iterator<byte[]> {
	private ArrayList<byte[]> list;
	private int counter;

	public ByteArraySplitter() {
		list = new ArrayList<byte[]>();
	}

	public void split(byte[] bytes, int rate) {
		if (bytes.length <= rate || rate <= 0) {
			list.add(bytes);
			return;
		}

		int numOfParts = bytes.length / rate;
		if (bytes.length % rate != 0) {
			numOfParts = numOfParts + 1;
		}

		for (int i = 0, offset = 0; i < numOfParts; i++, offset += rate) {
			if (bytes.length - offset < rate) {
				rate = bytes.length - offset;
			}
			byte[] arr = Arrays.copyOfRange(bytes, offset, offset + rate);
			list.add(arr);
		}
	}

	@Override
	public boolean hasNext() {
		return counter < list.size();
	}

	@Override
	public byte[] next() {
		byte[] part = list.get(counter);
		counter++;
		return part;
	}
}
