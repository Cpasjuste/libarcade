package com.greatlittleapps.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences 
{
	static public boolean getBool(Context ctx, String key, boolean defValue )
	{
		return getPrefs(ctx).getBoolean(key, defValue);
	}
	
	static public void setBool(Context ctx, String key, boolean value )
	{
		getPrefs(ctx).edit().putBoolean(key, value).commit();
	}
	
	static public int getInt(Context ctx, String key, int defValue )
	{
		return getPrefs(ctx).getInt(key, defValue);
	}
	
	static public void setInt(Context ctx, String key, int value )
	{
		getPrefs(ctx).edit().putInt(key, value).commit();
	}
	
	static private SharedPreferences getPrefs(Context ctx)
	{
		return ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
	}
}
