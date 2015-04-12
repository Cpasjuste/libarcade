package com.greatlittleapps.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Enumeration;
import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class Utility 
{
	public static String TAG = null;
			
	public static void setTag( Context ctx )
	{
		final PackageManager pm = ctx.getPackageManager();
		ApplicationInfo ai;
		try {
		    ai = pm.getApplicationInfo( ctx.getPackageName(), 0 );
		} catch (final NameNotFoundException e) {
		    ai = null;
		}
		TAG = (String) (ai != null ? pm.getApplicationLabel(ai) : "!!!" );
	}
	
	
	public static void log( final String pString )
	{
		StackTraceElement stack = new Throwable().fillInStackTrace().getStackTrace()[1];
		String tag = stack.getClassName()+": "+stack.getLineNumber();
		try
		{
			Log.d( tag, stack.getMethodName() + ": " + pString );
		}
		catch( NullPointerException e )
		{
			Log.e( tag, "could not log: " + e.toString() );
		}
	}
	
	public static void loge( final String pString )
	{
		StackTraceElement stack = new Throwable().fillInStackTrace().getStackTrace()[1];
		String tag = stack.getClassName()+": "+stack.getLineNumber();
		try
		{
			Log.e( tag, stack.getMethodName() + ": " + pString );
		}
		catch( NullPointerException e )
		{
			Log.e( tag, "could not log: " + e.toString() );
		}
	}
	
	public static void logVisible( final String pString )
	{
		StackTraceElement stack = new Throwable().fillInStackTrace().getStackTrace()[1];
		String tag = stack.getClassName()+": "+stack.getLineNumber();
		try
		{
			Log.d( tag, "################################################" );
			Log.d( tag, "################################################" );		
			Log.d( tag, stack.getMethodName() + ": " + pString );
			Log.d( tag, "################################################" );
			Log.d( tag, "################################################" );
		}
		catch( NullPointerException e )
		{
			Log.e( tag, "could not log: " + e.toString() );
		}
	}
	
	public static String formatFileSize( final long size ) 
	{
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
	
	public static String formatSecondes( final int pSecs )
	{
		int hours = pSecs / 3600,
		remainder = pSecs % 3600,
		minutes = remainder / 60,
		seconds = remainder % 60;
	
		return ( (hours < 10 ? "0" : "") + hours
				+ ":" + (minutes < 10 ? "0" : "") + minutes
				+ ":" + (seconds< 10 ? "0" : "") + seconds );
	}
	
	public static String parseUrl( String pString ) {
		String s = "";
		try 
		{
			//s = "http://www.nzbindex.nl/search/?q="+
			//	URLEncoder.encode(item.file, "iso-8859-1")+
			//	"&age=&max=25&minage=&sort=agedesc&minsize=&maxsize=&dq=&poster=&nfo=&hidespam=0&hidespam=1&more=0";
			
			s = URLEncoder.encode( pString, "iso-8859-1" );
		} 
		catch (UnsupportedEncodingException e) {}
		return s;
	}
	
	public static int parseInt( String pString )
	{
		int ret = 0;
		try
		{
			ret = Integer.parseInt( pString );
		}
		catch(NumberFormatException e) {}
		return ret;
	}
	
	public static long parseLong( String pString )
	{
		long ret = 0;
		try
		{
			ret = Long.parseLong( pString );
		}
		catch(NumberFormatException e) {}
		return ret;
	}
	
	public static boolean parseBoolean( String pString )
	{
		boolean ret = false;
		try
		{
			ret = Boolean.parseBoolean( pString );
		}
		catch(NumberFormatException e) {}
		return ret;
	}
	
	public static void WriteStringToFile ( String pString, String pPath )
	{
		try
		{
			 File file = new File( pPath );
			 FileWriter writer = new FileWriter(file);
			 BufferedWriter out = new BufferedWriter(writer);
			 out.write( pString );
			 out.close();
		} 
		catch (java.io.IOException e) {}
	}
	
	public static String ReadFileAsString( String pPath )
	{
		String ret = "";
		
		try 
		{
		    BufferedReader reader = null;
			reader = new BufferedReader(new FileReader(pPath));
			
		    String line;
		    
			while( ( line = reader.readLine() ) != null)
			{
				ret += line + "\n";
			}
			reader.close();

		    return ret;
		}
		catch (java.io.IOException e) 
		{
			return ret;
		}
	}
	
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
		  Integer.parseInt(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	
	/** Read the object from Base64 string. */
    public static Object unserialize( String s )
    {
    	Object o = null;
    	try
    	{
	        byte [] data = Base64.decode( s, 0 );
	        ObjectInputStream ois = new ObjectInputStream( 
	                                        new ByteArrayInputStream(  data ) );
	        o  = ois.readObject();
	        ois.close();
    	}
    	catch ( Exception e )
    	{
    		loge( "unserialize failed" );
    	}
    	return o;
    }
    
    /** Write the object to a Base64 string. */
    public static String serialize( Serializable o )
    {
    	String ret = null;
    	
    	try
    	{
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream( baos );
            oos.writeObject( o );
            oos.close();
            ret = new String( Base64.encode( baos.toByteArray(), 0 ) );
            baos.close();
    	}
    	catch ( Exception e )
    	{
    		loge( "serialize failed" );
    		e.printStackTrace();
    	}
    	return ret;
    }
    
    public static String getLocalIpAddress()
	{
		try 
		{
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
			{
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) 
				{
					InetAddress inetAddress = enumIpAddr.nextElement();
	            	if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address( inetAddress.getHostAddress() ) )
	            	{
	                 	return inetAddress.getHostAddress().toString();
	            	}
				}
			}
		} 
		catch (Exception ex) 
		{
	              Log.e("IP Address", ex.toString());
	   	}
		return null;
	}
	
	public static boolean isTablet(Context context) 
	{
	    return (context.getResources().getConfiguration().screenLayout
	            & Configuration.SCREENLAYOUT_SIZE_MASK)
	            >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	
	public static Point getScreenSize( Context context )
	{
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = ( WindowManager ) context.getSystemService( Context.WINDOW_SERVICE );
		wm.getDefaultDisplay().getMetrics( dm );
		return new Point( dm.widthPixels, dm.heightPixels );
	}
	
	public static String formatTimeHMS( int sec )
	{
		String hms = "";
		int days = (int)Math.floor(sec / 86400);
		if (days > 0)
		{
			hms = days+"d";
		}
		int hours = (int)Math.floor((sec % 86400) / 3600);
		hms = hms + hours + ":";
		int minutes = (int)Math.floor((sec / 60) % 60);
		if (minutes < 10)
		{
			hms = hms + "0";
		}
		hms = hms + minutes + ':';
		int seconds = (int)Math.floor(sec % 60);
		if (seconds < 10)
		{
			hms = hms + "0";
		}
		hms = hms + seconds;
		return hms;
	}
	
	public static String formatTimeLeft( int sec )
	{
		int days = (int)Math.floor(sec / 86400);
		int hours = (int)Math.floor((sec % 86400) / 3600);
		int minutes = (int)Math.floor((sec / 60) % 60);
		int seconds = (int)Math.floor(sec % 60);

		if (days > 10)
		{
			return days + "d";
		}
		if (days > 0)
		{
			return days + "d " + hours + "h";
		}
		if (hours > 0)
		{
			return hours + "h " + (minutes < 10 ? "0" : "") + minutes + "m";
		}
		if (minutes > 0)
		{
			return minutes + "m " + (seconds < 10 ? "0" : "") + seconds + "s";
		}
		return seconds + "s";
	}
	
	public static void delete(File file) {
		if(file.isDirectory()){
			if(file.list() == null || file.list().length==0){
				file.delete(); 
			} else {
				String files[] = file.list();
				for (String temp : files) {
	        	   File fileDelete = new File(file, temp);
	        	   delete(fileDelete);
				}
				if(file.list().length==0){
					file.delete();
				}
	    	}
		} else {
	    	file.delete();
		}
	}
}
