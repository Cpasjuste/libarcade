#include "burner.h"
#include "android_snd.h"
#include "SDL.h"

void uninit();

int option_samplerate = 1;

int BUFFSIZE;
int NUM_BUFS;

bool GameMute = false;
extern int nBurnFPS;
int dspfd = -1;
volatile short *pOutput[8];
unsigned short *audioBuffer;
static int audioBufferSize = 0;
static int audioChannels = 2;
static int currentbuffer = 0;

int SAMPLESIZE = 512;

static unsigned char *buffer[15];

static unsigned int buf_read=0;
static unsigned int buf_write=0;
static unsigned int buf_read_pos=0;
static unsigned int buf_write_pos=0;
static int full_buffers=0;
static int buffered_bytes=0;


static int write_buffer( unsigned char* data, int len )
{
	int len2=0;
	int x;

	while( len>0 )
	{
		if( full_buffers == NUM_BUFS )
			break;

		x = BUFFSIZE - buf_write_pos;

		if( x > len )
			x = len;

		memcpy( buffer[buf_write]+buf_write_pos, data+len2, x );

		len2+=x;
		len-=x;

		buffered_bytes += x; buf_write_pos += x;

		if( buf_write_pos >= BUFFSIZE )
		{
			// block is full, find next!
			buf_write = (buf_write+1)%NUM_BUFS;
			++full_buffers;
			buf_write_pos=0;
		}
	}
	return len2;
}

static int read_buffer( unsigned char* data, int len )
{
	int len2=0;
	int x;

	while( len > 0 )
	{
		if(full_buffers==0)
			break; // no more data buffered!

		x=BUFFSIZE-buf_read_pos;

		if(x>len)
			x=len;

		memcpy(data+len2,buffer[buf_read]+buf_read_pos,x);
		SDL_MixAudio( data+len2, data+len2, x, SDL_MIX_MAXVOLUME );
		len2+=x; len-=x;
		buffered_bytes-=x; buf_read_pos+=x;

		if(buf_read_pos>=BUFFSIZE)
		{
		   // block is empty, find next!
		   buf_read=(buf_read+1)%NUM_BUFS;
		   --full_buffers;
		   buf_read_pos=0;
		}
	}
	return len2;
}

// end ring buffer stuff

// SDL Callback function
void outputaudio( void *unused, Uint8 *stream, int len )
{
	read_buffer( stream, len );
}

// stop playing and empty buffers (for seeking/pause)
static void SndReset(void)
{
	/* Reset ring-buffer state */
	buf_read=0;
	buf_write=0;
	buf_read_pos=0;
	buf_write_pos=0;

	full_buffers=0;
	buffered_bytes=0;
}

static void SndPause(void)
{
	SDL_PauseAudio(1);
}

static void SndResume(void)
{
	SDL_PauseAudio(0);
}

// plays 'len' bytes of 'data'
// it should round it down to outburst*n
// return: number of bytes played
static int play( unsigned char* data,int len,int flags )
{
#if 0
	int ret;

	/* Audio locking prohibits call of outputaudio */
	SDL_LockAudio();
	// copy audio stream into ring-buffer
	ret = write_buffer(data, len);
	SDL_UnlockAudio();

	return ret;
#else
	return write_buffer(data, len);
#endif
}

static int configure( int rate, int channels, int format )
{
	SDL_AudioSpec aspec, obtained;

	aspec.format   = format;
	aspec.freq     = rate;
	aspec.channels = channels;
	aspec.samples  = SAMPLESIZE;
	aspec.callback = outputaudio;
	aspec.userdata = NULL;

	if( SDL_Init ( SDL_INIT_AUDIO ) )
		return 0;

	printf( "desired audio samples %d\n", aspec.samples );

	if( SDL_OpenAudio( &aspec, &obtained ) < 0 )
		return 0;

	printf( "obtained audio samples %d\n", obtained.samples );
	printf( "obtained audio buffersize %d\n", obtained.size );

	SDL_PauseAudio(0);

	return 1;
}

