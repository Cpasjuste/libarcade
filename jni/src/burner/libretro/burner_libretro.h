#ifndef _BURNER_LIBRETRO_H
#define _BURNER_LIBRETRO_H

#include "gameinp.h"
#include "input/inp_keys.h"

extern int bDrvOkay;
extern int bRunPause;
extern bool bAlwaysProcessKeyboardInput;

#ifdef _MSC_VER
#define snprintf _snprintf
#define ANSIToTCHAR(str, foo, bar) (str)
#endif

extern void InpDIPSWResetDIPs (void);

#endif
