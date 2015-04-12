#ifndef ANDROID_H
#define ANDROID_H

#include <SDL.h>

char android_img_state_path[2048];
int android_pause;
int android_quit;
//int android_fast;
int android_fskip;

const char *getCachePath();
const char *getRomsPath();
const char *getDataPath();

int android_pad_up;
int android_pad_down;
int android_pad_left;
int android_pad_right;
int android_pad_1;
int android_pad_2;
int android_pad_3;
int android_pad_4;
int android_pad_5;
int android_pad_6;
int android_pad_start;
int android_pad_coins;
int android_pad_test;
int android_pad_service;
int android_pad_reset;

struct gamepadList 
{
	int up;
	int down;
	int left;
	int right;
	int a;
	int b;
	int c;
	int d;
	int e;
	int f;
	int start;
	int coins;
	int test;
	int service;
	int reset;
} android_gamepad;

enum  { ANDROID_UP=0x1,		ANDROID_LEFT=0x4,	ANDROID_DOWN=0x10,  ANDROID_RIGHT=0x40,
        ANDROID_SERVICE=1<<16,	ANDROID_TEST=1<<17,	ANDROID_RESET=1<<18, ANDROID_START=1<<8,	ANDROID_COINS=1<<9,
        ANDROID_1=1<<12,	ANDROID_2=1<<13,        ANDROID_3=1<<14,    ANDROID_4=1<<15,
	ANDROID_5=1<<10,    	ANDROID_6=1<<11
};

#endif

