LIBPNG_PATH := $(call my-dir)
LIBPNG := $(subst jni/src/,, $(wildcard $(LIBPNG_PATH)/png.c)) \
	$(subst jni/src/,, $(wildcard $(LIBPNG_PATH)/pngerror.c)) \
	$(subst jni/src/,, $(wildcard $(LIBPNG_PATH)/pngget.c)) \
	$(subst jni/src/,, $(wildcard $(LIBPNG_PATH)/pngmem.c)) \
	$(subst jni/src/,, $(wildcard $(LIBPNG_PATH)/pngpread.c)) \
	$(subst jni/src/,, $(wildcard $(LIBPNG_PATH)/pngread.c)) \
	$(subst jni/src/,, $(wildcard $(LIBPNG_PATH)/pngrio.c)) \
	$(subst jni/src/,, $(wildcard $(LIBPNG_PATH)/pngrtran.c)) \
	$(subst jni/src/,, $(wildcard $(LIBPNG_PATH)/pngrutil.c)) \
	$(subst jni/src/,, $(wildcard $(LIBPNG_PATH)/pngset.c)) \
	$(subst jni/src/,, $(wildcard $(LIBPNG_PATH)/pngtest.c)) \
	$(subst jni/src/,, $(wildcard $(LIBPNG_PATH)/pngtrans.c)) \
	$(subst jni/src/,, $(wildcard $(LIBPNG_PATH)/pngwio.c)) \
	$(subst jni/src/,, $(wildcard $(LIBPNG_PATH)/pngwrite.c)) \
	$(subst jni/src/,, $(wildcard $(LIBPNG_PATH)/pngwtran.c)) \
	$(subst jni/src/,, $(wildcard $(LIBPNG_PATH)/pngwutil.c)) 
	
ifeq ($(TARGET_ARCH),x86)
#LIB7Z += $(subst jni/src/,, $(wildcard $(LIBPNG_PATH)/CpuArch.c))
endif
