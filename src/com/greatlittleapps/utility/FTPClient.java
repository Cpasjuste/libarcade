package com.greatlittleapps.utility;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.greatlittleapps.utility.Utility;

public class FTPClient extends Thread
{
	public String host;
	public int port;
	public String user;
	public String pwd;
	
	
	org.apache.commons.net.ftp.FTPClient ftp;
	int keepAliveTimeout = 300;
	
	public FTPClient( String _host, int _port, String _user, String _pwd )
	{
		host = _host;
		port = _port;
		user = _user;
		pwd = _pwd;
		
		start();
	}

	@Override
	public void run()
	{
		ftp = new org.apache.commons.net.ftp.FTPClient();
		ftp.setControlKeepAliveTimeout( keepAliveTimeout );
		connect();
		
		while( isConnected() )
		{
			try {
				Thread.sleep( 1 );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void connect()
	{
		try
        {
            ftp.connect( host, port );
            onLog( ftp.getReplyString() );
            if ( !FTPReply.isPositiveCompletion( ftp.getReplyCode() ) )
            {
                onError( "FTP server refused connection." );
                return;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            onError( "Could not connect to server." );
            return;
        }

		try
		{
			if ( !ftp.login( user, pwd ) )
	        {
	            ftp.logout();
	            onError( "Could not connect to server." );
	            return;
	        }
			onLog( ftp.getReplyString() );
		}
		catch (IOException e)
        {
            e.printStackTrace();
            onError( "Invalid username/password." );
            return;
        }
		
		ftp.enterLocalPassiveMode();
		onLog( ftp.getReplyString() );

		onConnected();
	}
	
	public FTPFile[] getFiles( String path )
	{
		FTPFile[] files = null;
		
		try 
		{
			files = ftp.listFiles( path );
			onLog( ftp.getReplyString() );
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			onError( ftp.getReplyString() );
		}
		return files;
	}
	
	public String getSystemType()
	{
		String s = "";
		
		try 
		{
			s = ftp.getSystemType();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			onError( "Could not get system type." );
		}
		return s;
	}
	
	public void disconnect()
	{
		if ( ftp.isConnected() )
        {
            try
            {
                ftp.disconnect();
                onLog( ftp.getReplyString() );
            }
            catch (IOException f){}
        }
	}
	
	public boolean isConnected()
	{
		return ftp.isConnected();
	}
	
	protected void onConnected( ){}
	
	protected void onError( String s )
	{
		disconnect();
		Utility.loge( s );
	}
	
	protected void onLog( String s )
	{
		Utility.log( s );
	}
}
