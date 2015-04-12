// Software blitter effects
#include "burner.h"
#include "vid_softfx.h"
#include "xbr.h"

typedef unsigned long uint32;
typedef unsigned short uint16;
typedef unsigned char uint8;


void _2xpm_lq(void *SrcPtr, void *DstPtr, unsigned long SrcPitch, unsigned long DstPitch, unsigned long SrcW, unsigned long SrcH, int nDepth);
void _2xpm_hq(void *SrcPtr, void *DstPtr, unsigned long SrcPitch, unsigned long DstPitch, unsigned long SrcW, unsigned long SrcH, int nDepth);

extern void hq2xS_init(unsigned bits_per_pixel);
extern void hq2xS(unsigned char*, unsigned int, unsigned char*, unsigned char*, unsigned int, int, int);
extern void hq2xS32(unsigned char*, unsigned int, unsigned char*, unsigned char*, unsigned int, int, int);

extern int Init_2xSaI(unsigned int BitFormat, unsigned int systemColorDepth);
extern void _2xSaI32(unsigned char*, unsigned int, unsigned char*, unsigned char*, unsigned int, int, int);
extern void Super2xSaI32(unsigned char*, unsigned int, unsigned char*, unsigned char*, unsigned int, int, int);
extern void SuperEagle32(unsigned char*, unsigned int, unsigned char*, unsigned char*, unsigned int, int, int);

extern void InitLUTs();
extern void RenderHQ2XS(unsigned char*, unsigned int, unsigned char*, unsigned int, int, int, int type);
extern void RenderHQ3XS(unsigned char*, unsigned int, unsigned char*, unsigned int, int, int, int type);

void RenderEPXB(unsigned char*, unsigned int, unsigned char*, unsigned int, int, int, int);
void RenderEPXC(unsigned char*, unsigned int, unsigned char*, unsigned int, int, int, int);

void ddt3x(unsigned char * src,  unsigned int srcPitch, unsigned char * dest, unsigned int dstPitch, int Xres, int Yres);

#if defined __GNUC__
 #include "scale2x.h"
#elif defined _MSC_VER && defined BUILD_X86_ASM
 #include "scale2x_vc.h"
 #define scale2x_16_mmx internal_scale2x_16_mmx
 #define scale2x_32_mmx internal_scale2x_32_mmx
#endif
#include "scale3x.h"

#if defined BUILD_X86_ASM
extern "C" void __cdecl superscale_line(UINT16 *src0, UINT16 *src1, UINT16 *src2, UINT16 *dst, UINT32 width, UINT64 *mask);
extern "C" void __cdecl  superscale_line_75(UINT16 *src0, UINT16 *src1, UINT16 *src2, UINT16 *dst, UINT32 width, UINT64 *mask);

#ifdef __ELF__
 #define LUT16to32 _LUT16to32
 #define RGBtoYUV _RGBtoYUV
 #define hq2x_32 _hq2x_32
 #define hq3x_32 _hq3x_32
 #define hq4x_32 _hq4x_32
#endif

extern "C" void __cdecl _eagle_mmx16(unsigned long* lb, unsigned long* lb2, short width, unsigned long* screen_address1, unsigned long* screen_address2);

extern "C" void __cdecl _2xSaISuperEagleLine(uint8* srcPtr, uint8* deltaPtr, uint32 srcPitch, uint32 width, uint8* dstPtr, uint32 dstPitch, uint16 dstBlah);
extern "C" void __cdecl _2xSaILine(uint8* srcPtr, uint8* deltaPtr, uint32 srcPitch, uint32 width, uint8* dstPtr, uint32 dstPitch, uint16 dstBlah);
extern "C" void __cdecl _2xSaISuper2xSaILine(uint8* srcPtr, uint8* deltaPtr, uint32 srcPitch, uint32 width, uint8* dstPtr, uint32 dstPitch, uint16 dstBlah);
extern "C" void __cdecl Init_2xSaIMMX(uint32 BitFormat);

extern "C" {
	void __cdecl hq2x_32(unsigned char*, unsigned char*, DWORD, DWORD, DWORD);
	void __cdecl hq3x_32(unsigned char*, unsigned char*, DWORD, DWORD, DWORD);
	void __cdecl hq4x_32(unsigned char*, unsigned char*, DWORD, DWORD, DWORD);

	unsigned int LUT16to32[65536];
	unsigned int RGBtoYUV[65536];
}
#endif

