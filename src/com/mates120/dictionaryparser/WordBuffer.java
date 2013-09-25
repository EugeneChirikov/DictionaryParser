package com.mates120.dictionaryparser;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import com.mates120.dictionaryparser.Exceptions.DictionaryParserException;

public class WordBuffer
{
	private final int MAX_WORD_LENGTH = 255;
	private byte [] wordString;
	int length;
	CharsetDecoder utf8decoder;
	
	public WordBuffer()
	{
		wordString = new byte [MAX_WORD_LENGTH];
		length = 0;
		Charset charset = Charset.forName("UTF-8");
		utf8decoder = charset.newDecoder();
	}
	
	public void addByte(byte newByte) throws DictionaryParserException
	{
		if (length >= MAX_WORD_LENGTH)
			throw new DictionaryParserException("Word is too long. Dictionary is invalid.");
		wordString[length] = newByte;
		length++;
	}
	
	public String getWord() throws CharacterCodingException
	{
		ByteBuffer wrappedBuffer = ByteBuffer.wrap(wordString, 0, length);
		String word = byteBufferToString(wrappedBuffer);
		wrappedBuffer = null;
		return word;
	}
	
	private String byteBufferToString(ByteBuffer byteBuffer) throws CharacterCodingException
	{
		return utf8decoder.decode(byteBuffer).toString();
	}
	
	public void clear()
	{
		length = 0;
	}
}