/*----------------
Stuff to finish:

It wouldn't be a stretch of the imagination to think the whole of the sdl 'port' needs a redo but here are the main things wrong with this version:


There is OSD of any kind which makes it hard to display info to the users.
There are lots of problems with the audio output code.
There are lots of problems with the opengl renderer
probably many other things.
------------------*/
#include "burner.h"

#include "android_snd.h"

extern const char *getRomsPath();


TCHAR* GetIsoPath() { return NULL; }
INT32 CDEmuInit() { return 0; }
INT32 CDEmuExit() { return 0; }
INT32 CDEmuStop() { return 0; }
INT32 CDEmuPlay(UINT8 M, UINT8 S, UINT8 F) { return 0; }
INT32 CDEmuLoadSector(INT32 LBA, char* pBuffer) { return 0; }
UINT8* CDEmuReadTOC(INT32 track) { return 0; }
UINT8* CDEmuReadQChannel() { return 0; }
INT32 CDEmuGetSoundBuffer(INT16* buffer, INT32 samples) { return 0; }
CDEmuStatusValue CDEmuStatus;
bool bDoIpsPatch;
void IpsApplyPatches(UINT8 *, char *) {}
TCHAR szAppHiscorePath[MAX_PATH] = "highscores";
TCHAR szAppSamplesPath[MAX_PATH] = "samples";
TCHAR szAppCheatsPath[MAX_PATH] = "cheats";
TCHAR szAppBlendPath[MAX_PATH] = "blends";
void Reinitialise(void) {}
void NeoCDInfo_Exit() {}
bool bCDEmuOkay = false;
void wav_pause(bool bResume){}
struct VidOut VidOutSDLOpenGL;

int nAppVirtualFps = 6000;			// App fps * 100
bool bRunPause=0;
bool bAlwaysProcessKeyboardInput=0;

void write_gamelist_sdcard();
void print_buttons();

bool startsWith(const char *pre, const char *str)
{
    size_t lenpre = strlen(pre), lenstr = strlen(str);
    return lenstr < lenpre ? false : strncmp(pre, str, lenpre) == 0;
}

void init_emu(int gamenum)
{
	bBurnUseASMCPUEmulation=0;
 	bCheatsAllowed=false;
	ConfigAppLoad();
	ConfigAppSave();
	DrvInit(gamenum,0);
}

int main(int argc, char *argv[])
{
	UINT32 i=0;

	snprintf( szAppRomPaths[0], MAX_PATH, "%s/", getRomsPath() );

	ConfigAppLoad();
	SDL_Init(SDL_INIT_TIMER|SDL_INIT_VIDEO);
	BurnLibInit();

#ifdef ANDROID_WRITE_GAMELIST
	write_gamelist_sdcard();
	exit(0);
#endif

	SDL_WM_SetCaption( "aFBA", "aFBA");
	SDL_ShowCursor(SDL_DISABLE);

	if (argc == 2)
	{
		for (i = 0; i < nBurnDrvCount; i++)
		{
			nBurnDrvSelect[0] = i;
			nBurnDrvActive = i;
			if (strcmp(BurnDrvGetTextA(0), argv[1]) == 0)
				break;
		}

		if (i == nBurnDrvCount)
		{
			printf("%s is not supported by aFBA.",argv[1]);
			return 1;
		}
	}

	InputInit();
	init_emu(i);

	RunMessageLoop();
	InputExit();

	DrvExit();
	ConfigAppSave();
	BurnLibExit();
	SDL_Quit();

	return 0;
}

bool AppProcessKeyboardInput()
{
	return true;
}

TCHAR* ANSIToTCHAR( const char* pszInString, TCHAR* pszOutString, int nOutSize )
{
	if (pszOutString)
	{
		_tcscpy(pszOutString, pszInString);
		return pszOutString;
	}
	return (TCHAR*)pszInString;
}


char* TCHARToANSI(const TCHAR* pszInString, char* pszOutString, int nOutSize)
{
	if (pszOutString)
	{
		strcpy(pszOutString, pszInString);
		return pszOutString;
	}
	return (char*)pszInString;
}

