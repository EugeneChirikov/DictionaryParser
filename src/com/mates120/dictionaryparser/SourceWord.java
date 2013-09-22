package com.mates120.dictionaryparser;

import java.util.ArrayList;
import java.util.List;

public class SourceWord {
	private String source;
	private List<String> values;
	
	SourceWord()
	{		
		this.values = new ArrayList<String>();
	}
	
	SourceWord(String source, List<String> values){
		this.source = source;
		this.values = values;
	}
	
	public String getSource(){
		return source;
	}
	
	public List<String> getValues(){
		return values;
	}
	
	public void setSource(String source)
	{
		this.source = source;
	}
	
	public void clearValues()
	{
		this.values.clear();
	}
	
	public void addValue(String value)
	{
		this.values.add(value);
	}
}
