LIB7Z_PATH := $(call my-dir)
LIB7Z := $(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/7zAlloc.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/7zBuf2.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/7zBuf.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/7zCrc.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/7zCrcOpt.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/7zDecode.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/7zDec.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/7zExtract.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/7zFile.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/7zHeader.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/7zIn.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/7zItem.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/7zStream.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/Bcj2.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/Bra86.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/BraIA64.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/Bra.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/LzFind.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/LzmaDec.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/lzma.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/Ppmd7.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/Lzma2Dec.c)) \
	$(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/Ppmd7Dec.c))
	
ifeq ($(TARGET_ARCH),x86)
#LIB7Z += $(subst jni/src/,, $(wildcard $(LIB7Z_PATH)/CpuArch.c))
endif
