BURN_PATH := $(call my-dir)

BURN := 	$(subst jni/src/,, \
			$(wildcard $(BURN_PATH)/*.cpp) \
			$(wildcard $(BURN_PATH)/devices/*.cpp) \
			$(wildcard $(BURN_PATH)/snd/*.c*) \
			$(wildcard $(BURN_PATH)/drv/*.cpp) \
			$(wildcard $(BURN_PATH)/drv/capcom/*.cpp) \
			$(wildcard $(BURN_PATH)/drv/cave/*.cpp) \
			$(wildcard $(BURN_PATH)/drv/cps3/*.cpp) \
			$(wildcard $(BURN_PATH)/drv/dataeast/*.cpp) \
			$(wildcard $(BURN_PATH)/drv/galaxian/*.cpp) \
			$(wildcard $(BURN_PATH)/drv/irem/*.cpp) \
			$(wildcard $(BURN_PATH)/drv/konami/*.cpp) \
			$(wildcard $(BURN_PATH)/drv/megadrive/*.cpp) \
			$(wildcard $(BURN_PATH)/drv/neogeo/*.cpp) \
			$(wildcard $(BURN_PATH)/drv/pce/*.cpp) \
			$(wildcard $(BURN_PATH)/drv/pgm/*.cpp) \
			$(wildcard $(BURN_PATH)/drv/pre90s/*.cpp) \
			$(wildcard $(BURN_PATH)/drv/psikyo/*.cpp) \
			$(wildcard $(BURN_PATH)/drv/pst90s/*.cpp) \
			$(wildcard $(BURN_PATH)/drv/sega/*.cpp) \
			$(wildcard $(BURN_PATH)/drv/snes/*.cpp) \
			$(wildcard $(BURN_PATH)/drv/taito/*.cpp) \
			$(wildcard $(BURN_PATH)/drv/toaplan/*.cpp) \
			$(wildcard $(BURN_PATH)/drv/coleco/*.cpp) \
			$(wildcard $(BURN_PATH)/drv/sg1000/*.cpp) \
			$(wildcard $(BURN_PATH)/drv/sms/*.cpp))