void print_buttons()
{
	int i;

	for (i = 0; i < 100; i++)
	{
		nBurnDrvSelect[0] = i;
		nBurnDrvActive = i;

		printf( "##############" );
		printf( "%s", BurnDrvGetTextA( DRV_FULLNAME ) );

		int buttons = 0;
		for ( UINT32 j = 0; j < 0x1000; j++ )
		{
			struct BurnInputInfo bii;
			INT32 nRet = BurnDrvGetInputInfo( &bii, j );
			if ( nRet )
				break;
			else
			{
				if( strstr( bii.szInfo, "p1 fire" ) != NULL && strstr( bii.szInfo, "Volume" ) == NULL ) {
					//"Volume Up"
					//buttons++;
					printf( "%s: %s", bii.szName, bii.szInfo );
				}
			}
		}
		//printf( "%s: %i", BurnDrvGetTextA( DRV_FULLNAME ), buttons );
	}
	exit(0);
}

void write_gamelist_sdcard()
{
	printf( "START: write_gamelist_sdcard()" );
	char buffer[1024];

	FILE* file = fopen( "/sdcard/aFBA-gamelist.txt", "w+" );
	if( file == NULL )
	{
		printf( "ERROR: could not create \"/sdcard/aFBA-gamelist.txt\"" );
		exit( 0 );
	}

	fputs( "package fr.mydedibox.afba;\n", file );
	fputs( "\n", file );
	fputs( "import java.util.ArrayList;\n", file );
	fputs( "import fr.mydedibox.afba.objects.RomInfo;\n", file );
	fputs( "\n", file );
	fputs( "public class Compatibility\n", file );
	fputs( "{\n", file );
	fputs( "\tpublic ArrayList<RomInfo> list = new ArrayList<RomInfo>();\n\n", file );


	int i, listCount = 0;

	sprintf( buffer, "\tprivate void AddList%i()\n\t{\n", listCount );
	fputs( buffer, file );

	for (i = 0; i < nBurnDrvCount; i++)
	{
		nBurnDrvSelect[0] = i;
		nBurnDrvActive = i;
#if ANDROID_NEOGEO_ONLY
		if( !startsWith( "Neo Geo", BurnDrvGetTextA( DRV_SYSTEM ) ) )
			continue;
#endif
		// fix crash !?
		if( startsWith( "Ninja Princess", BurnDrvGetTextA( DRV_FULLNAME ) ) )
			continue;
		if( startsWith( "NMK004 Internal ROM", BurnDrvGetTextA( DRV_FULLNAME ) ) )
			continue;
		
		int vertical = 0;
		int w, h;
		BurnDrvGetVisibleSize( &w, &h );
		if( BurnDrvGetFlags() & BDF_ORIENTATION_VERTICAL )
		{
			vertical = 1;
			int n = w;
			w = h;
			h = n;
		}

		struct BurnInputInfo bii;
		INT32 nRet = 0;
		int buttons = 0;
		printf( "GAME: %s - SYSTEM: %s", BurnDrvGetTextA( DRV_FULLNAME ), BurnDrvGetTextA( DRV_SYSTEM ) );
		for ( UINT32 j = 0; j < 0x1000; j++ )
		{
			nRet = BurnDrvGetInputInfo( &bii, j );
			if ( nRet )
				break;

			if( strstr( bii.szInfo, "p1 fire" ) != NULL 
				&& strstr( bii.szName, "Volume" ) == NULL ) 
			{
				buttons++;
			}
		}
		
		if( buttons > 6 )
			buttons = 6;

		if( i!=0 && !(i % 512) ) // multiple of 512, create a new function to prevent java function overload
		{
			listCount++;
			sprintf( buffer, "\t}\n\tprivate void AddList%i()\n{\t\n", listCount );
			fputs( buffer, file );
		}

		sprintf( buffer, "\t\tlist.add( new RomInfo( \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", %i, %i, %i, %i, %i ) );\n",
				BurnDrvGetTextA( DRV_FULLNAME ),
				BurnDrvGetTextA( DRV_NAME ),
				BurnDrvGetTextA( DRV_PARENT ),
				BurnDrvGetTextA( DRV_COMMENT ),
				BurnDrvGetTextA( DRV_MANUFACTURER ),
				BurnDrvGetTextA( DRV_SYSTEM ),
				BurnDrvGetTextA( DRV_DATE ),
				BurnDrvIsWorking(),
				buttons,
				w, h, vertical );
		fputs( buffer, file );
	}

	sprintf( buffer, "\t}\n" );
	fputs( buffer, file );

	fputs( "\n\tpublic Compatibility()\n", file );
	fputs( "\t{", file );
	for( i=0; i<listCount+1; i++)
	{
		sprintf( buffer, "\t\tAddList%i();\n", i );
		fputs( buffer, file );
	}
	fputs( "\t}", file );
	fputs( "}", file );

	fflush(file);
	fclose(file);
	
	printf( "END: write_gamelist_sdcard()" );

	exit(0);
}


