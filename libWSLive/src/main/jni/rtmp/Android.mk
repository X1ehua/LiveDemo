LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := $(wildcard $(LOCAL_PATH)/librtmp/*.c)
LOCAL_SRC_FILES += libresrtmp.c

LOCAL_C_INCLUDES := $(LOCAL_PATH)/librtmp
LOCAL_C_INCLUDES += libresrtmp.h
LOCAL_C_INCLUDES += log.h

LOCAL_CFLAGS += -DNO_CRYPTO

LOCAL_CFLAGS += -Wno-pointer-sign \
				-Wno-incompatible-pointer-types \
				-Wno-implicit-function-declaration \
				-Wno-int-conversion

LOCAL_MODULE := libresrtmp

LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)
