BURNER_PATH := $(call my-dir)

BURNER := 	$(subst jni/src/,, \
			burner/unzip.c \
			burner/ioapi.c \
			$(wildcard $(BURNER_PATH)/*.cpp) \
			$(wildcard $(BURNER_PATH)/sdl/*.cpp))





