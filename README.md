# 🛡️ Bigo Guardian POSIX (Native JNI Edition)

![FFmpeg](https://img.shields.io/badge/FFmpeg-8.0.1-blue?logo=ffmpeg&logoColor=white)
![NDK](https://img.shields.io/badge/NDK-r28c-orange)
![Platform](https://img.shields.io/badge/Platform-Android_ARM64-green?logo=android)

**Bigo Guardian POSIX** adalah solusi perekaman stream Bigo Live tingkat tinggi yang dibangun di atas arsitektur **Native JNI**. Dengan menggunakan **FFmpeg 8.0.1 Shared Library (.so)**, aplikasi ini mampu melewati batasan keamanan Android 13+ (W^X) secara legal, memberikan performa perekaman yang stabil dan tahan banting (*24/7 stability*).

---

## ✨ Fitur Unggulan

* **Native JNI Engine:** FFmpeg berjalan langsung di dalam memori proses aplikasi melalui jembatan C++, bukan lagi memanggil biner eksternal.
* **FFmpeg 8.0.1 (Latest 2026):** Versi paling mutakhir yang di-build khusus menggunakan Android NDK r28c untuk stabilitas maksimal.
* **Ultra Lightweight:** Library `libffmpeg.so` telah di-strip secara optimal dari **74MB** menjadi hanya **15MB** tanpa mengurangi fitur esensial.
* **Anti-Blocking Architecture:** Berjalan sebagai *Shared Library* (.so), menjadikannya "halal" di mata sistem keamanan Android 10 hingga Android 14+.
* **Automated Pipeline:** Terintegrasi dengan **GitHub Actions** untuk proses kompilasi NDK dan pembuatan APK secara otomatis.

---

## 📂 Struktur Repositori

Pastikan file diletakkan sesuai folder berikut agar **GitHub Actions** dan **CMake** bisa merakit APK dengan benar:

```text
.
├── .github/workflows/
│   └── android-build.yml       # Automasi Build APK via GitHub Actions
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── cpp/            # Jantung Native C++
│   │   │   │   ├── include/    # Header FFmpeg 8.0.1 (.h files)
│   │   │   │   ├── native-lib.cpp
│   │   │   │   └── CMakeLists.txt
│   │   │   ├── jniLibs/        # Biner Shared Library (.so)
│   │   │   │   └── arm64-v8a/
│   │   │   │       ├── libffmpeg.so
│   │   │   │       └── libc++_shared.so
│   │   │   └── AndroidManifest.xml
│   └── build.gradle            # Konfigurasi App & JNI
└── build.gradle                # Project-level build file
