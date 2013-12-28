package com.mates120.dictionaryparser.stardict;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class IfoFile extends StarDictFile
{
	FileInputStream inputStream;
	
	public IfoFile(String afilePath) throws FileNotFoundException
	{
		super(afilePath);
		inputStream = new FileInputStream(this.afile);
	}
	
	public String readEverythingString() throws IOException
	{	
	    byte[] buffer = readEverythingByte();
	    String newst = convertToString(buffer);
	    buffer = null;
	    return newst;
	}
	
	public void close() throws IOException
	{
		inputStream.close();
	}
	
	private byte[] readEverythingByte() throws IOException
	{
		int available = 0;
		available = inputStream.available();
	    byte[] buffer = new byte[available];
	    inputStream.read(buffer);
	    return buffer;
	}
}
