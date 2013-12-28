package com.mates120.dictionaryparser.debug;

public class Logger 
{
	private  String tag = "STARD_PARSER";
	private static  Printing printer;
	private static Logger lInstance = null;
	
	private Logger(){}
	
	synchronized static public Logger l()
	{
		if (lInstance == null)
		{	
			lInstance = new Logger();
		}
		return lInstance;
	}
	
	synchronized public void setPrinter(Printing p)
	{
		printer = p;
	}
	
	synchronized public void setTag(String atag)
	{
		tag = atag;
	}

	public void PRINT(String msg)
	{
		printer.print(tag, msg);
	}
	
	public void PRINT(String atag, String msg)
	{
		printer.print(atag, msg);
	}
}
