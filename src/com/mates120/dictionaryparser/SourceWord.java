package com.mates120.dictionaryparser;

public class SourceWord {
	private String source;
	private String value;
	
	public String getSource(){
		return source;
	}
	
	public String getValue(){
		return value;
	}
	
	public void setSource(String source)
	{
		this.source = source;
	}
	
	public void addValue(String value)
	{
		this.value = value;
	}
}
