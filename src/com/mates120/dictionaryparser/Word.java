package com.mates120.dictionaryparser;


public class Word {
	private long id;
	private String source;
	private String value;
	
	public long getId(){
		return id;
	}
	
	public void setId(long id){
		this.id = id;
	}
	
	public String getSource(){
		return source;
	}
	
	public void setSource(String source){
		this.source = source;
	}
	
	public String getValue(){
		return value;
	}
	
	public void setValue(String value){
		this.value = value;
	}
}