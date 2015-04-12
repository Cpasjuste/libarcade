CPU_PATH := $(call my-dir)

CPU := 	$(subst jni/src/,, \
			$(wildcard $(CPU_PATH)/*.cpp) \
			$(wildcard $(CPU_PATH)/arm/*.cpp) \
			$(wildcard $(CPU_PATH)/arm7/*.cpp) \
			$(wildcard $(CPU_PATH)/h6280/*.cpp) \
			$(wildcard $(CPU_PATH)/hd6309/*.cpp) \
			$(wildcard $(CPU_PATH)/i8039/*.cpp) \
			$(wildcard $(CPU_PATH)/konami/*.cpp) \
			$(wildcard $(CPU_PATH)/m68k/m68kcpu.c) \
			$(wildcard $(CPU_PATH)/m68k/m68kopac.c) \
			$(wildcard $(CPU_PATH)/m68k/m68kopdm.c) \
			$(wildcard $(CPU_PATH)/m68k/m68kopnz.c) \
			$(wildcard $(CPU_PATH)/m68k/m68kops.c) \
			$(wildcard $(CPU_PATH)/m6502/*.cpp) \
			$(wildcard $(CPU_PATH)/m6800/*.cpp) \
			$(wildcard $(CPU_PATH)/m6805/*.cpp) \
			$(wildcard $(CPU_PATH)/m6809/*.cpp) \
			$(wildcard $(CPU_PATH)/nec/*.cpp) \
			$(wildcard $(CPU_PATH)/s2650/*.cpp) \
			$(wildcard $(CPU_PATH)/sh2/*.cpp) \
			$(wildcard $(CPU_PATH)/z80/*.cpp) \
			$(wildcard $(CPU_PATH)/tlcs90/*.cpp) \
			$(wildcard $(CPU_PATH)/pic16c5x/*.cpp))





