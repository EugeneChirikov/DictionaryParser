package com.mates120.dictionaryparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.mates120.dictionaryparser.Exceptions.DictionaryParserException;
import com.mates120.dictionaryparser.Exceptions.EndOfFileException;

public class RawStarDictDictionary implements  RawDictionary
{
	private enum IFO_PARAMETERS
	{
		BOOKNAME("bookname"),       // required
		WORDCOUNT("wordcount"),     // required
		SYNWORDCOUNT("synwordcount"),  // required if ".syn" file exists.
		IDXFILESIZE("idxfilesize"),   // required
		IDXOFFSETBITS("idxoffsetbits"), // New in 3.0.0
		AUTHOR("author"),
		EMAIL("email"),
		WEBSITE("website"),
		DESCRIPTION("description"),    // You can use <br> for new line.
		DATE("date"),
		SAMETYPESEQUENCE("sametypesequence"), // very important.
		VERSION(""),
		UNKNOWN("");

		String paramName;
		IFO_PARAMETERS(String paramName)
		{
			this.paramName= paramName;
		}

		@Override
		public String toString()
		{
			return super.toString().toLowerCase(Locale.US);
		}
	}

	private String name;
	private InputStream ifoFile;
	private InputStream idxFile;
	private InputStream dictFile;
	private StreamsReader inputStream;

	private int wordcount = -1;
	private int synwordcount = -1;
	private int idxoffsetbits = -1;
	private int idxfilesize = -1;
	private String author = "";
	private String email = "";
	private String website = "";
	private String description = "";
	private String date = "";
	private String sametypesequence = "";
	private String version = "";
	private final byte WORD_ENDING = 0;
	
	WordBuffer wordString = new WordBuffer();

	public RawStarDictDictionary(InputStream ifoFile, InputStream idxFile, InputStream dictFile)
	{
		this.ifoFile = ifoFile;
		this.idxFile = idxFile;
		this.dictFile = dictFile;
	}

	public void parseAndCopyIntoDB(DataSource ds)
	{
		try
		{
			parseIFO();
			checkParsedIFO();
//			TimeUnit.SECONDS.sleep(10);
			setWordsOffsetSize();
			StarDictWord newWord = new StarDictWord(idxoffsetbits);			
			initStreamsReaderBuffered(idxFile);
			ds.open();
			while (true)
			{
				obtainWordFromIDX(newWord);
				Log.d("STARD_PARSER", newWord.getSource());
//				obtainValuesFromDICT(newWord);
				ds.insertWord(newWord.getSource(), "this-is-very-long-value,almost an article");
			}
//			checkParsedIDX();
		}
		catch (EndOfFileException e)
		{
			Log.d("STARD_PARSER", "End of file reached");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			Log.d("STARD_PARSER", "Closing database");
			ds.close();
			Log.d("STARD_PARSER", "Closing all streams");
			try {
				ifoFile.close();
				idxFile.close();
				dictFile.close();
			} catch (IOException e) {
				Log.d("STARD_PARSER", "Failed to close streams");
			}
						
//			checkParsedIDX();
		}

	}

	private void checkParsedIFO()
	{
		System.out.println("bookname = " + name);
		System.out.println("wordcount = " + Integer.toString(wordcount));
		System.out.println("synwordcount = " + Integer.toString(synwordcount));
		System.out.println("idxoffsetbits = " + Integer.toString(idxoffsetbits));
		System.out.println("idxfilesize = " + Integer.toString(idxfilesize));
		System.out.println("author = " + author);
		System.out.println("email = " + email);
		System.out.println("website = " + website);
		System.out.println("description = " + description);
		System.out.println("date = " + date);
		System.out.println("sametypesequence = " + sametypesequence);
	}
	
	private void parseIFO() throws IOException, DictionaryParserException
	{
		initStreamsReader(ifoFile);
		String ifoData = readEverythingString();

		Pattern regexpPattern = Pattern.compile("([a-z]+)=([0-9.]+|(?<=bookname=|description=|sametypesequence=).+)");
		Matcher regexpMatcher = regexpPattern.matcher(ifoData);
		while (regexpMatcher.find())
		{
			String parameterName = regexpMatcher.group(1);
			String parameterValue = regexpMatcher.group(2);
//			System.out.println("pname = " + parameterName);
//			System.out.println("pvalue = " + parameterValue);
			IFO_PARAMETERS parameter = identifyIFOParam(parameterName);
			switch (parameter)
			{
				case BOOKNAME:
					name = parameterValue;
					break;
				case WORDCOUNT:
					wordcount = Integer.parseInt(parameterValue);
					break;
				case VERSION:
					version = parameterValue;
					if (!version.equals("2.4.2") && !version.equals("3.0.0"))
						throw new DictionaryParserException("Incompatible with this StarDict version: " + parameterValue);
					break;
				case SYNWORDCOUNT:
					synwordcount = Integer.parseInt(parameterValue);
					break;
				case IDXOFFSETBITS:
					idxoffsetbits = Integer.parseInt(parameterValue);
					break;
				case IDXFILESIZE:
					idxfilesize = Integer.parseInt(parameterValue);
					break;
				case AUTHOR:
					author = parameterValue;
					break;
				case EMAIL:
					email = parameterValue;
					break;
				case WEBSITE:
					website = parameterValue;
					break;
				case DESCRIPTION:
					description = parameterValue;
					break;
				case DATE:
					date = parameterValue;
					break;
				case SAMETYPESEQUENCE:
					sametypesequence = parameterValue;
				default:
					break;
			}
		}		
	}

	private void initStreamsReader(InputStream myStream)
	{
		inputStream = new StreamsReader(myStream);
	}
	
	private void initStreamsReaderBuffered(InputStream myStream)
	{
		initStreamsReader(myStream);
		inputStream.initBufferedReading();
	}
	
	private String readEverythingString() throws IOException
	{
		return inputStream.readEverythingString();
	}
	
	private IFO_PARAMETERS identifyIFOParam(String paramName)
	{
		for (IFO_PARAMETERS p : IFO_PARAMETERS.values())
		{			
			if (paramName.equals( p.toString()))
			{
				return p;
			}
		}
		return IFO_PARAMETERS.UNKNOWN;
	}

	private void obtainWordFromIDX(StarDictWord newWord) throws IOException, DictionaryParserException
	{		

		byte newByte;
		wordString.clear();
		newByte = readByte();
		while (newByte != WORD_ENDING)
		{
			wordString.addByte(newByte);
			newByte = readByte();
		}
		newWord.setSource(wordString.getWord());
		readBytesIntoBuffer(newWord.getDataOffset());
		readBytesIntoBuffer(newWord.getDataSize());	
	}
	
	private byte readByte() throws EndOfFileException, IOException
	{
		return inputStream.getByte();
	}

	private void readBytesIntoBuffer(byte [] buffer) throws EndOfFileException, IOException
	{
		byte newByte;
		for (int i = 0; i < buffer.length; ++i)
		{
			newByte = readByte();
			buffer[i] = newByte;
		}
	}
	
	private void setWordsOffsetSize()
	{
		if (idxoffsetbits > 0)
			return;
		if (version.equals("3.0.0"))
			idxoffsetbits = 64;
		else
			idxoffsetbits = 32;
	}
	
	private void checkParsedIDX()
	{
		
	}
	
	private void parseDICT() throws DictionaryParserException, IOException{
		dictFile.close();
	}
}
