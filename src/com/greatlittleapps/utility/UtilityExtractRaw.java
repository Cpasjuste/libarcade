package com.greatlittleapps.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;

public class UtilityExtractRaw 
{
	Context ctx;
	ArrayList<RawResource> list = new ArrayList<RawResource>();

	public UtilityExtractRaw( Context pContext )
	{
		ctx = pContext;
	}
	
	public UtilityExtractRaw( Context pContext, String pResName, String pDestination )
	{
		ctx = pContext;
		add( pResName, pDestination );
	}

	public void add( String pResName, String pDestination )
	{
		this.list.add( new RawResource( pResName, pDestination ) );
	}
	
	public void process()
	{
		Thread th = new Thread( processThread );
		th.start();
	}
	
	public void onTerminate( boolean success ){}
	
	private Runnable processThread = new Runnable()
	{
		@Override
		public void run() 
		{
			Utility.log( "Starting processThread" );
			
			try 
			{
				for( int i=0; i<list.size(); i++ )
				{
					// check extract folder
					File f = new File( list.get(i).destination );
					File path = f.getParentFile();
					Utility.log( "destination path: " + path.getAbsolutePath() );
					if( !path.exists() )
						path.mkdirs();		
					else if( !path.isDirectory() )
					{
						Utility.log( "destination is not a directory! " + list.get(i).destination );
						onTerminate( false );
						return;
					}
					
					// extract
					InputStream in = getInputStream( list.get(i).source );
					FileOutputStream out = new FileOutputStream( list.get(i).destination );
					extract( in, out );
					out.close();
					in.close();
				}
				onTerminate( true );
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				Utility.loge( e.toString() );
				onTerminate( false );
			}
		}
	};
	
	private void extract( InputStream in, FileOutputStream out )
 	{ 
 		try  
 		{
 			byte[] buffer = new byte[1024*1024];
 			int read = 0;
 			
 			while( ( read = in.read(buffer) ) > 0 ) 
 			{
 				out.write( buffer, 0, read );
 			}
 		}
 		catch( Exception e )
 		{ 
 			Utility.loge( "extract: " + e );
 			onTerminate( false );
		}
	}
	
	private InputStream getInputStream( String pResName )
	{
		Resources res = null;
		try 
		{
			res = ctx.getPackageManager().getResourcesForApplication( ctx.getPackageName() );
		} 
		catch (NameNotFoundException e) 
		{
			e.printStackTrace();
			onTerminate( false );
			return null;
		}
		int resid = res.getIdentifier( pResName, "raw", ctx.getPackageName() );
		InputStream ins = res.openRawResource( resid );
		return ins;
	}
	
	private class RawResource
	{
		String source;
		String destination;
		
		public RawResource( String pResName, String pDestPath )
		{
			this.source = pResName;
			this.destination = pDestPath;
		}
	}
}

