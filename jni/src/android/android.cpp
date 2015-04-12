#include <jni.h>
#include <android/log.h>
#include "android.h"
#include <SDL.h>

extern void SDL_Android_Init(JNIEnv* env, jclass cls);
static jclass pActivityClass;
static JNIEnv* pEnv = NULL;
jmethodID showBar;
jmethodID hideBar;
jmethodID setBar;
jmethodID setError;

jmethodID JNIgetRomsPath;
jmethodID JNIgetCachePath;
jmethodID JNIgetDataPath;
const char* rom_path;
const char* data_path;
const char* cache_path;

extern int StatedLoad(int nSlot);
extern int StatedSave(int nSlot);

extern "C"
{
	void Java_fr_mydedibox_libarcade_emulator_sdl_SDLJni_nativeInitWithArgs(JNIEnv* env, jclass cls, jobjectArray strArray);
	void Java_fr_mydedibox_libarcade_emulator_sdl_SDLJni_setfskip( JNIEnv *env, jobject thiz, jint n );
	void Java_fr_mydedibox_libarcade_emulator_sdl_SDLJni_emustop( JNIEnv *env, jobject thiz );
	jint Java_fr_mydedibox_libarcade_emulator_sdl_SDLJni_ispaused( JNIEnv *env, jobject thiz );
	void Java_fr_mydedibox_libarcade_emulator_sdl_SDLJni_pauseemu( JNIEnv *env, jobject thiz );
	void Java_fr_mydedibox_libarcade_emulator_sdl_SDLJni_resumeemu( JNIEnv *env, jobject thiz );
	jint Java_fr_mydedibox_libarcade_emulator_sdl_SDLJni_getslotnum( JNIEnv *env, jobject thiz );
	void Java_fr_mydedibox_libarcade_emulator_sdl_SDLJni_statesave( JNIEnv *env, jobject thiz, jint statenum );
	void Java_fr_mydedibox_libarcade_emulator_sdl_SDLJni_stateload( JNIEnv *env, jobject thiz, jint statenum );
	void Java_fr_mydedibox_libarcade_emulator_sdl_SDLJni_setPadData( JNIEnv *env, jobject thiz, jint i, jlong jl );
}

void Java_fr_mydedibox_libarcade_emulator_sdl_SDLJni_nativeInitWithArgs(JNIEnv* env, jclass cls, jobjectArray strArray)
{
	int status, i;
	
	SDL_Android_Init(env, cls);

	pEnv = env;
	pActivityClass = (jclass)env->NewGlobalRef(cls);

	JNIgetRomsPath = env->GetStaticMethodID( pActivityClass, "getRomsPath","()Ljava/lang/String;" );
	jstring rompath = (jstring)env->CallStaticObjectMethod( pActivityClass, JNIgetRomsPath );
        rom_path = env->GetStringUTFChars( rompath, 0 );

	JNIgetDataPath = env->GetStaticMethodID( pActivityClass, "getDataPath","()Ljava/lang/String;" );
	jstring datapath = (jstring)env->CallStaticObjectMethod( pActivityClass, JNIgetDataPath );
        data_path = env->GetStringUTFChars( datapath, 0 );

	showBar = env->GetStaticMethodID( pActivityClass, "showProgressBar","(Ljava/lang/String;I)V" );
	hideBar = env->GetStaticMethodID( pActivityClass, "hideProgressBar","()V" );
	setBar = env->GetStaticMethodID( pActivityClass,"setProgressBar","(Ljava/lang/String;I)V" );
	setError = env->GetStaticMethodID( pActivityClass, "setErrorMessage","(Ljava/lang/String;)V" );

	jsize len = env->GetArrayLength( strArray );
	const char *argv[len];
	argv[0] = strdup( "aFBA" );

	for( i=0; i<len; i++ )
	{
		jstring str = (jstring)env->GetObjectArrayElement(strArray,i);
		argv[i+1] = env->GetStringUTFChars( str, 0 );
	}

	android_pause = 0;
	android_quit = 0;
	android_fskip = 0;

	status = SDL_main(i+1, (char **)argv);
}

const char *getRomsPath()
{
	return rom_path;
}

const char *getDataPath()
{
	return data_path;
}

void Java_fr_mydedibox_libarcade_emulator_sdl_SDLJni_setfskip( JNIEnv *env, jobject thiz, jint n )
{
	android_fskip = n;
}


void Java_fr_mydedibox_libarcade_emulator_sdl_SDLJni_emustop( JNIEnv *env, jobject thiz )
{
	android_quit = 1;
}

jint Java_fr_mydedibox_libarcade_emulator_sdl_SDLJni_ispaused( JNIEnv *env, jobject thiz )
{
	return android_pause;
}

void Java_fr_mydedibox_libarcade_emulator_sdl_SDLJni_pauseemu( JNIEnv *env, jobject thiz )
{
	android_pause = 1;
}

void Java_fr_mydedibox_libarcade_emulator_sdl_SDLJni_resumeemu( JNIEnv *env, jobject thiz )
{
	android_pause = 0;
}

jint Java_fr_mydedibox_libarcade_emulator_sdl_SDLJni_getslotnum( JNIEnv *env, jobject thiz )
{
	return 0;
}

void Java_fr_mydedibox_libarcade_emulator_sdl_SDLJni_statesave( JNIEnv *env, jobject thiz, jint statenum )
{
	StatedSave( statenum );
}

void Java_fr_mydedibox_libarcade_emulator_sdl_SDLJni_stateload( JNIEnv *env, jobject thiz, jint statenum )
{
	StatedLoad( statenum );
}

void Java_fr_mydedibox_libarcade_emulator_sdl_SDLJni_setPadData( JNIEnv *env, jobject thiz, jint i, jlong jl )
{
	unsigned long l = (unsigned long)jl;

	android_pad_test = (l & ANDROID_TEST);
	android_pad_service = (l & ANDROID_SERVICE);
	android_pad_reset = (l & ANDROID_RESET);

	android_pad_coins = (l & ANDROID_COINS);
	android_pad_start = (l & ANDROID_START);

	android_pad_up = (l & ANDROID_UP);
	android_pad_down = (l & ANDROID_DOWN);
	android_pad_left = (l & ANDROID_LEFT);
	android_pad_right = (l & ANDROID_RIGHT);
	android_pad_1 = (l & ANDROID_1);
	android_pad_2 = (l & ANDROID_2);
	android_pad_3 = (l & ANDROID_3);
	android_pad_4 = (l & ANDROID_4);
	android_pad_5 = (l & ANDROID_5);
	android_pad_6 = (l & ANDROID_6);
}

void setErrorMsg( char *msg )
{
	if( setError )
	{
		pEnv->CallStaticVoidMethod( pActivityClass, setError, pEnv->NewStringUTF(msg) );
	}
}

void progressBarShow(char *name, int size)
{
	if(showBar)
	{
		pEnv->CallStaticVoidMethod( pActivityClass, showBar, pEnv->NewStringUTF(name), size );
	}
}

void progressBarUpdate(char *msg, int pos)
{
	if (setBar) 
	{
		pEnv->CallStaticVoidMethod( pActivityClass, setBar, pEnv->NewStringUTF(msg), pos );
	}
}

void progressBarHide(void)
{
	if(hideBar)
	{
		pEnv->CallStaticVoidMethod( pActivityClass, hideBar);
	}
}

