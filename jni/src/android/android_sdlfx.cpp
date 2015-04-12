// Software blitter effects via SDL
#include "burner.h"
#include "vid_support.h"
#include "vid_softfx.h"

static int nInitedSubsytems = 0;
static int nGameWidth = 0, nGameHeight = 0;			// screen size
static int nSize;
static int nUseBlitter;
static int nUseSys;
static int nDirectAccess = 1;
static int nRotateGame = 0;
SDL_Surface* sdlFramebuf = NULL;

static int BlitFXExit()
{
	VidSFreeVidImage();
	nRotateGame = 0;
	return 0;
}

static int BlitFXInit()
{
	if (nRotateGame & 1) {
		nVidImageWidth = nGameHeight;
		nVidImageHeight = nGameWidth;
	} else {
		nVidImageWidth = nGameWidth;
		nVidImageHeight = nGameHeight;
	}

	if (nUseBlitter >= 7 && nUseBlitter <= 9) {
		nVidImageDepth = 16;								// Use 565 format
	} else {
		nVidImageDepth = sdlFramebuf->format->BitsPerPixel;// Use color depth of primary surface
	}
	nVidImageBPP = sdlFramebuf->format->BytesPerPixel;
	nBurnBpp = nVidImageBPP;								// Set Burn library Bytes per pixel

	// Use our callback to get colors:
	SetBurnHighCol(nVidImageDepth);

	// Make the normal memory buffer
	if (VidSAllocVidImage()) {
		BlitFXExit();
		return 1;
	}

	return 0;
}

static int Exit()
{
	BlitFXExit();

	if (!(nInitedSubsytems & SDL_INIT_VIDEO)) {
		SDL_QuitSubSystem(SDL_INIT_VIDEO);
	}
	nInitedSubsytems = 0;

	return 0;
}

static int Init()
{
	nInitedSubsytems = SDL_WasInit(SDL_INIT_VIDEO);

	if (!(nInitedSubsytems & SDL_INIT_VIDEO)) {
		SDL_InitSubSystem(SDL_INIT_VIDEO);
	}

	nUseBlitter = 0;
	nGameWidth = nVidImageWidth; nGameHeight = nVidImageHeight;
	nRotateGame = 0;

	if (bDrvOkay) {
		// Get the game screen size
		BurnDrvGetVisibleSize(&nGameWidth, &nGameHeight);

		printf( "vid_sdlfx.Init: nGame: %ix%i", nGameWidth, nGameHeight );

	    if (BurnDrvGetFlags() & BDF_ORIENTATION_VERTICAL) {
			if (nVidRotationAdjust & 1) {
				int n = nGameWidth;
				nGameWidth = nGameHeight;
				nGameHeight = n;
				nRotateGame |= (nVidRotationAdjust & 2);
			} else {
				nRotateGame |= 1;
			}
		}

		if (BurnDrvGetFlags() & BDF_ORIENTATION_FLIPPED) {
			nRotateGame ^= 2;
		}
	}

	nSize = VidSoftFXGetZoom(nUseBlitter);
	bVidScanlines = 0;								// !!!

	if( ( sdlFramebuf = SDL_SetVideoMode(nGameWidth * nSize, nGameHeight * nSize, 0, SDL_RESIZABLE | SDL_HWSURFACE ) ) == NULL )
		return 1;

	printf("android_sdlfx.Init: SDL_SetVideoMode( %i, %i )", nGameWidth * nSize, nGameHeight * nSize );

	printf("android_sdlfx.Init: sdlFramebuf PixelFormat = %s",
				SDL_GetPixelFormatName(
						SDL_MasksToPixelFormatEnum(
								sdlFramebuf->format->BitsPerPixel,
								sdlFramebuf->format->Rmask,
								sdlFramebuf->format->Gmask,
								sdlFramebuf->format->Bmask,
								sdlFramebuf->format->Amask) ) );

	printf("android_sdlfx.Init: ( %i, %u, %u, %u, %u )",
			sdlFramebuf->format->BitsPerPixel,
			sdlFramebuf->format->Rmask,
			sdlFramebuf->format->Gmask,
			sdlFramebuf->format->Bmask,
			sdlFramebuf->format->Amask );

	SDL_SetClipRect(sdlFramebuf, NULL);

	// Initialize the buffer surfaces
	BlitFXInit();

	if (VidSoftFXInit(nUseBlitter, nRotateGame)) {
		if (VidSoftFXInit(0, nRotateGame)) {
			Exit();
			return 1;
		}
	}

	printf( "vid_sdlfx.Init: nRotateGame=%i", nRotateGame );

	return 0;
}

static int vidScale(RECT* , int, int)
{
	return 0;
}

static int MemToSurf()
{
	VidSoftFXApplyEffectSDL( sdlFramebuf );
	return 0;
}

// Run one frame and render the screen
static int Frame(bool bRedraw)						// bRedraw = 0
{
	//if (pVidImage == NULL) {
	if( sdlFramebuf == NULL ) {
	printf("sdlFramebuf==NULL");
		return 1;
	}

	if (bDrvOkay) {
		if (bRedraw) {								// Redraw current frame
			if (BurnDrvRedraw()) {
				BurnDrvFrame();						// No redraw function provided, advance one frame
			}
		} else {
			BurnDrvFrame();							// Run one frame and draw the screen
		}
	}
	else
	{
		printf("bDrvOkay != TRUE");
		return 1;
	}

	MemToSurf();									// Copy the memory buffer to the directdraw buffer for later blitting
	return 0;
}

// Paint the BlitFX surface onto the primary surface
static int Paint(int bValidate)
{
	SDL_UpdateRect(sdlFramebuf, 0, 0, 0, 0);
	return 0;
}

static int GetSettings(InterfaceInfo* pInfo)
{
	TCHAR szString[MAX_PATH] = _T("");

	_sntprintf(szString, MAX_PATH, _T("Prescaling using %s (%i� zoom)"), VidSoftFXGetEffect(nUseBlitter), nSize);
	IntInfoAddStringModule(pInfo, szString);

	if (nRotateGame) {
		IntInfoAddStringModule(pInfo, _T("Using software rotation"));
	}

	return 0;
}

// The Video Output plugin:
struct VidOut VidOutSDLFX = { Init, Exit, Frame, Paint, vidScale, GetSettings, _T("SDL Software Effects video output") };
