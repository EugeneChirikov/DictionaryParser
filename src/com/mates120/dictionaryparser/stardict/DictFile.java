package com.mates120.dictionaryparser.stardict;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.mates120.dictionaryparser.Exceptions.DictionaryParserException;
import com.mates120.dictionaryparser.debug.Logger;

public class DictFile extends StarDictFile
{
	RandomAccessFile randomAccessStream;

	public DictFile(String afilePath) throws FileNotFoundException
	{
		super(afilePath);
		randomAccessStream = new RandomAccessFile(this.afile, "r");
	}

	public String readValue(byte[] offset, byte[] size) throws IOException, DictionaryParserException
	{
		String newValue = "";
//		Logger.l().PRINT("STARD_PARSER", "offset was = ");
//		debuggingPrint(offset);
		long offsetValue = makeFittingOffset(offset);
//		Logger.l().PRINT("STARD_PARSER", "offset is = " + offsetValue);
		
		randomAccessStream.seek(offsetValue);
		
//		Logger.l().PRINT("STARD_PARSER", "size was = ");
//		debuggingPrint(size);
		int[] sizeValue = makeFittingSize(size);
//		Logger.l().PRINT("STARD_PARSER", "size is = " + sizeValue);
		if (sizeValue.length == 1)
		{		
			byte [] buffer = createBuffer(sizeValue[0]);
			randomAccessStream.read(buffer);
			newValue = convertToString(buffer);
			buffer = null;
		}
		else
			throw new DictionaryParserException("Too long article");
	    return newValue;
	}
	
	private void debuggingPrint(byte[] buffer)
	{
		for (int i = 0; i < buffer.length; ++i)
			Logger.l().PRINT("STARD_PARSER", "" + buffer[i]);
	}
	
	public void close() throws IOException
	{
		randomAccessStream.close();
	}
	
	private long makeFittingOffset(byte[] offset) throws DictionaryParserException
	{
		long offsetValue = 0;
		if (offset.length != 4)
			throw new DictionaryParserException("We support only 4 byte long offsets");
		else
		{
			for (int i = 0; i < 4; ++i)
			{
				offsetValue <<= 8;
				offsetValue |= (offset[i] & 0xFF);
			}
		}
		return offsetValue;
	}
	
	private int[] makeFittingSize(byte[] size) throws DictionaryParserException
	{
		requireSize(size);
		long sizeValue = 0;		
		for (int i = 0; i < 4; ++i)
		{
			sizeValue <<= 8;
			sizeValue |= (size[i] & 0xFF);
		}
		int[] sizes;
		if (sizeValue <= Integer.MAX_VALUE) // size is just int
		{
			sizes = new int[1];
			sizes[0] = (int)sizeValue;
			return sizes;
		}
		int maxIntsCount = (int)(sizeValue / (long)Integer.MAX_VALUE);
		long base = maxIntsCount * Integer.MAX_VALUE;
		int rest = (int)(sizeValue - base);
		if (rest < 0)
			throw new DictionaryParserException("It can never happen");
		sizes = new int[2];
		sizes[0] = maxIntsCount;
		sizes[1] = rest;
		return sizes;
	}
	
	private void requireSize(byte[] size) throws DictionaryParserException
	{
		if (size.length != 4)
			throw new DictionaryParserException("Article length is more than 4 bytes long number");
	}
}
