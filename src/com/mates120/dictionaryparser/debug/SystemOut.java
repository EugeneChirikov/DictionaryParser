package com.mates120.dictionaryparser.debug;

public class SystemOut implements Printing
{
	@Override
	public void print(String tag, String msg) {
		System.out.println(tag + ":\t" + msg);
	}
}
