// State dialog module
#include "burner.h"
#include "SDL.h"

extern const char *getDataPath();

char szChoice[512];
int bDrvSaveAll = 0;
extern SDL_Surface* sdlFramebuf;

// The automatic save
int StatedAuto(int bSave)
{
	static TCHAR szName[512] = "";
	int nRet;

	sprintf(szName, "%s/states/%s.fs", getDataPath(), BurnDrvGetText(DRV_NAME));

	if (bSave == 0)
	{
		nRet = BurnStateLoad(szName, bDrvSaveAll, NULL);		// Load ram
		if (nRet && bDrvSaveAll)
		{
			nRet = BurnStateLoad(szName, 0, NULL);				// Couldn't get all - okay just try the nvram
		}
	}
	else
	{
		nRet = BurnStateSave(szName, bDrvSaveAll);				// Save ram
	}

	return nRet;
}

static void CreateStateName(int nSlot)
{
	sprintf(szChoice, "%s/states/%s.%03d", getDataPath(), BurnDrvGetText(DRV_NAME), nSlot);
}

int StatedLoad(int nSlot)
{
	int nRet;

	CreateStateName(nSlot);

	nRet = BurnStateLoad(szChoice, 1, &DrvInitCallback);

	return nRet;
}

int StatedSave(int nSlot)
{
	int nRet;

	if (bDrvOkay == 0) {
		return 1;
	}

    CreateStateName(nSlot);

	nRet = BurnStateSave(szChoice, 1);
	if( !nRet )
	{
		char bmppath[MAX_PATH];
		sprintf( bmppath, "%s.bmp", szChoice );
		SDL_LockSurface( sdlFramebuf );
		SDL_SaveBMP( sdlFramebuf, bmppath );
		SDL_UnlockSurface( sdlFramebuf );
	}

	return nRet;
}

