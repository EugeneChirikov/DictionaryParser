package com.mates120.dictionaryparser;

import java.util.ArrayList;
import java.util.List;

public class Word {
	private long id;
	private String source;
	private int rating;
	private int searchCount;
	private List<Value> values;

	public Word(){	
	}

	public Word(String source, List<Value> values){
		this.source = source;
		this.values = new ArrayList<Value>();
		this.setValues(values);
	}

	public Word(String source){
		this.id = 0;
		this.source = source;
		this.rating = 0;
		this.searchCount = 0;
		this.values = new ArrayList<Value>();
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getSource(){
		return source;
	}
	
	public void setSource(String source){
		this.source = source;
	}
	
	public void setSearchCount(int searchCount){
		this.searchCount = searchCount;
	}
	
	public int getSearchCount(){
		return this.searchCount;
	}
	
	public int getRating(){
		return rating;
	}
	
	public void setRating(int rating){
		this.rating = rating;
	}
	
	public void setValues(List<Value> values){
		this.values = values;
	}
	
	public List<Value> getValues(){
		return this.values;
	}
	
	public Value getValue(int index){
		return this.values.get(index);
	}
	
	@Override
	public String toString(){
		return source;
	}
	
	public boolean equals(Word word){
		boolean isEqual = false;
		if((this.source.equals(word.getSource())) && 
				(this.rating == word.getRating()) &&
				(this.searchCount == word.getSearchCount())) {
			if((this.values == null)&&(word.getValues() == null)){
				isEqual = true;}
			else{
				if(this.values.size() == word.getValues().size())
					for(int i = 0; i < this.values.size(); i++)
						if(!this.values.get(i).getValue().equals(word.getValue(i).getValue())){
							isEqual = false;
							break;
						}else
							isEqual = true;
				else
					isEqual = false;
			}
		}
		return isEqual;
	}
}
