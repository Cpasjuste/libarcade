package com.greatlittleapps.utility;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class UtilityPackage 
{
	public static final String AFBA_DATA_PACKAGE = "fr.mydedibox.afbadata";
	public static final String AFBA_PACKAGE = "fr.mydedibox.libafba";
	public static final String AFBA_ACTIVITY = "fr.mydedibox.libafba.activity.Main";

	
	
	public static boolean isAvailable( Context pCtx, String pPackage )
	{
		try 
		{
			pCtx.getPackageManager().getApplicationInfo( pPackage, 0 );
			return true;
		} 
		catch( NameNotFoundException e ) 
		{
			e.printStackTrace();
			Utility.loge( "package \""+pPackage+"\" not installed" );
			return false;
		}
	}
}
