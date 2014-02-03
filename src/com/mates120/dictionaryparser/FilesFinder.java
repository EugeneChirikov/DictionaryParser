package com.mates120.dictionaryparser;

import java.io.File;
import java.io.FilenameFilter;

import com.mates120.dictionaryparser.Exceptions.DictionaryParserException;
import com.mates120.dictionaryparser.Exceptions.ExpectedFilesNotFoundExeption;
import com.mates120.dictionaryparser.Exceptions.NotADirectoryException;
import com.mates120.dictionaryparser.Exceptions.ToManyAlikeFilesException;
import com.mates120.dictionaryparser.debug.Logger;


public class FilesFinder
{
	private String[] extensions;
	private String searchPath = null;

	
	public String getFilePath(String searchPath, String[] extensions) throws Exception
	{
		this.extensions = extensions;
		this.searchPath = searchPath;
		return getValidPathToFile();
	}
	
	public String getFilePath(String searchPath, String extension) throws Exception
	{
		this.extensions = new String[1];
		this.extensions[0] = extension;
		this.searchPath = searchPath;
		return getValidPathToFile();
	}
	
	private File makeDirectoryObject() throws NotADirectoryException
	{
		File directory = new File(this.searchPath);
		if (!directory.isDirectory())
			throw new NotADirectoryException();
		return directory;
	}
	
	private String getValidPathToFile() throws DictionaryParserException
	{
		File directory = this.makeDirectoryObject();
		DictionaryFilenamesFilter filter = new DictionaryFilenamesFilter(this.extensions);
		String[] filesOfDictionaries = directory.list(filter);
		debugPrintFileNames(filesOfDictionaries);
		this.requireOnlyOneFile(filesOfDictionaries.length);	
		return filesOfDictionaries[0];
	}
	
	private void requireOnlyOneFile(int listLenght) throws ToManyAlikeFilesException,
	ExpectedFilesNotFoundExeption
	{
		if (listLenght > 1)
			throw new ToManyAlikeFilesException();
		if (listLenght == 0)
			throw new ExpectedFilesNotFoundExeption();
	}

	private void debugPrintFileNames(String[] fileNames)
	{
		Logger.l().PRINT("debugPrintFileNames:");
		for (int i = 0; i < fileNames.length; ++i)
		{
			Logger.l().PRINT(fileNames[i]);
		}
	}
	
	public class DictionaryFilenamesFilter implements FilenameFilter
	{
		private String[] extensions;
		public DictionaryFilenamesFilter(String[] extensions)
		{
			this.extensions = extensions;
		}
	
		@Override
		public boolean accept(File dir, String filename) {
			for (int i = 0; i < extensions.length; ++i)
				if (filename.endsWith(extensions[i]))
					return true;
			return false;
		}	
	}
}