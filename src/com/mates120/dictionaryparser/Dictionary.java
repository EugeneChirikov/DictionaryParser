package com.mates120.dictionaryparser;

import java.util.ArrayList;
import java.util.List;

public class Dictionary {
	private String name;

	private List<SourceWord> words;
	private boolean searchIn;
	
	public void initDictionary()
	{
		words = new ArrayList<SourceWord>();
		searchIn = true;
	}
	
	public Dictionary()
	{
		initDictionary();
	}
	
	public Dictionary(String name)
	{
		initDictionary();
		this.name = name;		
	}
	
	public Dictionary(String name, int searchIn){
		this.name = name;
		if (searchIn == 1)
			this.searchIn = true;
		else
			this.searchIn = false;
	}

	public void setName(String name){
		this.name = name;
	}

	public void addWord(String word_source, List<String> valuesInString)
	{
		this.words.add(new SourceWord(word_source, valuesInString));

	}
	
	public String getName(){
		return this.name;
	}
	
	public List<SourceWord> getWords(){


		return this.words;
	}
	
	public SourceWord getWord(int index){
		return words.get(index);
	}
	
	public boolean isSearchIn(){
		return this.searchIn;
	}
	
	public void setSearchIn(boolean searchIn){
		this.searchIn = searchIn;
	}
}