int SndInit()
{
//	if ((BurnDrvGetHardwareCode() == HARDWARE_CAPCOM_CPS1)
//			|| (BurnDrvGetHardwareCode() == HARDWARE_CAPCOM_CPS1_GENERIC))
//		option_samplerate = 0;

	switch( option_samplerate )
	{
		case 1:
			nBurnSoundRate 	= 22050;
			SAMPLESIZE		= 2048;
			BUFFSIZE		= SAMPLESIZE*2;
			NUM_BUFS		= 8;
		break;

		case 2:
			nBurnSoundRate	= 44100;
			SAMPLESIZE		= 4096;
			BUFFSIZE		= SAMPLESIZE*2;
			NUM_BUFS		= 8;
        break;

		default:
			nBurnSoundRate	= 11025;
			SAMPLESIZE		= 512;
			BUFFSIZE		= SAMPLESIZE*2*2;
			NUM_BUFS		= 4;
     	break;
	}

	nBurnSoundLen = ((nBurnSoundRate * 100) / nBurnFPS );
	pBurnSoundOut = NULL;
	dspfd = -1;

	return 0;
}

int SndOpen()
{
	unsigned int BufferSize;
	unsigned int bufferStart;

	BufferSize = (nBurnSoundLen * audioChannels * AUDIO_BLOCKS)*2 + SAMPLESIZE/*512*/;
	audioBuffer= (unsigned short *)malloc(BufferSize);
	audioBufferSize = nBurnSoundLen * audioChannels * 2;
	memset( audioBuffer, 0 ,BufferSize );
	//BUFFSIZE = audioBufferSize;
	audioBuffer[1]=(audioBuffer[0]=(nBurnSoundLen * audioChannels * 2));
	audioBuffer[2]=(1000000000/nBurnSoundRate)&0xFFFF;
	audioBuffer[3]=(1000000000/nBurnSoundRate)>>16;
	bufferStart = (unsigned int)&audioBuffer[4];
	pOutput[0] = (short*)bufferStart;
	pOutput[1] = (short*)(bufferStart+1*audioBuffer[1]);
	pOutput[2] = (short*)(bufferStart+2*audioBuffer[1]);
	pOutput[3] = (short*)(bufferStart+3*audioBuffer[1]);
	pOutput[4] = (short*)(bufferStart+4*audioBuffer[1]);
	pOutput[5] = (short*)(bufferStart+5*audioBuffer[1]);
	pOutput[6] = (short*)(bufferStart+6*audioBuffer[1]);
	pOutput[7] = (short*)(bufferStart+7*audioBuffer[1]);

	if ( !GameMute )
	{
        for(int i=0;i<NUM_BUFS;i++) 
			buffer[i]=(unsigned char *) malloc(BUFFSIZE);

        dspfd=configure( nBurnSoundRate, audioChannels, AUDIO_S16 );
	
		if ( dspfd > 0 )
		{
		    pBurnSoundOut  = (short*)pOutput[0];
		    SndResume();
			return 0;
		}
		else
		{
			nBurnSoundRate	= 0;
			nBurnSoundLen	= 0;
		}
	}
	
	return -1;
}

void SndProcessFrame()
{
    if ( dspfd > 0 )
    {
        play( (unsigned char *)pOutput[currentbuffer], audioBufferSize, 0 );
        ++ currentbuffer &= 7;
        pBurnSoundOut = (short*)pOutput[currentbuffer];
    }
}

void SndClose()
{
	if (dspfd > 0) 
		uninit();
		
	dspfd = -1;
}

void SndExit()
{
	if( dspfd > 0 )
		uninit();

	pBurnSoundOut = NULL;
	dspfd = -1;

	if( audioBuffer )
	{
	    free(audioBuffer);
	    audioBuffer=NULL;
	}

}

void uninit()
{
	SDL_CloseAudio();
	SDL_QuitSubSystem(SDL_INIT_AUDIO);
}

