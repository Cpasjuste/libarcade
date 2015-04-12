package com.greatlittleapps.utility;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import android.os.AsyncTask;
import android.os.Environment;


public class Filer 
{
	String path;
	String match;
	
	public Filer( String pPath )
	{
		path = pPath;
		
		if( path == null )
			path = Environment.getExternalStorageDirectory().getAbsolutePath();
		
		start();
	}
	
	public Filer( String pPath, String pMatchString )
	{
		path = pPath;
	
		if( path == null )
			path = Environment.getExternalStorageDirectory().getAbsolutePath();
		
		match = pMatchString;
				
		start();
	}
	
	private void start()
	{
		new FilerTask().execute();
	}
	
	protected void onPreExecute(){};
	protected void onPostExecute( final ArrayList<FileInfo> files ){};
	
	private class FilerTask extends AsyncTask< String, Integer, ArrayList<FileInfo> >
	{
		@Override
		protected void onPreExecute()
		{
			Filer.this.onPreExecute();
		}
		
		
		@Override
		protected ArrayList<FileInfo> doInBackground( String... args ) 
		{
			Comparator<FileInfo> byDirThenAlpha = new dirAlphaComparator();
			ArrayList<FileInfo> files = new  ArrayList<FileInfo>();
			
			final File dir = new File( path );
			if( !dir.exists() || dir.isFile() )
				return null;
						    
			final FileFilter fileFilter = new FileFilter() 
			{
				public boolean accept( File file )
				{
					if( file.getPath().startsWith( "." ) 
							|| !file.canRead() 
							|| file.isHidden() )
						return false;
					
					if( match != null )
					{
						if( !file.getName().toLowerCase(Locale.US).contains( match.toLowerCase() ) )
							return false;
					}
					return true;
				}
			};
			
			File[] filelist = dir.listFiles( fileFilter );
			if (filelist != null)
			{
				for (int i = 0; i < filelist.length; i++)
				{
					files.add( new FileInfo( filelist[i] ) );
				}
			}
			Collections.sort( files, byDirThenAlpha );
			return files;
		}

		@Override
		protected void onProgressUpdate( Integer... progress ) 
		{
		}
		
		@Override
		protected void onPostExecute( ArrayList<FileInfo> files ) 
		{
			Filer.this.onPostExecute(files);
		}
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

