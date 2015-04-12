package fr.mydedibox.libarcade.emulator.sdl;

import fr.mydedibox.libarcade.emulator.activity.Main;
import android.content.Context;

public class SDLJni 
{
	public static native void setfskip( int n );
	public static native int ispaused();
    public static native void pauseemu( );
    public static native void resumeemu( );
    public static native int getslotnum();
    public static native void statesave( int statenum );
    public static native void stateload( int statenum );
    public static native void emustop();
    public static native void setPadData( int i, long data );
    public static native void setPadSwitch();
    
    public static native void nativeInitWithArgs( String[] pArgs );
    public static native void onNativeResize(int x, int y, int format);
    public static native void nativeRunAudioThread();
    public static native void nativePause();
    public static native void nativeResume();
    
    public static String rom;
    public static String datapath;
    public static String rompath;
    public static String statespath;
    public static String cachepath;
    
    public static String getDataPath()
    {
    	return datapath;
    }
    public static String getRomsPath()
    {
    	return rompath;
    }
    public static String getCachePath()
    {
    	return cachepath;
    }
   
    public static boolean createGLContext(int majorVersion, int minorVersion) 
    {
    	return SDLSurface.createGLContext(majorVersion, minorVersion);
    }
    public static void flipBuffers() 
    {
    	SDLSurface.flipBuffers();
    }
    public static void setActivityTitle(String title) 
    {
    	//Main.surface.setActivityTitle(title);
    }
    public static Context getContext() 
    {
    	return Main.surfaceView.getContext();
    }
    public static Object audioInit(int sampleRate, boolean is16Bit, boolean isStereo, int desiredFrames) 
    {
    	return SDLAudio.init(sampleRate, is16Bit, isStereo, desiredFrames);
    }
    public static void audioStartThread() 
    {
    	SDLAudio.start();
    }
    public static void audioWriteShortBuffer(short[] buffer) 
    {
    	SDLAudio.writeShortBuffer(buffer);
    }
  
    public static void audioWriteByteBuffer(byte[] buffer) 
    {
    	SDLAudio.writeByteBuffer(buffer);
    }
    public static void audioQuit() 
    {
    	SDLAudio.quit();
    }
    
    public static void setErrorMessage( String pMessage ) 
    {
    	Main.setErrorMessage(pMessage);
    }
    public static void showProgressBar( String pMessage, int pMax ) 
    {
    	Main.showProgressBar(pMessage, pMax);
    }
    public static void hideProgressBar() 
    {
    	Main.hideProgressBar();
    }
    public static void setProgressBar ( String pMessage, int pProgress ) 
    {
    	Main.setProgressBar( pMessage, pProgress );
    }
}
