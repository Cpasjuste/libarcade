package com.greatlittleapps.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.net.ftp.FTPFile;

import com.greatlittleapps.utility.FileInfo;


public class FTPFiler 
{
	FTPClient client;
	String path;
	String match;
	
	public FTPFiler( FTPClient _client, String pPath )
	{
		path = path == null ? "/" : pPath;
		client = _client;
	}

	public ArrayList<FileInfo> getFiles()
	{
		Comparator<FileInfo> byDirThenAlpha = new dirAlphaComparator();
		ArrayList<FileInfo> files = new  ArrayList<FileInfo>();
			
		if( client == null || !client.isConnected() )
			return null;
			
		FTPFile[] filelist = client.getFiles( path );
		if (filelist != null)
		{
			for (int i = 0; i < filelist.length; i++)
				files.add( new FileInfo( path, filelist[i] ) );
			
			Collections.sort( files, byDirThenAlpha );
		}
		return files;
	}
	
	private class dirAlphaComparator implements Comparator<FileInfo> 
	{
		// Comparator interface requires defining compare method.
	    public int compare( FileInfo filea, FileInfo fileb ) 
	    {
	        //... Sort directories before files,
	        //    otherwise alphabetical ignoring case.
	        if ( filea.isDirectory() && !fileb.isDirectory() ) 
	        {
	            return -1;

	        } 
	        else if ( !filea.isDirectory() && fileb.isDirectory() ) 
	        {
	            return 1;
	        } 
	        else 
	        {
	            return filea.getName().compareToIgnoreCase( fileb.getName() );
	        }
	    }
	}
}

