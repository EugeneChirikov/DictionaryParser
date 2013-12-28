package com.mates120.dictionaryparser.stardict;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.util.Log;

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
//		Logger.l().PRINT("STARD_PARSER", "offset was = ");
//		debuggingPrint(offset);
		long offsetValue = makeFittingOffset(offset);
//		Logger.l().PRINT("STARD_PARSER", "offset is = " + offsetValue);
		
		randomAccessStream.seek(offsetValue);
		
//		Logger.l().PRINT("STARD_PARSER", "size was = ");
//		debuggingPrint(size);
		int sizeValue = makeFittingSize(size);
//		Logger.l().PRINT("STARD_PARSER", "size is = " + sizeValue);
		
		byte [] buffer = createBuffer(sizeValue);
		randomAccessStream.read(buffer);
	    String newValue = convertToString(buffer);
	    buffer = null;
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
	
	private int makeFittingSize(byte[] size) throws DictionaryParserException
	{
		int sizeValue = 0;
		if (size.length != 4)
			throw new DictionaryParserException("We support only 2 byte long size of article");
		for (int i = 0; i < 2; ++i)
		{
			if ((size[i] & 0xFF) != 0)
				throw new DictionaryParserException("We support only 2 byte long size of article");
			sizeValue <<= 8;
			sizeValue |= (size[i+2] & 0xFF);
		}
		return sizeValue;
	}
}
