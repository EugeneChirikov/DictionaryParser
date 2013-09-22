package com.mates120.dictionaryparser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.mates120.dictionaryparser.Exceptions.EndOfFileException;

public class StreamsReader
{
	private final int BUFFER_LENGTH = 57344; //56Kb
	private InputStream myStream;
	private byte[] myBuffer = null;
	private int bytesAvailable;
	private int currentByte;
	private BufferedInputStream myStreamBuffered;
	
	public StreamsReader(InputStream myStream){
		this.myStream = myStream;
	}
	
	public byte getByte() throws EndOfFileException, IOException
	{
		if (currentByte >= bytesAvailable)
			readChunk();
		int readByte = myBuffer[currentByte];
		currentByte++;
		return (byte)readByte;
	}
	
	public String readEverythingString() throws IOException
	{	
	    byte[] buffer = readEverythingByte();
	    String newst = convertToString(buffer);
	    buffer = null;
	    return newst;
	}
	
	public void initBufferedReading()
	{
		myBuffer = new byte[BUFFER_LENGTH];
		currentByte = 0;
		bytesAvailable = 0;
		myStreamBuffered = new BufferedInputStream(myStream);
	}

	private void readChunk() throws IOException, EndOfFileException
	{
		int offset = 0;
		bytesAvailable = myStreamBuffered.read(myBuffer, offset, BUFFER_LENGTH);
		currentByte = 0;
		if (bytesAvailable < 0)
			throw new EndOfFileException();
	}
	
	private byte[] readEverythingByte() throws IOException
	{
		int available = 0;
		available = myStream.available();
	    byte[] buffer = new byte[available];
	    myStream.read(buffer);
	    return buffer;
	}

	private String convertToString(byte[] buffer) throws UnsupportedEncodingException
	{
		return new String(buffer, "UTF-8");
	}
}