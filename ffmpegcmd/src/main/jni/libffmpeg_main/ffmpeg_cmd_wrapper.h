#ifndef FFMPEG_BUILD_LIB_FFMPEG_MAIN_H
#define FFMPEG_BUILD_LIB_FFMPEG_MAIN_H

#include"jni.h"

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     com_brian_recorder_jni_FFmpegCmd
 * Method:    run
 * Signature: ([Ljava/lang/String;)I
 */

JNIEXPORT jint
JNICALL Java_com_brian_recorder_jni_FFmpegCmd_run
        (JNIEnv *env, jclass obj, jobjectArray commands);

#ifdef __cplusplus
}
#endif

#endif //FFMPEG_BUILD_LIB_FFMPEG_MAIN_H
