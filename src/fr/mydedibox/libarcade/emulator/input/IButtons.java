package fr.mydedibox.libarcade.emulator.input;

public interface IButtons 
{
	final public static int INVALID_POINTER_ID = -1;
	
	final public static int BTN_UP = 0x1;
	final public static int BTN_LEFT=0x4;
	final public static int BTN_DOWN=0x10;
	final public static int BTN_RIGHT=0x40;
	
	final public static int BTN_1=1<<12;
	final public static int BTN_2=1<<13;
	final public static int BTN_3=1<<14;
	final public static int BTN_4=1<<15;
	final public static int BTN_5=1<<10;
	final public static int BTN_6=1<<11;
	
	final public static int VALUE_SERVICE=1<<16;
	final public static int VALUE_TEST=1<<17;
	final public static int VALUE_RESET=1<<18;
	
	final public static int BTN_START=1<<8;   
	final public static int BTN_COINS=1<<9;
	
	/*
	final public static int BTN_1 = 0;
	final public static int BTN_2 = 1;
	final public static int BTN_3 = 2;
	final public static int BTN_4 = 3;
	final public static int BTN_5 = 4;
	final public static int BTN_6 = 5;
	final public static int BTN_COINS = 6;
	final public static int BTN_START = 7;
	*/
	
	final public static int STICK_NONE = 0;
	final public static int STICK_UP_LEFT = 1;	
	final public static int STICK_UP = 2;
	final public static int STICK_UP_RIGHT = 3;
	final public static int STICK_LEFT = 4;	
	final public static int STICK_RIGHT = 5;
	final public static int STICK_DOWN_LEFT = 6;	
	final public static int STICK_DOWN = 7;
	final public static int STICK_DOWN_RIGHT = 8;
	
}
