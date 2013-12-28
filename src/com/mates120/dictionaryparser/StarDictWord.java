package com.mates120.dictionaryparser;

import com.mates120.dictionaryparser.Exceptions.DictionaryParserException;

public class StarDictWord extends SourceWord
{
	private byte [] dataOffset;
	private byte [] dataSize;
	
	public StarDictWord(int idxoffsetbits) throws DictionaryParserException
	{
		if (idxoffsetbits != 64 && idxoffsetbits != 32)
			throw new DictionaryParserException("idxoffsetbits can be 8 or 4, you have " + idxoffsetbits);
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
	
	private boolean hasLongSize()
	{
		int first_byte = dataSize[0] & 0xFF;
		int second_byte = dataSize[1] & 0xFF;
		return (first_byte + second_byte) != 0;
	}
}