package com.mates120.dictionaryparser.stardict;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mates120.dictionaryparser.RawDictionary;
import com.mates120.dictionaryparser.StarDictWord;
import com.mates120.dictionaryparser.Storage;
import com.mates120.dictionaryparser.WordBuffer;
import com.mates120.dictionaryparser.Exceptions.DictionaryParserException;
import com.mates120.dictionaryparser.Exceptions.EndOfFileException;
import com.mates120.dictionaryparser.debug.Logger;

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
			return paramName;//super.toString().toLowerCase(Locale.US);
		}
	}

	private String name;
	private IfoFile ifoFile;
	private IdxFile idxFile;
	private DictFile dictFile;

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

	public RawStarDictDictionary(IfoFile ifoFile, IdxFile idxFile, DictFile dictFile)
	{
		this.ifoFile = ifoFile;
		this.idxFile = idxFile;
		this.dictFile = dictFile;
	}

	public void parseAndCopyIntoDB(Storage storage)
	{
		try
		{
			parseIFO();
//			checkParsedIFO();
			setWordsOffsetSize();
			StarDictWord newWord = new StarDictWord(idxoffsetbits);
			storage.open();
			while (true)
			{
				obtainWordFromIDX(newWord);
				Logger.l().PRINT("STARD_PARSER", newWord.getSource());
				newWord.setSource(escapeQuotes(newWord.getSource()));
				try
				{
					obtainValueFromDICT(newWord);
				}
				catch (Exception e)
				{
					Logger.l().PRINT("STARD_PARSER",e.getMessage());
					Logger.l().PRINT("STARD_PARSER","The word was skipped");					
					continue;
				}
				storage.insertWord(newWord.getSource(), newWord.getValue());
			}
//			checkParsedIDX();
		}
		catch (EndOfFileException e)
		{
			Logger.l().PRINT("STARD_PARSER", "End of file reached");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			Logger.l().PRINT("STARD_PARSER", "Closing database");
			storage.close();
			Logger.l().PRINT("STARD_PARSER", "Closing all streams");
			try {
				ifoFile.close();
				idxFile.close();
				dictFile.close();
			} catch (IOException e) {
				Logger.l().PRINT("STARD_PARSER", "Failed to close streams");
			}

//			checkParsedIDX();
		}
	}
	
	private String escapeQuotes(String str)
	{
		StringBuilder sb = new StringBuilder(str);
		char ch;
		for (int i = 0; i < sb.length(); ++i)
		{
			ch = sb.charAt(i);
			if (ch == '\'')
			{
				sb.insert(i, '\'');
				++i;
			}
		}
		return sb.toString();
	}

	private void checkParsedIFO()
	{
		Logger.l().PRINT("bookname = " + name);
		Logger.l().PRINT("wordcount = " + Integer.toString(wordcount));
		Logger.l().PRINT("synwordcount = " + Integer.toString(synwordcount));
		Logger.l().PRINT("idxoffsetbits = " + Integer.toString(idxoffsetbits));
		Logger.l().PRINT("idxfilesize = " + Integer.toString(idxfilesize));
		Logger.l().PRINT("author = " + author);
		Logger.l().PRINT("email = " + email);
		Logger.l().PRINT("website = " + website);
		Logger.l().PRINT("description = " + description);
		Logger.l().PRINT("date = " + date);
		Logger.l().PRINT("sametypesequence = " + sametypesequence);
	}
	
	private void parseIFO() throws IOException, DictionaryParserException
	{
		String ifoData = ifoFile.readEverythingString();

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
		newByte = idxFile.getByte();
		while (newByte != WORD_ENDING)
		{
			wordString.addByte(newByte);
			newByte = idxFile.getByte();
		}
		newWord.setSource(wordString.getWord());
		readBytesIntoBuffer(newWord.getDataOffset());
		readBytesIntoBuffer(newWord.getDataSize());
	}
	
	private void obtainValueFromDICT(StarDictWord newWord) throws DictionaryParserException, IOException
	{
		if (sametypesequence.isEmpty())
			throw new DictionaryParserException("sametypesequence is empty");
		if (sametypesequence.equals("x"))
		{
//			Logger.l().PRINT("STARD_PARSER", "type is X");
			String avalue = dictFile.readValue(newWord.getDataOffset(), newWord.getDataSize());
			newWord.addValue(avalue);
		}
	}
	
	private void convertXdxf2Html()
	{
		
	}
	
	private void readBytesIntoBuffer(byte [] buffer) throws EndOfFileException, IOException
	{
		byte newByte;
		for (int i = 0; i < buffer.length; ++i)
		{
			newByte = idxFile.getByte();
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
	
}
