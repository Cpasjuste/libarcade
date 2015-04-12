package fr.mydedibox.libarcade.emulator.sdl;

import com.greatlittleapps.utility.Utility;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class SDLAudio 
{
	private static Thread mAudioThread;
    private static AudioTrack mAudioTrack;
    private static Object buf;
    private static int audioBufSize;
    
    @SuppressWarnings("deprecation")
	public static Object init( int rate, boolean is16Bit, boolean isStereo, int desiredFrames ) 
    {
    	int channels = isStereo ? AudioFormat.CHANNEL_CONFIGURATION_STEREO : 
									AudioFormat.CHANNEL_CONFIGURATION_MONO;
    	int encoding = is16Bit ? AudioFormat.ENCODING_PCM_16BIT :
									AudioFormat.ENCODING_PCM_8BIT;
   
    	//int frameSize = (isStereo ? 2 : 1) * (is16Bit ? 2 : 1);
    	audioBufSize = 4096;//desiredFrames*frameSize;
    	
    	if( mAudioTrack == null )
		{
    		Utility.log( "audioInit: requested frames: " + desiredFrames );
			Utility.log( "audioInit: requested buffer size: " + audioBufSize );
			
			if( AudioTrack.getMinBufferSize( rate, channels, encoding ) > audioBufSize )
			{
				Utility.log( "audioInit: getMinBufferSize > audioBufSize" );
				audioBufSize = AudioTrack.getMinBufferSize( rate, channels, encoding );
				Utility.log( "audioInit: new audioBufSize: " + audioBufSize );
			}
			
			mAudioTrack = new AudioTrack( AudioManager.STREAM_MUSIC,
											rate,
											channels,
											encoding,
											audioBufSize,
											AudioTrack.MODE_STREAM );
			
			buf = is16Bit ? new short[audioBufSize/2] : new byte[audioBufSize/2];
			start();
		}
		return buf;
    }

    public static void start() 
    {
        mAudioThread = new Thread(new Runnable() 
        {
            public void run() 
            {
                mAudioTrack.play();
                SDLJni.nativeRunAudioThread();
            }
        });
        
        // I'd take REALTIME if I could get it!
        mAudioThread.setPriority(Thread.MAX_PRIORITY);
        mAudioThread.start();
    }
 
    public static void writeShortBuffer( short[] buffer ) 
    {
    	//Utility.log( "audioWriteShortBuffer:" + buffer.length );
        for ( int i = 0; i < buffer.length; ) 
        {
            int result = mAudioTrack.write( buffer, i, buffer.length - i );
            if( result > 0 ) 
            {
                i += result;
            }
            /*
            else if( result == 0 ) 
            {
                try 
                {
                    Thread.sleep(1);
                } 
                catch(InterruptedException e) 
                {
                }
            }
            */
        }
    }
    
    public static void writeByteBuffer(byte[] buffer) 
    {
        for (int i = 0; i < audioBufSize; ) {
            int result = mAudioTrack.write(buffer, i, audioBufSize - i);
            if (result > 0) {
                i += result;
            } else if (result == 0) {
                try {
                    Thread.sleep(1);
                } catch(InterruptedException e) {
                    // Nom nom
                }
            } else {
                Utility.log("SDL audio: error return from write(short)");
                return;
            }
        }
    }
    
    public static void quit() 
    {
    	Utility.log("audioQuit");
    	
        if (mAudioThread != null) 
        {
        	Utility.log("AudioThread != null");
            try 
            {
                mAudioThread.join();
            } 
            catch(Exception e) 
            {
                Utility.loge("Problem stopping audio thread: " + e);
            }
            mAudioThread = null;
            Utility.log("Finished waiting for audio thread");
        }
        else
        	Utility.log("mAudioThread == null");  
        
        if (mAudioTrack != null) 
        {
        	Utility.log("mAudioTrack != null");
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
        else
        {
        	Utility.log("mAudioTrack == null");
        }
    }
    
    public static void pause()
    {
    	if( mAudioTrack != null )
    		mAudioTrack.pause();
    }
    
    public static void play()
    {
    	if( mAudioTrack != null )
    		mAudioTrack.play();
    }
}