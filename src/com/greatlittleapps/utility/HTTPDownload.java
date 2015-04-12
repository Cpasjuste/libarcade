package com.greatlittleapps.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HTTPDownload 
{
	String httpPath;
	String destination;
	
	public HTTPDownload( String pHttpPath, String pDestination )
	{
		this.httpPath = pHttpPath;
		this.destination = pDestination;
	}
	
	public void download()
	{
		new Thread( downloadThread ).start();
	}
	
	public void onTerminate( boolean success ){}
	
	public void onProgress( int current, int total ){}
	
	private Runnable downloadThread = new Runnable()
	{
		@Override
		public void run()
		{
			try 
			{
		        URL url = new URL( httpPath );
		        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		        urlConnection.setRequestMethod("GET");
		        urlConnection.setDoOutput(true);
		        urlConnection.connect();
	
		        File file = new File( destination );
		        FileOutputStream fileOutput = new FileOutputStream( file );
		        InputStream inputStream = urlConnection.getInputStream();
		        int totalSize = urlConnection.getContentLength();
		        int downloadedSize = 0;
	
		        //create a buffer...
		        byte[] buffer = new byte[1024];
		        int bufferLength = 0; 

		        onProgress( downloadedSize, totalSize );
		        
		        Utility.log( "download file size: " + totalSize );
		        
		        while ( (bufferLength = inputStream.read(buffer)) > 0 ) 
		        {
		                fileOutput.write(buffer, 0, bufferLength);
		                downloadedSize += bufferLength;
		                //String msg = Utility.formatFileSize( downloadedSize ) + " / " +  Utility.formatFileSize( totalSize );
		                onProgress( downloadedSize, totalSize );
		        }
		        fileOutput.close();
		       
		        Utility.log( "downloaded file size: " + file.length() );
		       // onTerminate( file.length() == totalSize ? true : false );
		        onTerminate( true );
		        return;
			} 
			catch (MalformedURLException e) {e.printStackTrace();} 
			catch (IOException e) {e.printStackTrace();}
			onTerminate( false );
		}
	};
}


