package com.mates120.dictionaryparser.stardict;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.mates120.dictionaryparser.Exceptions.EndOfFileException;

public class IdxFile extends StarDictFile
{
	private final int BUFFER_LENGTH = 57344; //56Kb
	private BufferedInputStream bufferedInputStream;
	private byte[] myBuffer = null;
	private int bytesAvailable;
	private int currentByte;
	
	public IdxFile(String afilePath) throws FileNotFoundException
	{
		super(afilePath);
		currentByte = 0;
		bytesAvailable = 0;
		myBuffer = createBuffer(BUFFER_LENGTH);
		FileInputStream is = new FileInputStream(this.afile);
		bufferedInputStream = new BufferedInputStream(is);
	}
	
	public byte getByte() throws EndOfFileException, IOException
	{
		if (currentByte >= bytesAvailable)
			readChunk();
		int readByte = myBuffer[currentByte];
		currentByte++;
		return (byte)readByte;
	}
	
	public void close() throws IOException
	{
		bufferedInputStream.close();
	}
	
	private void readChunk() throws IOException, EndOfFileException
	{
		int offset = 0;
		bytesAvailable = bufferedInputStream.read(myBuffer, offset, BUFFER_LENGTH);
		currentByte = 0;
		if (bytesAvailable < 0)
			throw new EndOfFileException();
	}
}
