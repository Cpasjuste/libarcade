void TimepltSndSoundlatch(UINT8 data);
void TimepltSndReset();
void TimepltSndInit(UINT8 *rom, UINT8 *ram, INT32 z80number);
void TimepltSndExit();
void TimepltSndUpdate(INT16 **pAY8910Buffer, INT16 *pSoundBuf, INT32 nSegmentLength);
INT32 TimepltSndScan(INT32 nAction, INT32 *pnMin);
