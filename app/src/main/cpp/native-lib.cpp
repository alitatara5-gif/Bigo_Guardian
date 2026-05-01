#include <jni.h>
#include <string>
#include <android/log.h>

extern "C" {
#include <libavutil/avutil.h>
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_bigoguardian_Recorder_getFFmpegVersion(JNIEnv *env, jobject thiz) {
    // Mengambil info versi langsung dari jantung libavutil.so
    const char* version = av_version_info();
    return env->NewStringUTF(version);
}
