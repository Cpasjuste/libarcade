package fr.mydedibox.libarcade.preferences;

import java.io.File;
import com.greatlittleapps.utility.Utility;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.preference.PreferenceManager;

public class EmuPreferences 
{
	public static String DATA_URL = "";
	public static String DATA_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/EmuFrontend";
	public static String ROM_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	public static String ROMINFO_PATH = DATA_PATH + "/rominfo";
	public static String TITLES_PATH = DATA_PATH + "/titles";
	public static String PREVIEWS_PATH = DATA_PATH + "/previews";
	public static String ICONS_PATH = DATA_PATH + "/icons";
	public static String STATE_PATH = DATA_PATH + "/states";
	
	private final Context mCtx;
	private SharedPreferences mPrefs;
	private SharedPreferences.Editor mEditor;
	
	final static public int  CONTROL_DIGITAL = 1;
	final static public int  CONTROL_ANALOG_FAST = 2;
	
	public EmuPreferences( Context pCtx ) 
	{
		this.mCtx = pCtx;
		this.mPrefs = PreferenceManager.getDefaultSharedPreferences(pCtx);
		this.mEditor = this.mPrefs.edit();
	}
	
	public SharedPreferences getSharedPreferences()
	{
		return this.mPrefs;
	}
	
	public boolean updatePrefs( )
	{
		int thisversion = 0;
		int savedversion = Utility.parseInt( this.mPrefs.getString( "version", "0" ) );	
		
		try 
		{
			PackageInfo packageInfo = this.mCtx.getPackageManager().getPackageInfo( this.mCtx.getPackageName() ,0 );
			thisversion = packageInfo.versionCode;
			Utility.log( "Version " + thisversion );
		} 
		catch ( NameNotFoundException e )
		{
			e.printStackTrace();
		}
		
		if( savedversion < thisversion )
		{
			Utility.log( "new version installed, preferences need to be cleared" );
			this.mEditor.clear();
			this.mEditor.commit();
			this.mEditor.putString( "version", Integer.toString(thisversion) );
			this.mEditor.commit();
			return true;
		}
		Utility.log( "Package up to date" );
		return false;
	}
	
	public boolean licenceRead()
	{
		return this.mPrefs.getBoolean( "licenceread", false );
	}
	
	public void setLicenceRead( boolean pValue )
	{
		this.mEditor.putBoolean( "licenceread", pValue );
		this.mEditor.commit();
	}
	
	public void setLaunchedFilepath( String pFilepath )
	{
		this.mEditor.putString( "launchedpath", pFilepath );
		this.mEditor.commit();
	}
	public String getLaunchedFilepath()
	{
		return mPrefs.getString( "launchedpath", null );
	}
	
	/*
	 * Rom's path
	 */
	public void setRomsPath( final String pPath )
	{
		this.mEditor.putString( "rompath", pPath );
		this.mEditor.commit();
	}
	
	public String getRomsPath()
	{
		File rompath = new File( this.mPrefs.getString( "rompath", ROM_PATH ) );
		if( !rompath.exists() )
		{
			if( !rompath.mkdirs() )
			{
				Utility.log( "Could not create rom path, reseting to: " + ROM_PATH );
				this.mEditor.putString( "rompath", "/" );
				this.mEditor.commit();
				return ROM_PATH;
			}
			this.mEditor.putString( "rompath", rompath.getAbsolutePath() );
			this.mEditor.commit();
		}
		return rompath.getAbsolutePath();
	}
	
	public String getCachePath()
	{
		return getRomsPath() + "/cache";
	}
	
	/*
	 * Data path
	 */
	public void setDataPath( final String pPath )
	{
		this.mEditor.putString( "datapath", pPath );
		this.mEditor.commit();
	}
	public String getDataPath()
	{
		File datapath = new File( this.mPrefs.getString( "datapath", DATA_PATH ) );
		if( !datapath.exists() )
		{
			if( !datapath.mkdirs() )
			{
				Utility.log( "Could not create data path, reseting to: " + DATA_PATH );
				this.mEditor.putString( "datapath", DATA_PATH );
				this.mEditor.commit();
				return DATA_PATH;
			}
			this.mEditor.putString( "datapath", datapath.getAbsolutePath() );
			this.mEditor.commit();
		}
		return datapath.getAbsolutePath();
	}
	
	public String getRom()
	{
		return this.mPrefs.getString( "rom", "" );
	}
	public void setRom( final String pRomName )
	{
		this.mEditor.putString( "rom", pRomName );
		this.mEditor.commit();
	}
	
	public void SetShowClones( boolean showClones )
	{
		this.mEditor.putBoolean( "showClones", showClones );
		this.mEditor.commit();
	}
	public boolean GetShowClones()
	{
		return this.mPrefs.getBoolean( "showClones", true );
	}
	
	public void SetDataOk( boolean available )
	{
		this.mEditor.putBoolean( "dataok", available );
		this.mEditor.commit();
	}
	public boolean GetDataOk()
	{
		return this.mPrefs.getBoolean( "dataok", false );
	}
}