#define FXF_MMX		(1 << 31)

static struct { TCHAR* pszName; int nZoom; unsigned int nFlags; } SoftFXInfo[] = {
#ifdef ANDROID
	{ _T("Android Plain Software"),			1, 0	   },
#else
	{ _T("Plain Software Scale"),			2, 0	   },
	{ _T("AdvanceMAME Scale2x"),			2, FXF_MMX },
	{ _T("AdvanceMAME Scale3x"),			3, 0	   },
	{ _T("2xPM LQ"),						2, FXF_MMX },
	{ _T("2xPM HQ"),						2, FXF_MMX },
	{ _T("Eagle Graphics"),					2, FXF_MMX },
	{ _T("SuperEagle"),						2, FXF_MMX },
	{ _T("2xSaI"),							2, FXF_MMX },
	{ _T("Super 2xSaI"),					2, FXF_MMX },
	{ _T("SuperEagle (VBA)"),				2, FXF_MMX },
	{ _T("2xSaI (VBA)"),					2, FXF_MMX },
	{ _T("Super 2xSaI (VBA)"),				2, FXF_MMX },
	{ _T("SuperScale"),						2, FXF_MMX },
	{ _T("SuperScale (75% Scanlines)"),		2, FXF_MMX },
	{ _T("hq2x Filter"),					2, FXF_MMX },
	{ _T("hq3x Filter"),					3, FXF_MMX },
	{ _T("hq4x Filter"),					4, FXF_MMX },
	{ _T("hq2xS (VBA) Filter"),				2, 0       },
	{ _T("hq3xS (VBA) Filter"),				3, FXF_MMX },
	{ _T("hq2xS (SNES9X) Filter"),			2, FXF_MMX },
	{ _T("hq3xS (SNEX9X) Filter"),			3, FXF_MMX },
	{ _T("hq2xBold Filter"),				2, FXF_MMX },
	{ _T("hq3xBold Filter"),				3, FXF_MMX },
	{ _T("EPXB Filter"),					2, FXF_MMX },
	{ _T("EPXC Filter"),					2, FXF_MMX },
	{ _T("2xBR (Squared) Filter"),			2, FXF_MMX },
	{ _T("2xBR (Semi-Rounded) Filter"),		2, FXF_MMX },
	{ _T("2xBR (Rounded) Filter"),			2, FXF_MMX },
	{ _T("3xBR (Squared) Filter"),			3, FXF_MMX },
	{ _T("3xBR (Semi-Rounded) Filter"),		3, FXF_MMX },
	{ _T("3xBR (Rounded) Filter"),			3, FXF_MMX },
	{ _T("4xBR (Squared) Filter"),			4, FXF_MMX },
	{ _T("4xBR (Semi-Rounded) Filter"),		4, FXF_MMX },
	{ _T("4xBR (Rounded) Filter"),			4, FXF_MMX },
	{ _T("DDT3x"),                          3, FXF_MMX },
#endif
};

//static unsigned char* pSoftFXImage = NULL;
static int nSoftFXImageWidth = 0;
static int nSoftFXImageHeight = 0;
static int nSoftFXImagePitch = 0;

static unsigned char* pSoftFXXBuffer = NULL;

static int nSoftFXRotate = 0;
static int nSoftFXBlitter = 0;
static bool nSoftFXEnlarge = 0;

SDL_Surface* sdlFbRotation;
extern SDL_Surface* sdlFramebuf;

static bool MMXSupport()
{
	return 0;
}

TCHAR* VidSoftFXGetEffect(int nEffect)
{
	return SoftFXInfo[nEffect].pszName;
}

int VidSoftFXGetZoom(int nEffect)
{
	return SoftFXInfo[nEffect].nZoom;
}

void VidSoftFXExit()
{
	if (pSoftFXXBuffer) {
		free(pSoftFXXBuffer);
		pSoftFXXBuffer = NULL;
	}

//	if (nSoftFXRotate) {
//		free(pSoftFXImage);
//		pSoftFXImage = NULL;
//	}
//	pSoftFXImage = NULL;

	nSoftFXRotate = 0;
	nSoftFXEnlarge = 0;
	nSoftFXBlitter = 0;

	return;
}

