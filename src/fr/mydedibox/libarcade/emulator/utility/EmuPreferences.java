package fr.mydedibox.libarcade.emulator.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import com.greatlittleapps.utility.Utility;

import fr.mydedibox.libarcade.emulator.effects.EffectList;

public class EmuPreferences 
{
	public static String ROM_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	public static String DATA_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/EmuFrontend";

	private SharedPreferences mPrefs;
	private SharedPreferences.Editor mEditor;
	
	public EmuPreferences( Context pCtx ) 
	{
		this.mPrefs = PreferenceManager.getDefaultSharedPreferences(pCtx);
		this.mEditor = this.mPrefs.edit();
	}
	
	public SharedPreferences getSharedPreferences()
	{
		return this.mPrefs;
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
	
	/*
	 * Effects
	 */
	public int getScreenSize()
	{
		return this.mPrefs.getInt( "screensize", 3 );
	}
	
	public void setScreenSize(final int pScreenSize )
	{
		this.mEditor.putInt( "screensize", pScreenSize );
		this.mEditor.commit();
	}
	
	public String getEffectFast()
	{
		return this.mPrefs.getString( "effectfast", EffectList.effect_scanlines25_name );
	}
	
	public void setEffectFast( String pEffectName )
	{
		this.mEditor.putString( "effectfast", pEffectName );
		this.mEditor.commit();
	}
	
	public void setFrameSkip( int fskip )
	{
		this.mEditor.putInt( "fskip", fskip );
		this.mEditor.commit();
	}
	
	public int getFrameSkip( )
	{
		return this.mPrefs.getInt( "fskip", 0 );
	}
	public boolean useVibration()
	{
		return this.mPrefs.getBoolean( "vibrate", true );
	}
	public void useVibration( boolean pValue )
	{
		this.mEditor.putBoolean( "vibrate", pValue );
		this.mEditor.commit();
	}
	
	/*
	 * Hardware controls
	 */
	public boolean useSwInput()
	{
		return this.mPrefs.getBoolean( "useswinput", true );
	}
	public void useSwInput( boolean pValue )
	{
		this.mEditor.putBoolean( "useswinput", pValue );
		this.mEditor.commit();
	}
	public int getPad( final String pKey )
	{
		return Utility.parseInt(this.mPrefs.getString( pKey, "0" ));
	}
	public void setPad( final String pKey, final int pValue )
	{
		this.mEditor.putString( pKey, ""+pValue );
		this.mEditor.commit();
	}
	public int getPadUp()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_up", "19" ));
	}
	public int getPadDown()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_down", "20" ));
	}
	public int getPadLeft()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_left", "21" ));
	}
	public int getPadRight()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_right", "22" ));
	}
	public int getPad1()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_1", "23" ));
	}
	public int getPad2()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_2", "4" ));
	}
	public int getPad3()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_3", "99" ));
	}
	public int getPad4()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_4", "100" ));
	}
	public int getPad5()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_5", "102" ));
	}
	public int getPad6()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_6", "103" ));
	}
	public int getPadCoins()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_coins", "109" ));
	}
	public int getPadStart()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_start", "108" ));
	}
	public int getPadMenu()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_menu", "82" ));
	}
	/*
	public int getPadSwitch()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_switch", "0" ));
	}
	public int getPadCustom1()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_custom_1", "0" ));
	}
	*/
	public int getPadExit()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_exit", "84" ));
	}
}



