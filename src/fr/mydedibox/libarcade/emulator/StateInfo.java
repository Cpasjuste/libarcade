package fr.mydedibox.libarcade.emulator;

import java.io.File;
import java.util.Date;

import com.greatlittleapps.utility.Utility;

import android.graphics.drawable.Drawable;

public class StateInfo 
{
	public File file;
	public int id;
	public String path;
	public String date;
	public Drawable drawable;
	public boolean available = false;
	
	@SuppressWarnings("deprecation")
	public StateInfo( String pPath )
	{
		file = new File( pPath );
		path = pPath;
		date = new Date( file.lastModified() ).toLocaleString();
		drawable = Drawable.createFromPath( pPath + ".bmp" );
		id = Utility.parseInt( pPath.substring( pPath.lastIndexOf('.')+1, pPath.length() ) );
		available = file.exists();
		
		Utility.log( "State path: " + pPath );
		Utility.log( "State id: " + id );
	}
	
	public StateInfo( Drawable d )
	{
		date = "Create new save";
		drawable = d;
	}
	
	public void delete()
	{
		file.delete();
		new File( path + ".bmp" ).delete();
	}
}
