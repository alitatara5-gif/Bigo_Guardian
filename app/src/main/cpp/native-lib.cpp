#include <jni.h>
#include <string>
#include <vector>
#include <android/log.h>

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavutil/avutil.h>
// Jika libffmpeg.so abang punya fungsi main ffmpeg, kita bisa panggil di sini
}

#define LOG_TAG "BigoGuardianNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_bigo_posix_MainActivity_stringFromJNI(JNIEnv* env, jobject /* this */) {
    // Cek versi ffmpeg untuk memastikan library benar-benar terbaca
    std::string info = "Engine POSIX Ready | FFmpeg v";
    info += av_version_info(); 
    return env->NewStringUTF(info.c_str());
}

// Fungsi inilah yang nanti akan melakukan tugas berat (rekam)
extern "C" JNIEXPORT jint JNICALL
Java_com_bigo_posix_MainActivity_startRecording(JNIEnv* env, jobject /* this */, jstring url, jstring output) {
    const char *nativeUrl = env->GetStringUTFChars(url, 0);
    const char *nativeOutput = env->GetStringUTFChars(output, 0);

    LOGI("Mencoba merekam dari: %s", nativeUrl);
    LOGI("Hasil akan disimpan ke: %s", nativeOutput);

    // Di sini kita akan memasukkan logika avformat_open_input 
    // untuk mulai menyedot stream Bigo.
    
    env->ReleaseStringUTFChars(url, nativeUrl);
    env->ReleaseStringUTFChars(output, nativeOutput);
    return 0; // Return 0 jika sukses
}
