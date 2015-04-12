// Module for input using SDL
#include <SDL.h>

#include "burner.h"
#include "inp_sdl_keys.h"

extern int android_pad_up;
extern int android_pad_down;
extern int android_pad_left;
extern int android_pad_right;
extern int android_pad_1;
extern int android_pad_2;
extern int android_pad_3;
extern int android_pad_4;
extern int android_pad_5;
extern int android_pad_6;
extern int android_pad_start;
extern int android_pad_coins;
extern int android_pad_test;
extern int android_pad_service;
extern int android_pad_reset;

int SDLinpSetCooperativeLevel(bool bExclusive, bool /*bForeGround*/){return 0;}
int SDLinpExit(){return 0;}
int SDLinpStart(){return 0;}
int SDLinpJoyAxis(int i, int nAxis){return 0;}
int SDLinpMouseAxis(int i, int nAxis){return 0;}
int SDLinpFind(bool CreateBaseline){return -1;}

bool invert_buttons = false;

/*
CPS2: SF3A - 2 players - 6 buttons
=====================
CinpState: 2 (name:P1 Start || info:p1 start)
CinpState: 200 (name:P1 Up || info:p1 up)
CinpState: 208 (name:P1 Down || info:p1 down)
CinpState: 203 (name:P1 Left || info:p1 left)
CinpState: 205 (name:P1 Right || info:p1 right)
CinpState: 30 (name:P1 Weak Punch || info:p1 fire 1) -> && 47 ?!
CinpState: 31 (name:P1 Medium Punch || info:p1 fire 2)
CinpState: 32 (name:P1 Strong Punch || info:p1 fire 3)
CinpState: 44 (name:P1 Weak Kick || info:p1 fire 4)
CinpState: 45 (name:P1 Medium Kick || info:p1 fire 5)
CinpState: 46 (name:P1 Strong Kick || info:p1 fire 6)
CinpState: 7 (name:P2 Coin || info:p2 coin)
CinpState: 3 (name:P2 Start || info:p2 start)
CinpState: 16386 (name:P2 Up || info:p2 up)
CinpState: 16387 (name:P2 Down || info:p2 down)
CinpState: 16384 (name:P2 Left || info:p2 left)
CinpState: 16385 (name:P2 Right || info:p2 right)
CinpState: 16512 (name:P2 Weak Punch || info:p2 fire 1)
CinpState: 16513 (name:P2 Medium Punch || info:p2 fire 2)
CinpState: 16514 (name:P2 Strong Punch || info:p2 fire 3)
CinpState: 16515 (name:P2 Weak Kick || info:p2 fire 4)
CinpState: 16516 (name:P2 Medium Kick || info:p2 fire 5)
CinpState: 16517 (name:P2 Strong Kick || info:p2 fire 6)
CinpState: 61 (name:Reset || info:reset)
CinpState: 60 (name:Diagnostic || info:diag)
CinpState: 10 (name:Service || info:service)
CinpState: 6 (name:P1 Coin || info:p1 coin)

NEOGEO: MSLUG - 2 players - 4 buttons
=====================
CinpState: 2 (name:P1 Start || info:p1 start)
CinpState: 4 (name:P1 Select || info:p1 select)
CinpState: 200 (name:P1 Up || info:p1 up)
CinpState: 208 (name:P1 Down || info:p1 down)
CinpState: 203 (name:P1 Left || info:p1 left)
CinpState: 205 (name:P1 Right || info:p1 right)
CinpState: 44 (name:P1 Button A || info:p1 fire 1)
CinpState: 45 (name:P1 Button B || info:p1 fire 2)
CinpState: 46 (name:P1 Button C || info:p1 fire 3)
CinpState: 47 (name:P1 Button D || info:p1 fire 4)
CinpState: 7 (name:P2 Coin || info:p2 coin)
CinpState: 3 (name:P2 Start || info:p2 start)
CinpState: 5 (name:P2 Select || info:p2 select)
CinpState: 16386 (name:P2 Up || info:p2 up)
CinpState: 16387 (name:P2 Down || info:p2 down)
CinpState: 16384 (name:P2 Left || info:p2 left)
CinpState: 16385 (name:P2 Right || info:p2 right)
CinpState: 16512 (name:P2 Button A || info:p2 fire 1)
CinpState: 16513 (name:P2 Button B || info:p2 fire 2)
CinpState: 16514 (name:P2 Button C || info:p2 fire 3)
CinpState: 16515 (name:P2 Button D || info:p2 fire 4)
CinpState: 61 (name:Reset || info:reset)
CinpState: 60 (name:Test || info:diag)
CinpState: 10 (name:Service || info:service)
CinpState: 6 (name:P1 Coin || info:p1 coin)
*/

int SDLinpInit()
{
	invert_buttons = false;
	if ((BurnDrvGetHardwareCode() == HARDWARE_CAPCOM_CPS2)
			|| (BurnDrvGetHardwareCode() == HARDWARE_CAPCOM_CPS2_SIMM))
	{
		struct BurnInputInfo bii;
		int buttons = 0;
		for ( UINT32 j = 0; j < 0x1000; j++ )
		{
			if( BurnDrvGetInputInfo( &bii, j ) )
				break;

			if( strstr( bii.szInfo, "p1 fire" ) != NULL
					&& strstr( bii.szName, "Volume" ) == NULL )
			{
				buttons++;
			}
		}

		printf( "buttons: %i", buttons );
		if( buttons == 3 || buttons == 4 )
		{
			invert_buttons = true;
		}
	}
	return 0;
}

int SDLinpGetControlName(int nCode, TCHAR* pszDeviceName, TCHAR* pszControlName)
{
	return 0;
}

// Get the state (pressed = 1, not pressed = 0) of a particular input code
int SDLinpState( int nCode )
{
	int pressed = 0;

	switch( nCode )
	{
		case 2:
			return android_pad_start;

		case 200:
			return android_pad_up;

		case 208:
			return android_pad_down;

		case 203:
			return android_pad_left;

		case 205:
			return android_pad_right;

		case 30:
		case 47:
			return invert_buttons ? android_pad_1 : android_pad_4;
		break;

		case 31:
			return invert_buttons ? android_pad_2 : android_pad_5;
		break;

		case 32:
			return invert_buttons ? android_pad_3 : android_pad_6;
		break;

		case 44:
			return invert_buttons ? android_pad_4 : android_pad_1;
		break;

		case 45:
			return invert_buttons ? android_pad_5 : android_pad_2;
		break;

		case 46:
			return invert_buttons ? android_pad_6 : android_pad_3;
		break;

		case 61: //FBK_F3
			pressed = android_pad_reset;
			android_pad_reset = 0;
		return pressed;

		case 60: //FBK_F2
			pressed = android_pad_test;
			android_pad_test = 0;
		return pressed;

		case 10: //FBK_9
			pressed = android_pad_service;
			android_pad_service = 0;
		return pressed;

		case 6:
		case 4: //neogeo select
			return android_pad_coins;
	}
	return 0;
}

struct InputInOut InputInOutSDL = { SDLinpInit, SDLinpExit, SDLinpSetCooperativeLevel, SDLinpStart, SDLinpState, SDLinpJoyAxis, SDLinpMouseAxis, SDLinpFind, SDLinpGetControlName, NULL, _T("SDL input") };

