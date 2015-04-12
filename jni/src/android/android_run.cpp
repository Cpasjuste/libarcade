// Run module
#include "burner.h"

#ifdef ANDROID 
#include "android_snd.h"
extern char android_img_state_path[2048];
extern int android_pause;
extern int android_quit;
extern int android_fskip;
extern const char *getCachePath();
extern const char *getRomsPath();
extern const char *getDataPath();
extern void setErrorMsg( char *msg );
extern void progressBarShow(char *name, int size);
extern void progressBarUpdate(char *msg, int pos);
extern void progressBarHide(void);
#endif

INT32 nAudSampleRate[8];          // sample rate
INT32 nAudVolume;				// Sound volume (% * 100)
INT32 nAudSegCount;          	// Segs in the pdsbLoop buffer
INT32 nAudSegLen;            	// Seg length in samples (calculated from Rate/Fps)
INT32 nAudAllocSegLen;
INT16 *nAudNextSound;       	// The next sound seg we will add to the sample loop
UINT8 bAudOkay;    	// True if DSound was initted okay
UINT8 bAudPlaying;	// True if the Loop buffer is playing
INT32 nAudDSPModule[8];			// DSP module to use: 0 = none, 1 = low-pass filter
UINT32 nAudSelect;
INT32 AudSoundInit(){};

bool bAltPause = 0;

int bAlwaysDrawFrames = 0;

static bool bShowFPS = false;

int counter;								// General purpose variable used when debugging

static unsigned int nNormalLast = 0;		// Last value of timeGetTime()
static int nNormalFrac = 0;					// Extra fraction we did

static bool bAppDoStep = 0;
static bool bAppDoFast = 0;
static int nFastSpeed = 6;

static int GetInput(bool bCopy)
{
	static int i = 0;
	InputMake(bCopy); 						// get input

	// Update Input dialog ever 3 frames
	if (i == 0) {
		//InpdUpdate();
	}

	i++;

	if (i >= 3) {
		i = 0;
	}

	// Update Input Set dialog
	//InpsUpdate();
	return 0;
}
#ifndef ANDROID
static void DisplayFPS()
{
	static time_t fpstimer;
	static unsigned int nPreviousFrames;

	char fpsstring[8];
	time_t temptime = clock();
	float fps = static_cast<float>(nFramesRendered - nPreviousFrames) * CLOCKS_PER_SEC / (temptime - fpstimer);
	sprintf(fpsstring, "%2.1f", fps);
	VidSNewShortMsg(fpsstring, 0xDFDFFF, 480, 0);

	fpstimer = temptime;
	nPreviousFrames = nFramesRendered;
}
#endif

// define this function somewhere above RunMessageLoop()
void ToggleLayer(unsigned char thisLayer)
{
	nBurnLayer ^= thisLayer;				// xor with thisLayer
	VidRedraw();
	VidPaint(0);
}

/*
// Callback used when DSound needs more sound
static int RunGetNextSound(int bDraw)
{
	if (nAudNextSound == NULL) {
		return 1;
	}

	if (bRunPause) {
		if (bAppDoStep) {
			RunFrame(bDraw, 0);
			memset(nAudNextSound, 0, nAudSegLen << 2);	// Write silence into the buffer
		} else {
			RunFrame(bDraw, 1);
		}

		bAppDoStep = 0;									// done one step
		return 0;
	}
	if (bAppDoFast) {									// do more frames
		for (int i = 0; i < nFastSpeed; i++) {
			RunFrame(0, 0);
		}
	}

	// Render frame with sound
	pBurnSoundOut = nAudNextSound;
	RunFrame(bDraw, 0);
	if (bAppDoStep) {
		memset(nAudNextSound, 0, nAudSegLen << 2);		// Write silence into the buffer
	}
	bAppDoStep = 0;										// done one step

	return 0;
}
*/

// With or without sound, run one frame.
// If bDraw is true, it's the last frame before we are up to date, and so we should draw the screen
static int RunFrame(int bDraw, int bPause)
{
	static int bPrevPause = 0;
	static int bPrevDraw = 0;

	if (bPrevDraw && !bPause) {
		VidPaint(0);							// paint the screen (no need to validate)
	}

	if (!bDrvOkay) {
		return 1;
	}

	if (bPause) 
	{
		if (bPause != bPrevPause) 
		{
			VidPaint(2);                        // Redraw the screen (to ensure mode indicators are updated)
		}
	} 
	else 
	{
		nFramesEmulated++;
		nCurrentFrame++;
		GetInput(true);					// Update inputs
	}
	if (bDraw) {
		nFramesRendered++;
		if (VidFrame()) {					// Do one frame
			memset(nAudNextSound, 0, nAudSegLen << 2);
		}
	} 
	else {								// frame skipping
		pBurnDraw = NULL;					// Make sure no image is drawn
		BurnDrvFrame();
	}
	bPrevPause = bPause;
	bPrevDraw = bDraw;

	return 0;
}

int RunIdle()
{
	int nTime, nCount;
	nTime = SDL_GetTicks() - nNormalLast;
	nCount = (nTime * nAppVirtualFps - nNormalFrac) / 100000;

	if (nCount <= 0) {						// No need to do anything for a bit
		SDL_Delay(1);
		return 0;
	}

	nNormalFrac += nCount * 100000;
	nNormalLast += nNormalFrac / nAppVirtualFps;
	nNormalFrac %= nAppVirtualFps;

	/*
	if ( android_fskip > 0 )
	{
		nCount *= (android_fskip+1);
	}
	*/

	if (nCount > 100)
	{						// Limit frame skipping
		nCount = 100;
	}

	//printf( "skip=%i (nCount=%i)", nCount / 10, nCount );
	for (int i = nCount / 10; i > 0; i--)
	{
		// Mid-frames
		RunFrame( 0, 0 );
		SndProcessFrame();
	}

	for (int i = 0; i < android_fskip; i++)
	{
		RunFrame(0, 0);
		SndProcessFrame();
	}

	RunFrame( 1, 0 );							// End-frame
	SndProcessFrame();

	return 0;
}

int RunReset()
{
	// Reset the speed throttling code
	nNormalLast = 0; nNormalFrac = 0;
	if (!bAudPlaying)
	{
		// run without sound
		nNormalLast = SDL_GetTicks();
	}
	return 0;
}

static int RunInit()
{
	// Try to run with sound
	RunReset();

	return 0;
}

static int RunExit()
{
	nNormalLast = 0;
	// Stop sound if it was playing

	SndExit();

	return 0;
}

// The main message loop
int RunMessageLoop()
{

	progressBarShow( "Please Wait", 100 );

	int bRestartVideo;
	int finished= 0;
	do 
	{
		bRestartVideo = 0;

		//MediaInit();

		if (!bVidOkay) {

			// Reinit the video plugin
			VidInit();
			if (!bVidOkay && nVidFullscreen) {

				nVidFullscreen = 0;
				VidInit();
			}

		}

		RunInit();
		SndOpen();
		progressBarHide();
		
		while (!finished)
		{

			bRunPause = android_pause;
			finished = android_quit;
			if( bRunPause )
				SDL_Delay( 10 );
			else
				RunIdle();
		}
		RunExit();
	} while (bRestartVideo);
	return 0;
}
