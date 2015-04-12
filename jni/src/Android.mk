LOCAL_PATH := $(call my-dir)
MY_PATH := $(LOCAL_PATH)

include $(CLEAR_VARS)
LOCAL_PATH := $(MY_PATH)
include $(LOCAL_PATH)/android/Android.include
include $(LOCAL_PATH)/burn/Android.mk
include $(LOCAL_PATH)/burner/Android.mk
include $(LOCAL_PATH)/cpu/Android.mk
include $(LOCAL_PATH)/dep/libs/lib7z/Android.mk
include $(LOCAL_PATH)/dep/libs/libpng/Android.mk

LOCAL_MODULE := arcade
LOCAL_ARM_MODE   := arm

ANDROID_OBJS := android/android.cpp android/android_main.cpp android/android_run.cpp \
		android/android_softfx.cpp android/android_sdlfx.cpp \
		android/android_input.cpp android/android_snd.cpp \
		android/android_stated.cpp

INTF_DIR := $(LOCAL_PATH)/intf
INTF_OBJS := $(wildcard $(INTF_DIR)/*.cpp)

INTF_AUDIO_DIR := $(LOCAL_PATH)/intf/audio
INTF_AUDIO_OBJS := $(wildcard $(INTF_AUDIO_DIR)/*.cpp)

INTF_AUDIO_SDL_DIR := $(LOCAL_PATH)/intf/audio/sdl
INTF_AUDIO_SDL_OBJS := $(wildcard $(INTF_AUDIO_SDL_DIR)/*.cpp)

INTF_INPUT_DIR := $(LOCAL_PATH)/intf/input
INTF_INPUT_OBJS := $(wildcard $(INTF_INPUT_DIR)/*.cpp)

INTF_INPUT_SDL_DIR := $(LOCAL_PATH)/intf/input/sdl
INTF_INPUT_SDL_OBJS := $(wildcard $(INTF_INPUT_SDL_DIR)/*.cpp)

INTF_VIDEO_DIR := $(LOCAL_PATH)/intf/video
INTF_VIDEO_OBJS := $(wildcard $(INTF_VIDEO_DIR)/*.cpp)

INTF_VIDEO_SDL_DIR := $(LOCAL_PATH)/intf/video/sdl
INTF_VIDEO_SDL_OBJS := $(wildcard $(INTF_VIDEO_SDL_DIR)/*.cpp)

LOCAL_SRC_FILES += $(ANDROID_OBJS) \
		$(LIB7Z) $(LIBPNG) $(BURN) $(BURNER) $(CPU) \
		$(subst jni/src/,,$(INTF_OBJS)) \
		$(subst jni/src/,,$(INTF_AUDIO_OBJS)) \
		$(subst jni/src/,,$(INTF_AUDIO_SDL_OBJS)) \
		$(subst jni/src/,,$(INTF_INPUT_OBJS)) \
		$(subst jni/src/,,$(INTF_INPUT_SDL_OBJS)) \
		$(subst jni/src/,,$(INTF_VIDEO_OBJS)) \
		$(subst jni/src/,,$(INTF_VIDEO_SDL_OBJS))

#$(warning $(LOCAL_SRC_FILES)) 
LOCAL_SRC_FILES := $(filter-out intf/input/sdl/inp_sdl.cpp, $(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out intf/video/vid_softfx.cpp, $(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out intf/video/sdl/vid_sdlopengl.cpp, $(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out intf/video/sdl/vid_sdlfx.cpp, $(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out intf/audio/sdl/aud_sdl.cpp, $(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out intf/audio/aud_interface.cpp, $(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out burner/sdl/main.cpp, $(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out burner/sdl/run.cpp, $(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out burner/sdl/stated.cpp, $(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out burn/drv/pgm/pgm_sprite_create.cpp, $(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out cpu/mips3_intf.cpp, $(LOCAL_SRC_FILES))

LOCAL_STATIC_LIBRARIES := minizip
LOCAL_SHARED_LIBRARIES := SDL
LOCAL_LDLIBS := -lGLESv1_CM -llog -lz

include $(BUILD_SHARED_LIBRARY)

