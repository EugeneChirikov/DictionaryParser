package com.mates120.dictionaryparser.stardict;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;


public abstract class StarDictFile
{	
	protected File afile;
	private final Charset UTF8_CHARSET;

	public StarDictFile(String filePath)
	{
		this.afile = new File(filePath);
		UTF8_CHARSET = Charset.forName("UTF-16");
	}

	protected final byte[] createBuffer(int length)
	{
		return new byte[length];
	}
	
	protected String convertToString(byte[] buffer) throws UnsupportedEncodingException
	{
		return new String(buffer, "UTF-8");
	}
	
	protected String convertToStringExpensive(byte[] buffer) throws CharacterCodingException
	{
		CharsetDecoder decoder = UTF8_CHARSET.newDecoder();
		ByteBuffer wrappedBuffer = ByteBuffer.wrap(buffer, 0, buffer.length);
		return decoder.decode(wrappedBuffer).toString();
	}
}
