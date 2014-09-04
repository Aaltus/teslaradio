#pragma once

#include <android/log.h>
#include <stdarg.h>

// Utility for logging:
#define LOG_TAG    "VuforiaNative"

#define LOG_NONE    0
#define LOG_INFO    1
#define LOG_WARNING 2
#define LOG_ERROR   3
#define LOG_DEBUG   4
#define LOG_ALL     5

#define AALTUS_NBR_TARGET 5

static int logLevel = LOG_NONE;

inline void LOGI(const char* text ...)
{
    va_list va_args;
    va_start( va_args, text );

    if (logLevel >= LOG_INFO) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, text, va_args);

    va_end(va_args);
}

inline void LOGW(const char* text ...)
{
    va_list va_args;
    va_start( va_args, text );

    if (logLevel >= LOG_WARNING) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, text, va_args);

    va_end(va_args);
}
inline void LOGE(const char* text ...)
{
    va_list va_args;
    va_start( va_args, text );

    if (logLevel >= LOG_ERROR) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, text, va_args);

    va_end(va_args);
}
inline void LOGD(const char* text ...)
{
    va_list va_args;
    va_start( va_args, text );

    if (logLevel >= LOG_DEBUG) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, text, va_args);

    va_end(va_args);
}