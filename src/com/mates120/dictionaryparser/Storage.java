package com.mates120.dictionaryparser;

import com.mates120.dictionaryparser.Exceptions.DictionaryParserException;

public interface Storage {

	public void open();
	public void close();
	public void insertWord(String word, String value) throws DictionaryParserException;
}
