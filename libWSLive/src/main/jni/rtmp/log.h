#ifndef __LEUDLOG_H__
#define __LEUDLOG_H__

#define ENABLE_LOG 1

#ifdef __ANDROID__

#include <android/log.h>
#define LOG_TAG "RESRTMP"

#ifdef ENABLE_LOG
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,  LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#else
#define LOGD(...)
#define LOGW(...)
#define LOGE(...)
#endif

#else
#include <stdio.h>

#ifdef ENABLE_LOG
#define LOGD(...) printf(__VA_ARGS__)
#else
#define LOGD(...)
#endif

#endif

#endif