package fr.mydedibox.libarcade.emulator.sdl;

import android.content.Context;
import com.greatlittleapps.utility.Utility;

import fr.mydedibox.libarcade.emulator.activity.Main;

public class SDLMain implements Runnable 
{
	//private final Context ctx;
		
	public SDLMain( final Context pCtx )
	{
		//this.ctx = pCtx;
	}

	public void run()
	{
		Utility.log( "Starting emulator thread" );
		//Utility.log( Utility.dumpPrefs( ctx ) );
		SDLJni.nativeInitWithArgs( Main.args );
		Utility.log( "emualator thread returned" );
	}
}

