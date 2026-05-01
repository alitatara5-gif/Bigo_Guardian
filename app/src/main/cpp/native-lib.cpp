#include <jni.h>
#include <string>
#include <vector>

// Mengimpor header FFmpeg yang abang push tadi
extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavutil/avutil.h>
}

// Fungsi dummy untuk tes koneksi JNI
extern "C" JNIEXPORT jstring JNICALL
Java_com_bigo_posix_MainActivity_stringFromJNI(JNIEnv* env, jobject /* this */) {
    std::string info = "Engine POSIX Ready | FFmpeg v8.0.1";
    return env->NewStringUTF(info.c_str());
}

// FUNGSI UTAMA: Menjalankan perintah rekaman
// Nanti di Java tinggal panggil: runFFmpeg(new String[]{"ffmpeg", "-i", "url", ...})
extern "C" JNIEXPORT jint JNICALL
Java_com_bigo_posix_MainActivity_runFFmpeg(JNIEnv* env, jobject /* this */, jobjectArray cmdArray) {
    // Di sini nanti kita bisa memanggil fungsi main ffmpeg 
    // atau menggunakan library wrapper seperti FFmpegKit.
    // Untuk tahap awal, kita pastikan JNI terhubung dulu.
    return 0; 
}
