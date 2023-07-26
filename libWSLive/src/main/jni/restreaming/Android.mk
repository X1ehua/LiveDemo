LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)


LOCAL_SRC_FILES := restreaming.c \
				   colorConvert.c 

LOCAL_C_INCLUDES :=$(LOCAL_PATH)/

LOCAL_CFLAGS += -Wno-pointer-sign \
				-Wno-incompatible-pointer-types \
				-Wno-implicit-function-declaration \
				-Wno-int-conversion

LOCAL_MODULE := librestreaming

LOCAL_LDLIBS := -llog -ljnigraphics -landroid

include $(BUILD_SHARED_LIBRARY)
