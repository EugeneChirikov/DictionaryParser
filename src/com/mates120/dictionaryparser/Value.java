package com.mates120.dictionaryparser;

public class Value {
	private long id;
	private String value;
	private String dictionary;
	
	public Value(){
		
	}
	
	public Value(String value, String dictionary){
		this.value = value;
		this.dictionary = dictionary;
	}
	
	public void setId(long id){
		this.id = id;
	}
	
	public long getId(){
		return this.id;
	}
	
	public void setValue(String value){
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}
	
	public void setDictionary(String dictionary){
		this.dictionary = dictionary;
	}
	
	public String getDictionary(){
		return this.dictionary;
	}
	
	@Override
	public String toString(){
		return value;
	}
}
