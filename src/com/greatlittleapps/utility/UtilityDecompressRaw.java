package com.greatlittleapps.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;

public class UtilityDecompressRaw 
{
	Context ctx;
	ArrayList<RawResource> list = new ArrayList<RawResource>();

	public UtilityDecompressRaw( Context pContext )
	{
		ctx = pContext;
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
	
	public void OnProcess(){}
	
	public void OnTerminate( boolean success ){}
	
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
					File destination = new File( list.get(i).destination );
					if( !destination.exists() )
						destination.mkdirs();		
					else if( !destination.isDirectory() )
					{
						Utility.log( "destination is not a directory! " + list.get(i).destination );
						OnTerminate( false );
						return;
					}
					
					// extract
					InputStream in = getInputStream( list.get(i).source );
					Utility.log( "size: "+ Utility.formatFileSize( in.available() ) );
					unzip( in, list.get(i).destination );
					in.close();
				}
				OnTerminate( true );
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				Utility.loge( e.toString() );
				OnTerminate( false );
			}
		}
	};

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
			OnTerminate( false );
			return null;
		}
		int resid = res.getIdentifier( pResName, "raw", ctx.getPackageName() );
		InputStream ins = res.openRawResource( resid );
		return ins;
	}
	
	private void unzip( InputStream in, String destination )
 	{ 
 		try  
 		{
 			byte[] buffer = new byte[1024*1024];
 			ZipInputStream zin = new ZipInputStream( in );
 			ZipEntry ze = null;
 			
 			dirCheck( destination );
 			
 			while (( ze = zin.getNextEntry()) != null ) 
 			{ 
 				if( ze.isDirectory() ) 
 				{ 
 					dirCheck( destination + ze.getName() );
 				} 
 				else 
 				{ 
 					Utility.log( "extracting: " + destination + ze.getName() );
 					FileOutputStream fout = new FileOutputStream( destination + ze.getName() );

 					int len;
 					while ((len = zin.read(buffer)) != -1) {
 						OnProcess();
 						fout.write(buffer, 0, len);
 					}
 					zin.closeEntry(); 
 					fout.close(); 
 				}
 			} 
 			zin.close(); 
 		}
 		catch( Exception e )
 		{ 
 			Utility.loge( "unzip: " + e );
 			OnTerminate( false );
		}
	}
	
	private void dirCheck( String dir ) 
	{ 
		File f = new File( dir );
		if( !f.isDirectory() )
			f.mkdirs(); 
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