int VidSoftFXInit(int nBlitter, int nRotate)
{
	nSoftFXBlitter = nBlitter;
	nSoftFXEnlarge = true;

//	pSoftFXImage = pVidImage + nVidImageLeft * nVidImageBPP;

	nSoftFXImageWidth = nVidImageWidth; nSoftFXImageHeight = nVidImageHeight;

	nSoftFXRotate = 0;
    if (bDrvOkay) {
		BurnDrvGetFullSize(&nSoftFXImageWidth, &nSoftFXImageHeight);

		if ((nRotate & 1) && (BurnDrvGetFlags() & BDF_ORIENTATION_VERTICAL)) {
			nSoftFXRotate |= 1;

			BurnDrvGetFullSize(&nSoftFXImageHeight, &nSoftFXImageWidth);
		}

		if ((nRotate & 2) && (BurnDrvGetFlags() & BDF_ORIENTATION_FLIPPED)) {
			nSoftFXRotate |= 2;
		}

		if (nSoftFXRotate) {
			Uint32 Rmask, Gmask, Bmask, Amask;
			int bpp;
			SDL_PixelFormatEnumToMasks(SDL_PIXELFORMAT_RGB565, &bpp, &Rmask, &Gmask, &Bmask,&Amask);
			sdlFbRotation = SDL_CreateRGBSurface( 0, sdlFramebuf->w, sdlFramebuf->h, bpp, Rmask, Gmask, Bmask, Amask);
					/*SDL_CreateRGBSurface(
					0,
					sdlFramebuf->w, sdlFramebuf->h,
					sdlFramebuf->format->BytesPerPixel,
					sdlFramebuf->format->Rmask, sdlFramebuf->format->Gmask, sdlFramebuf->format->Bmask, sdlFramebuf->format->Amask);
					*/
			if(sdlFbRotation == NULL) {
			        printf("CreateRGBSurface failed: %s\n", SDL_GetError());
			        VidSoftFXExit();
			        return 1;
			    }
		}
		//if (nSoftFXRotate) {
		//	pSoftFXImage = (unsigned char*)malloc(nSoftFXImageWidth * nSoftFXImageHeight * nVidImageBPP);
		//	if (pSoftFXImage == NULL) {
		//		VidSoftFXExit();
		//		return 1;
		//	}
		//}
	}
	nSoftFXImagePitch = nSoftFXImageWidth * nVidImageBPP;

	if (nSoftFXBlitter >= FILTER_SUPEREAGLE && nSoftFXBlitter <= FILTER_SUPER_2XSAI) {		// Initialize the 2xSaI engine
		pSoftFXXBuffer = (unsigned char*)malloc((nSoftFXImageHeight + 2) * nSoftFXImagePitch);
		if (pSoftFXXBuffer == NULL) {
			VidSoftFXExit();
			return 1;
		}

		memset(pSoftFXXBuffer, 0, (nSoftFXImageHeight + 2) * nSoftFXImagePitch);
	}
	
	if (nSoftFXBlitter >= FILTER_SUPEREAGLE_VBA && nSoftFXBlitter <= FILTER_SUPER_2XSAI_VBA) {
		int nMemLen = (nSoftFXImageHeight + /*2*/4) * nSoftFXImagePitch;
		pSoftFXXBuffer = (unsigned char*)malloc(nMemLen);
		if (pSoftFXXBuffer == NULL) {
			VidSoftFXExit();
			return 1;
		}
		memset(pSoftFXXBuffer, 0, nMemLen);
	}

#ifdef PRINT_DEBUG_INFO
   	dprintf(_T("  * SoftFX initialised: using %s in %i-bit mode.\n"), SoftFXInfo[nSoftFXBlitter].pszName, nVidImageDepth);
#endif

	return 0;
}

