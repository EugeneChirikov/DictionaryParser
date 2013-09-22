package com.mates120.dictionaryparser;

public class StarDictWord extends SourceWord
{
	private byte [] dataOffset;
	private byte [] dataSize;
	
	StarDictWord(int idxoffsetbits)
	{
		super();
		dataOffset = new byte [idxoffsetbits/8]; // 8 or 4 bytes
		dataSize = new byte [4]; //4 bytes
	}
	
	public void addDataOffset(byte [] dataOffset)
	{
		this.dataOffset = dataOffset;
	}
	
	public void addDataSize(byte [] dataSize)
	{
		this.dataSize = dataSize;
	}
	
	public byte[] getDataOffset()
	{
		return dataOffset;
	}

	public byte[] getDataSize()
	{
		return dataSize;
	}
}