static void VidSoftFXRotate(SDL_Surface* pSurf)
{
	if(!nSoftFXRotate) {
		return;
	}
	else {
		unsigned char* ps;
		unsigned char* pd = (unsigned char*)sdlFbRotation->pixels;
		if (nSoftFXRotate & 2) {
			pd += nSoftFXImageHeight * nSoftFXImagePitch - nVidImageBPP;
		}

		switch (nVidImageBPP) {
			case 4:	{
				switch (nSoftFXRotate) {
					case 1: {
						for (int y = 0; y < nSoftFXImageHeight; y++) {
							ps = (unsigned char*)pSurf->pixels + (nSoftFXImageHeight + nVidImageLeft - 1 - y) * 4;
							for (int x = 0; x < nSoftFXImageWidth; x++) {
								*(int*)pd = *(int*)ps;
								ps += nVidImagePitch;
								pd += 4;
							}
						}
						break;
					}
					case 2: {
						for (int y = 0; y < nSoftFXImageHeight; y++) {
							ps = (unsigned char*)pSurf->pixels + y * nVidImagePitch + nVidImageLeft * 2;
							for (int x = 0; x < nSoftFXImageWidth; x++) {
								*(int*)pd = *(int*)ps;
								ps += 4;
								pd -= 4;
							}
						}
						break;
					}
					case 3: {
						for (int y = 0; y <nSoftFXImageHeight; y++) {
							ps = (unsigned char*)pSurf->pixels + (nSoftFXImageHeight + nVidImageLeft - 1 - y) * 4;
							for (int x = 0; x < nSoftFXImageWidth; x++) {
								*(int*)pd = *(int*)ps;
								ps += nVidImagePitch;
								pd -= 4;
							}
						}
						break;
					}
				}
				break;
			}

			case 3: {
				switch (nSoftFXRotate) {
					case 1: {
						for (int y = 0; y < nSoftFXImageHeight; y++) {
							ps = (unsigned char*)pSurf->pixels + (nSoftFXImageHeight + nVidImageLeft - 1 - y) * 3;
							for (int x = 0; x < nSoftFXImageWidth; x++) {
								pd[0] = ps[0];
								pd[1] = ps[2];
								pd[2] = ps[2];
								ps += nVidImagePitch;
								pd += 3;
							}
						}
						break;
					}
					case 2: {
						for (int y = 0; y < nSoftFXImageHeight; y++) {
							ps = (unsigned char*)pSurf->pixels + y * nVidImagePitch + nVidImageLeft * 3;
							for (int x = 0; x < nSoftFXImageWidth; x++) {
								pd[0] = ps[0];
								pd[1] = ps[2];
								pd[2] = ps[2];
								ps += 3;
								pd -= 3;
							}
						}
						break;
					}
					case 3: {
						for (int y = 0; y < nSoftFXImageHeight; y++) {
							ps = (unsigned char*)pSurf->pixels + (nSoftFXImageHeight + nVidImageLeft - 1 - y) * 3;
							for (int x = 0; x < nSoftFXImageWidth; x++) {
								pd[0] = ps[0];
								pd[1] = ps[2];
								pd[2] = ps[2];
								ps += nVidImagePitch;
								pd -= 3;
							}
						}
						break;
					}
				}
				break;
			}

			case 2:	{
				switch (nSoftFXRotate) {
					case 1: {
						for (int y = 0; y < nSoftFXImageHeight; y++) {
							ps = (unsigned char*)pSurf->pixels + (nSoftFXImageHeight + nVidImageLeft - 1 - y) * 2;
							for (int x = 0; x < nSoftFXImageWidth; x++) {
								*(short*)pd = *(short*)ps;
								ps += nVidImagePitch;
								pd += 2;
							}
						}
						break;
					}
					case 2: {
						for (int y = 0; y < nSoftFXImageHeight; y++) {
							ps = (unsigned char*)pSurf->pixels + y * nVidImagePitch + nVidImageLeft * 2;
							for (int x = 0; x < nSoftFXImageWidth; x++) {
								*(short*)pd = *(short*)ps;
								ps += 2;
								pd -= 2;
							}
						}
						break;
					}
					case 3: {
						for (int y = 0; y < nSoftFXImageHeight; y++) {
							ps = (unsigned char*)pSurf->pixels + (nSoftFXImageHeight + nVidImageLeft - 1 - y) * 2;
							for (int x = 0; x < nSoftFXImageWidth; x++) {
								*(short*)pd = *(short*)ps;
								ps += nVidImagePitch;
								pd -= 2;
							}
						}
						break;
					}
				}
				break;
			}
		}
		SDL_BlitSurface(sdlFbRotation, NULL, sdlFramebuf, NULL);
	}
}

int VidSoftFXApplyEffectSDL(SDL_Surface* pSurf)
{
	VidSoftFXRotate(pSurf);
	return 0;
}
